package org.nsu.fit.golenko_dmitriy.tdc.model.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nsu.fit.golenko_dmitriy.tdc.exception.AuthException;
import org.nsu.fit.golenko_dmitriy.tdc.exception.RegistrationException;
import org.nsu.fit.golenko_dmitriy.tdc.exception.WebClientException;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.handler.AfterConnectedListener;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.handler.HandleExceptionListener;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.handler.HandleTransportErrorListener;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.OnDisconnectedListener;
import org.nsu.fit.golenko_dmitriy.tdc.presenter.OnExceptionListener;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.handler.SimpleStompSessionHandler;
import org.nsu.fit.golenko_dmitriy.tdc.model.client.model.ExceptionContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@Log4j2
public class WebClient implements Client {
    @Getter
    private final Rest rest;
    @Getter
    private final Stomp stomp;

    public static WebClient registration(String username, String password, OnExceptionListener exceptionListener,
            OnDisconnectedListener disconnectedListener) throws RegistrationException {
        return new WebClient(getRegistration(username, password), exceptionListener, disconnectedListener);
    }

    public static WebClient authentication(String username, String password, OnExceptionListener exceptionListener,
            OnDisconnectedListener disconnectedListener) throws AuthException {
        return new WebClient(getAuthentication(username, password), exceptionListener, disconnectedListener);
    }


    private WebClient(String token, OnExceptionListener exceptionListener, OnDisconnectedListener disconnectedListener)
            throws AuthException {
        this.stomp = new Stomp(token, exceptionListener, disconnectedListener);
        this.rest = new Rest(token);
    }

    private static String getRegistration(String username, String password) throws AuthException, RegistrationException {
        try {
            String registered = register(username, password);
            log.info("register() : " + registered);
        } catch (AuthException e) {
            throw new RegistrationException(e);
        }
        try {
            String token = authorize(username, password);
            log.info("authorize() : " + token.substring(token.length() / 2) + "...(hidden)");
            return token;
        } catch (AuthException e) {
            throw new AuthException(e);
        }
    }

    private static String getAuthentication(String username, String password) throws AuthException, RegistrationException {
        try {
            String token = authorize(username, password);
            log.info("authorize() : " + token.substring(token.length() / 2) + "...(hidden)");
            return token;
        } catch (AuthException e) {
            throw new AuthException(e);
        }
    }



    private static String register(String username, String password) throws RegistrationException {
        JsonObject request = new JsonObject();
        request.addProperty("username", username);
        request.addProperty("password", password);
        Result pair;
        try {
            pair = Rest.sendTo(API.AUTH.getRegister(), request, HttpMethod.POST);
        } catch (IOException e) {
            throw new RegistrationException(e);
        }
        JsonObject result = pair.response.getAsJsonObject();
        if (pair.code == HttpURLConnection.HTTP_OK) {
            return result.get("message").getAsString();
        } else {
            String message = result.get("message").getAsString();
            throw new RegistrationException(message);
        }
    }

    private static String authorize(String username, String password) throws AuthException {
        JsonObject request = new JsonObject();
        request.addProperty("identifier", username);
        request.addProperty("password", password);

        Result pair;
        try {
            pair = Rest.sendTo(API.AUTH.getLogin(), request, HttpMethod.POST);
        } catch (IOException e) {
            throw new AuthException(e);
        }
        JsonObject result = pair.response.getAsJsonObject();
        if (pair.code == HttpURLConnection.HTTP_OK) {
            return result.get("token").getAsString();
        } else {
            String message = result.get("message").getAsString();
            throw new AuthException(message);
        }
    }

    public void close() {
        rest.close();
        stomp.close();
    }

    public record Result(Integer code, JsonElement response) {}

    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Rest implements Client {
        final String token;

        private Rest(String token) {
            this.token = token;
        }

        private static Result sendTo(String destination, JsonElement payload, HttpMethod method) throws IOException {
            return sendTo(destination, payload, method, null);
        }

        private static Result sendTo(String destination, JsonElement payload,HttpMethod method, String token)
                throws IOException {
            return sendTo(destination, payload == null ? null : payload.toString(), method, token);
        }

    private static @NonNull WebClient.Result sendTo(String destination, String payload, HttpMethod method, String token) throws IOException {
            log.debug("sendTo() : destination=" + destination + ", payload=" + payload + ", method=" + method);
            HttpURLConnection connection = createConnection(destination, method);
            connection.setDoOutput(true);
            if (token != null) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }
            if (payload != null) {
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Content-Length", Integer.toString(payload.getBytes().length));
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(payload.getBytes());
                outputStream.close();
            }
            int responseCode = connection.getResponseCode();
            JsonElement response = getResponse(connection);
            connection.disconnect();

            return new Result(responseCode, response);
        }

