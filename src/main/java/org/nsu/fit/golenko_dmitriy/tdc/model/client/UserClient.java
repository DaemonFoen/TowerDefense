package org.nsu.fit.golenko_dmitriy.tdc.model.client;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nsu.fit.golenko_dmitriy.tdc.exception.WebClientException;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.dto.LobbyDto;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.handler.ConsumableHandler;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.model.Lobby;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.model.User;
import org.nsu.fit.golenko_dmitriy.tdc.model.game.Mapper;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompSession;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserClient implements Client, Consumer<LobbyDto> {

    final WebClient client;
    final StompSession.Subscription invitationSubscription;
    Consumer<Lobby> lobbyInvitationHandler = null;
    Lobby lobby = null;
    String adminStompSessionId;

    public UserClient(WebClient client) {
        this.client = client;

        StompFrameHandler invitationHandler = new ConsumableHandler<>(LobbyDto.class, this);
        this.invitationSubscription = client.getStomp().subscribe(
                API.USER.getFetchInviteLobby(),
                invitationHandler
        );

        CompletableFuture<Boolean> future = new CompletableFuture<>();
        invitationSubscription.addReceiptTask(stompHeaders -> {
            log.debug("UserClient() : confirmation received : stompHeaders.getReceiptId()="
                    + stompHeaders.getReceiptId());
            future.complete(true);
        });
        invitationSubscription.addReceiptLostTask(() -> {
            log.error("UserClient() : could not able to subscribe to " + API.USER.getFetchInviteLobby());
            future.complete(false);
        });

        try {
            Boolean result = future.get();
            if (!result) {
                throw new WebClientException("could not able to subscribe to invitation topic");
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new WebClientException(e);
        }
    }

    @Override
    public void accept(LobbyDto lobbyDto) {
        if (lobbyInvitationHandler == null) {
            return;
        }
        Lobby lobby = Lobby.builder()
                .id(lobbyDto.getId())
                .createdAt(lobbyDto.getCreatedAt())
                .adminSessionId(lobbyDto.getAdminSessionId())
                .members(lobbyDto.getMembers())
                .build();
        lobbyInvitationHandler.accept(lobby);
    }

    public void setLobbyInvitationHandler(Consumer<Lobby> handler) {
        this.lobbyInvitationHandler = handler;
    }

    @SuppressWarnings("unchecked")
    private <T> T getObject(@NotNull String path, @Nullable String payload, @NotNull HttpMethod method) {

        WebClient.Result result = client.getRest().send(path, payload, method);
        if (result.code() == HttpURLConnection.HTTP_OK) {
            return (T) result.response();
        } else if (result.code() == HttpURLConnection.HTTP_BAD_REQUEST) {
            String message = result.response().getAsJsonObject().get("message").getAsString();
            throw new WebClientException(message);
        }
        throw new WebClientException("unexpected result code : " + result.code());
    }

    public List<String> getFriends() {
        JsonArray result = getObject(API.USER.getGetFriends(), null, HttpMethod.GET);
        if (!result.isJsonArray()) {
            throw new WebClientException("unsupported schema response: " + result);
        }
        return result.getAsJsonArray().asList().stream().map(it -> it.getAsJsonObject().get("username").getAsString())
                .toList();
    }

    public List<User> getFriendsStatuses() {
        JsonArray result = getObject(API.USER.getGetFriendsOnline(), null, HttpMethod.GET);
        if (!result.isJsonArray()) {
            throw new WebClientException("unsupported schema response: " + result);
        }
        return result.getAsJsonArray().asList().stream().map(it ->
                User.builder()
                        .status(User.Status.valueOf(it.getAsJsonObject().get("status").getAsString()))
                        .username(it.getAsJsonObject().get("username").getAsString())
                        .websocketSessionId(it.getAsJsonObject().get("sessionId").getAsString())
                        .build()).toList();
    }

    public Lobby createLobby() throws WebClientException {
        try {
            JsonElement lobby = getObject(API.USER.getCreateLobby(), null, HttpMethod.POST);
            LobbyDto lobbyDto = (new Gson()).fromJson(lobby, LobbyDto.class);
            this.lobby = Lobby.builder()
                    .id(lobbyDto.getId())
                    .createdAt(lobbyDto.getCreatedAt())
                    .adminSessionId(lobbyDto.getAdminSessionId())
                    .members(lobbyDto.getMembers())
                    .build();
            this.adminStompSessionId = this.lobby.getAdminSessionId();
            return this.lobby;

        } catch (Exception e) {
            throw new WebClientException(e);
        }
    }

    public void sendLobbyInvitation(User user) {
        client.getStomp().send(API.USER.getApiInviteFriend(), user.getUsername());
    }

    public Lobby getLobby() {
        return this.lobby;
    }

    public boolean acceptLobbyInvitation(Lobby lobby) {
        WebClient.Result accepted = client.getRest()
                .send(API.USER.getApiInviteAccept(), (new Gson()).toJsonTree(Mapper.toLobbyDto(lobby)).toString(),
                        HttpMethod.POST);
        if (accepted.code() != HttpURLConnection.HTTP_OK) {
            return false;
        }
        this.lobby = lobby;
        return accepted.code() == HttpURLConnection.HTTP_OK;
    }

    public String getAdminStompSessionId() {
        return adminStompSessionId;
    }

    @Override
    public void close() {
        invitationSubscription.unsubscribe();
    }
}
