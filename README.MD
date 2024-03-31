[<img src="https://img.shields.io/badge/dockerhub-images-important.svg?logo=Docker">](https://hub.docker.com/r/mkkl1/canyonbot)
# Canyonbot
Discord music bot built using Spring and Discord4J.
## Description
Canyonbot enables users to play music in Discord channels from various sources by leveraging [Lavalink](https://lavalink.dev/) server.
The bot offers commands that can be used within text channels to control playback.
The project is structured in a flexible manner, allowing for potential expansion into different functionalities in the future.
## Installation
You can install Canyonbot either using a Docker image or the provided artifact in the release. 
Regardless of the version chosen, ensure to specify the required environmental variables, particularly the Discord API token.
### Environmental variables
| Variable          | Description                                                                                                                                            |
|:------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------|
| DISCORD_BOT_TOKEN | Discord api token. If you don't have one, follow [this tutorial](https://docs.discord4j.com/discord-application-tutorial#create-a-discord-application) |
| CONFIG_PATH       | Alternative path to application.properties file                                                                                                        |
### a. Using docker
...
### b. Using docker compose
An example docker-compose.yml:
```yaml
version: "3.8"

services:
  #According to https://lavalink.dev/configuration/docker.html
  lavalink:
    image: ghcr.io/lavalink-devs/lavalink:4
    container_name: lavalink
    restart: unless-stopped
    environment:
      - _JAVA_OPTIONS=-Xmx2G
      - SERVER_PORT=2333
      - LAVALINK_SERVER_PASSWORD=youshallnotpass
    volumes:
      - ./application.yml:/opt/Lavalink/application.yml
      - ./plugins/:/opt/Lavalink/plugins/
    networks:
      - lavalink
    expose:
      - 2333
    ports:
      - "2333:2333"
  canyonbot:
    image: canyonbot:latest
    container_name: canyonbot
    restart: unless-stopped
    environment:
      - _JAVA_OPTIONS=-Xmx2G
      - DISCORD_BOT_TOKEN=<your discord token here>
      - CONFIG_PATH=/opt/canyonbot/application.properties
    volumes:
      - ./application.properties:/opt/canyonbot/application.properties
      - ./logback.xml:/opt/canyonbot/logback.xml
    networks:
      - lavalink
networks:
  lavalink:
    name: lavalink
```
Then run using `docker compose up -d`.
### c. Using jar artifact
...
## Usage
Generate [invite link](https://docs.discord4j.com/discord-application-tutorial#adding-the-bot-to-servers) for bot and use `/play` command to verify that the application is functioning correctly.
## Configuration
To enable the music module, you must provide at least one Lavalink node.
Below is an example application.properties file:
```yaml
player.nodes[0].name=Local Lavalink server
player.nodes[0].uri=ws://localhost:2333
player.nodes[0].password=youshallnotpass
player.nodes[0].httpTimeout=5000
```
If you are using Docker Compose, your URI should be `ws://<lavalink container name>:2333`.
