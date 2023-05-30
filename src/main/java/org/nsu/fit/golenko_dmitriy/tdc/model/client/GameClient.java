package org.nsu.fit.golenko_dmitriy.tdc.model.client;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.nsu.fit.golenko_dmitriy.tdc.exception.HandlerException;
import org.nsu.fit.golenko_dmitriy.tdc.exception.WebClientException;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.handler.ConsistentHandler;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.handler.ConsumableHandler;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.handler.OnFieldUpdateListener;
import org.nsu.fit.golenko_dmitriy.tdc.model.game.Mapper;
import org.nsu.fit.golenko_dmitriy.tdc.model.game.dto.FieldDto;
import org.nsu.fit.golenko_dmitriy.tdc.model.game.dto.GameStartDto;
import org.nsu.fit.golenko_dmitriy.tdc.model.game.dto.TowerCreateDto;
import org.nsu.fit.golenko_dmitriy.tdc.model.game.gameEntities.Entity;
import org.nsu.fit.golenko_dmitriy.tdc.model.game.gameEntities.Field;
import org.springframework.messaging.simp.stomp.StompSession;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GameClient implements Client {
    final WebClient webClient;
    final UserClient userClient;
    OnFieldUpdateListener onFieldUpdate;
    StompSession.Subscription fieldUpdates;
    int roadLength = 0;

    public GameClient(WebClient webClient, UserClient userClient) {
        this.webClient = webClient;
        this.userClient = userClient;
    }

    public void setOnFieldUpdate(OnFieldUpdateListener onFieldUpdate) {
        this.onFieldUpdate = onFieldUpdate;
    }

    public Field start() {
        ConsumableHandler<FieldDto> fieldUpdatesHandler = new ConsumableHandler<>(
                FieldDto.class,
                it -> Platform.runLater(() -> onFieldUpdate.updated(Mapper.toField(it, roadLength)))
        );

        fieldUpdates = webClient.getStomp().subscribe(
                GameDestinationHelper.get(API.GAME.getTopicFieldReceived(userClient.getLobby().getId()), userClient.getLobby().getId()),
                fieldUpdatesHandler
        );

        ConsistentHandler<GameStartDto> gameStartedHandler = new ConsistentHandler<>(GameStartDto.class);

        StompSession.Subscription gameStarted = webClient.getStomp().subscribe(
                API.GAME.getTopicGameStart(userClient.getLobby().getId()),
                gameStartedHandler
        );

        CountDownLatch latch = new CountDownLatch(1);
        gameStarted.addReceiptLostTask(latch::countDown);
        gameStarted.addReceiptTask(latch::countDown);

        try {
            boolean awaited = latch.await(5, TimeUnit.SECONDS);
            if (!awaited) {
                throw new WebClientException("could not subscribe to game start topic");
            }
        } catch (InterruptedException e) {
            throw new WebClientException(e);
        }

        webClient.getStomp().send(API.GAME.getApiGameStart(), "");
        try {
            GameStartDto response = gameStartedHandler.waitAny(50, TimeUnit.SECONDS);
            this.roadLength = response.getLength();
            Field field = Field.builder()
                    .length(this.roadLength)
                    .roads(new HashMap<>())
                    .guildhall(Entity.builder()
                            .id(-1)
                            .name("guildhall")
                            .build())
                    .build();
            gameStarted.unsubscribe();
            return field;
        } catch (HandlerException e) {
            throw new WebClientException(e);
        }
    }

    public void createTower(int position) {
        TowerCreateDto towerCreateDto = new TowerCreateDto(webClient.getStomp().getSessionId(), "DEFAULT_TOWER", position);
        webClient.getStomp().send(API.GAME.getApiTowerCreate(), towerCreateDto);
    }

    public void close() {
        fieldUpdates.unsubscribe();
    }

    private static class GameDestinationHelper {
        private static String get(String destination, String lobbyId) {
            return destination.replace("{lobby_id}", lobbyId);
        }
    }
}
