#!/usr/bin/env bash
name=tripstore-trips-service
docker rmi "$name"
docker build . -t "$name"