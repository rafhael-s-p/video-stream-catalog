#!/bin/bash

docker network create elastic

docker volume create es01

docker compose -f elk/docker-compose.yml up -d elasticsearch