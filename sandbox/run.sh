#!/bin/bash

docker network create elastic
docker network create kafka

docker volume create es01
docker volume create kafka01

docker compose -f elk/docker-compose.yml up -d elasticsearch
docker compose -f kafka/docker-compose.yml up -d