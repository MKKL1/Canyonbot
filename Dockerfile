FROM eclipse-temurin:21

RUN groupadd -g 322 canyonbot && \
    useradd -r -u 322 -g canyonbot canyonbot

WORKDIR /opt/canyonbot
RUN chown -R canyonbot:canyonbot /opt/canyonbot
USER canyonbot

COPY /target/canyonbot.jar canyonbot.jar

ENTRYPOINT ["java", "-jar", "canyonbot.jar"]