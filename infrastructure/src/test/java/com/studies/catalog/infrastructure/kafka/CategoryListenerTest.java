package com.studies.catalog.infrastructure.kafka;

import com.studies.catalog.AbstractEmbeddedKafkaTest;
import com.studies.catalog.application.category.delete.DeleteCategoryUseCase;
import com.studies.catalog.application.category.save.SaveCategoryUseCase;
import com.studies.catalog.domain.Fixture;
import com.studies.catalog.infrastructure.category.CategoryGateway;
import com.studies.catalog.infrastructure.category.models.CategoryEvent;
import com.studies.catalog.infrastructure.configuration.json.Json;
import com.studies.catalog.infrastructure.kafka.connect.MessageValue;
import com.studies.catalog.infrastructure.kafka.connect.Operation;
import com.studies.catalog.infrastructure.kafka.connect.ValuePayload;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryListenerTest extends AbstractEmbeddedKafkaTest {

    @MockBean
    private DeleteCategoryUseCase deleteCategoryUseCase;

    @MockBean
    private SaveCategoryUseCase saveCategoryUseCase;

    @MockBean
    private CategoryGateway categoryGateway;

    @SpyBean
    private CategoryListener categoryListener;

    @Value("${kafka.consumers.categories.topics}")
    private String categoryTopic;

    @Captor
    private ArgumentCaptor<ConsumerRecordMetadata> metadata;

    @Test
    public void testCategoriesTopics() throws Exception {
        // given
        final var expectedMainTopic = "adm_videos_mysql.adm_videos.categories";
        final var expectedRetry0Topic = "adm_videos_mysql.adm_videos.categories-retry-0";
        final var expectedRetry1Topic = "adm_videos_mysql.adm_videos.categories-retry-1";
        final var expectedRetry2Topic = "adm_videos_mysql.adm_videos.categories-retry-2";
        final var expectedDLTTopic = "adm_videos_mysql.adm_videos.categories-dlt";

        // when
        final var currentTopics = admin().listTopics().listings().get().stream()
                .map(TopicListing::name)
                .collect(Collectors.toSet());

        // then
        Assertions.assertTrue(currentTopics.contains(expectedMainTopic));
        Assertions.assertTrue(currentTopics.contains(expectedRetry0Topic));
        Assertions.assertTrue(currentTopics.contains(expectedRetry1Topic));
        Assertions.assertTrue(currentTopics.contains(expectedRetry2Topic));
        Assertions.assertTrue(currentTopics.contains(expectedDLTTopic));
    }

    @Test
    public void givenInvalidResponsesFromHandlerShouldRetryUntilGoesToDLT() throws Exception {
        // given
        final var expectedMaxAttempts = 4;
        final var expectedMaxDLTAttempts = 1;
        final var expectedMainTopic = "adm_videos_mysql.adm_videos.categories";
        final var expectedRetry0Topic = "adm_videos_mysql.adm_videos.categories-retry-0";
        final var expectedRetry1Topic = "adm_videos_mysql.adm_videos.categories-retry-1";
        final var expectedRetry2Topic = "adm_videos_mysql.adm_videos.categories-retry-2";
        final var expectedDLTTopic = "adm_videos_mysql.adm_videos.categories-dlt";

        final var movies = Fixture.Categories.movies();
        final var moviesEvent = new CategoryEvent(movies.id());

        final var message =
                Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(moviesEvent, moviesEvent, aSource(), Operation.DELETE)));

        final var latch = new CountDownLatch(5);

        doAnswer(t -> {
            latch.countDown();
            if (latch.getCount() > 0) {
                throw new RuntimeException("BOOM!");
            }
            return null;
        }).when(deleteCategoryUseCase).execute(any());

        // when
        producer().send(new ProducerRecord<>(categoryTopic, message));
        producer().flush();

        Assertions.assertTrue(latch.await(1, TimeUnit.MINUTES));

        // then
        verify(categoryListener, times(expectedMaxAttempts)).onMessage(eq(message), metadata.capture());

        final var allMetas = metadata.getAllValues();
        Assertions.assertEquals(expectedMainTopic, allMetas.get(0).topic());
        Assertions.assertEquals(expectedRetry0Topic, allMetas.get(1).topic());
        Assertions.assertEquals(expectedRetry1Topic, allMetas.get(2).topic());
        Assertions.assertEquals(expectedRetry2Topic, allMetas.get(3).topic());

        verify(categoryListener, times(expectedMaxDLTAttempts)).onDLTMessage(eq(message), metadata.capture());

        Assertions.assertEquals(expectedDLTTopic, metadata.getValue().topic());
    }

    @Test
    public void givenUpdateOperationWhenProcessGoesOKShouldEndTheOperation() throws Exception {
        // given
        final var movies = Fixture.Categories.movies();
        final var moviesEvent = new CategoryEvent(movies.id());

        final var message =
                Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(moviesEvent, moviesEvent, aSource(), Operation.UPDATE)));

        final var latch = new CountDownLatch(1);

        doAnswer(t -> {
            latch.countDown();
            return movies;
        }).when(saveCategoryUseCase).execute(any());

        doReturn(Optional.of(movies)).when(categoryGateway).categoryOfId(any());

        // when
        producer().send(new ProducerRecord<>(categoryTopic, message));
        producer().flush();

        Assertions.assertTrue(latch.await(1, TimeUnit.MINUTES));

        // then
        verify(categoryGateway, times(1)).categoryOfId(eq(movies.id()));

        verify(saveCategoryUseCase, times(1)).execute(eq(movies));
    }

    @Test
    public void givenCreateOperationWhenProcessGoesOKShouldEndTheOperation() throws Exception {
        // given
        final var movies = Fixture.Categories.movies();
        final var moviesEvent = new CategoryEvent(movies.id());

        final var message =
                Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(moviesEvent, moviesEvent, aSource(), Operation.CREATE)));

        final var latch = new CountDownLatch(1);

        doAnswer(t -> {
            latch.countDown();
            return movies;
        }).when(saveCategoryUseCase).execute(any());

        doReturn(Optional.of(movies)).when(categoryGateway).categoryOfId(any());

        // when
        producer().send(new ProducerRecord<>(categoryTopic, message));
        producer().flush();

        Assertions.assertTrue(latch.await(1, TimeUnit.MINUTES));

        // then
        verify(categoryGateway, times(1)).categoryOfId(eq(movies.id()));

        verify(saveCategoryUseCase, times(1)).execute(eq(movies));
    }

    @Test
    public void givenDeleteOperationWhenProcessGoesOKShouldEndTheOperation() throws Exception {
        // given
        final var movies = Fixture.Categories.movies();
        final var moviesEvent = new CategoryEvent(movies.id());

        final var message =
                Json.writeValueAsString(new MessageValue<>(new ValuePayload<>(moviesEvent, moviesEvent, aSource(), Operation.DELETE)));

        final var latch = new CountDownLatch(1);

        doAnswer(t -> {
            latch.countDown();
            return null;
        }).when(deleteCategoryUseCase).execute(any());

        // when
        producer().send(new ProducerRecord<>(categoryTopic, message));
        producer().flush();

        Assertions.assertTrue(latch.await(1, TimeUnit.MINUTES));

        // then
        verify(deleteCategoryUseCase, times(1)).execute(eq(movies.id()));
    }
}