        private static JsonElement getResponse(HttpURLConnection connection) throws IOException {
            InputStream inputStream;
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            if (inputStream == null) {
                throw new WebClientException("could not get a response: response is null");
            }
            byte[] bytes = inputStream.readAllBytes();
            String jsonString = new String(bytes);
            return JsonParser.parseString(jsonString);
        }

        private static HttpURLConnection createConnection( String path, HttpMethod method)
                throws IOException {
            HttpURLConnection connection = (HttpURLConnection) new URL(path).openConnection();
            connection.setRequestMethod(method.toString());
            return connection;
        }

        public WebClient.Result send(String destination, String payload, HttpMethod method) throws WebClientException {
            try {
                return sendTo(destination, payload, method, token);
            } catch (IOException e) {
                throw new WebClientException(e);
            }
        }
        @Override
        public void close() {
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Stomp implements Client, AfterConnectedListener, HandleExceptionListener,
            HandleTransportErrorListener {

        final StompSession stompSession;
        final WebSocketStompClient stompClient;
        final ThreadPoolTaskScheduler taskScheduler;
        final OnExceptionListener exceptionListener;
        final OnDisconnectedListener disconnectedListener;

        private Stomp(String token, OnExceptionListener exceptionListener,
                OnDisconnectedListener disconnectedListener) {
            this.taskScheduler = new ThreadPoolTaskScheduler();
            taskScheduler.setPoolSize(4);
            taskScheduler.afterPropertiesSet();
            this.exceptionListener = exceptionListener;
            this.disconnectedListener = disconnectedListener;
            this.stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
            this.stompSession = getStompSession(token, this.stompClient);
            this.stompSession.setAutoReceipt(true);
        }

        private static List<Transport> createTransportClient() {
            List<Transport> transports = new ArrayList<>(1);
            StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
            WebSocketTransport webSocketTransport = new WebSocketTransport(standardWebSocketClient);
            transports.add(webSocketTransport);
            return transports;
        }

        public void send(String destination, Object payload) {
            stompSession.send(destination, payload);
        }

        public StompSession.Subscription subscribe(String destination, StompFrameHandler handler) {
            return stompSession.subscribe(destination, handler);
        }

        public String getSessionId() {
            return stompSession.getSessionId();
        }

        @Override
        public void afterConnected(@NotNull StompSession session, @NotNull StompHeaders connectedHeaders) {
            log.debug("afterConnected() : session=" + session + ", connectedHeaders=" + connectedHeaders);
        }

        @Override
        public void handleException(@NotNull StompSession session, @NotNull StompCommand command,
                @NotNull StompHeaders headers, byte @NotNull [] payload, @NotNull Throwable e) {
            log.error("handleException() : session=" + session + ", command=" + command + ", headers=" + headers
                    + ", payload=" + Arrays.toString(payload) + ", e.getMessage=" + e.getMessage());
            exceptionListener.onException(
                    ExceptionContext.builder()
                            .sessionId(session.getSessionId())
                            .destination(headers.getDestination())
                            .payload(payload)
                            .build(),
                    e
            );
        }

        @Override
        public void handleTransportErrorListener(@NotNull StompSession session, @NotNull Throwable e) {
            log.error("handleTransportErrorListener() : session=" + session + ", e.getMessage()" + e.getMessage());
            disconnectedListener.onDisconnect(
                    ExceptionContext.builder()
                            .sessionId(session.getSessionId())
                            .build(),
                    e
            );
        }

        private StompSession getStompSession(String token, WebSocketStompClient stompClient) throws AuthException {
            stompClient.setTaskScheduler(taskScheduler);
            stompClient.setDefaultHeartbeat(new long[]{0, 0});
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());

            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            WebSocketHttpHeaders httpHeaders = new WebSocketHttpHeaders(new HttpHeaders(headers));
            SimpleStompSessionHandler handler = new SimpleStompSessionHandler(this, this, this);

            CompletableFuture<StompSession> future = stompClient.connectAsync(
                    API.STOMP.getRegistry(), httpHeaders, handler
            );
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new AuthException(e);
            }
        }
        @Override
        public void close() {
            stompSession.disconnect();
            taskScheduler.shutdown();
            stompClient.stop();
        }
    }
}