package org.nsu.fit.golenko_dmitriy.tdc.model.client;

class API {

    private static final String SCHEMA = "http";
    private static final String DOMAIN = "localhost";
    private static final Integer PORT = 8092;
    @SuppressWarnings("ConstantValue")
    private static final String ROOT = SCHEMA + "://" + DOMAIN + (PORT == null ? "" : ":" + PORT);


    private static String getDestination(String relativePath) {
        return ROOT + relativePath;
    }

    public static class AUTH {
        private static final String LOGIN = "/auth/authorize";
        private static final String REGISTER = "/auth/register";

        public static String getLogin() {
            return getDestination(LOGIN);
        }

        public static String getRegister() {
            return getDestination(REGISTER);
        }
    }

    public static class STOMP {

        private static final String REGISTRY = "/websocket";

        public static String getRegistry() {
            return getDestination(REGISTRY);
        }
    }

    public static class USER {

        private static final String GET_FRIENDS = "/user/friends/get";
        private static final String GET_FRIENDS_ONLINE = "/user/friends/get/online";
        private static final String CREATE_LOBBY = "/user/lobby/create";
        private static final String FETCH_INVITE_LOBBY = "/user/topic/lobby.invitation";
        private static final String API_INVITE_FRIEND = "/api/lobby.invite.friend";
        private static final String API_INVITE_ACCEPT = "/user/lobby/accept";

        public static String getGetFriends() {
            return getDestination(GET_FRIENDS);
        }

        public static String getGetFriendsOnline() {
            return getDestination(GET_FRIENDS_ONLINE);
        }

        public static String getCreateLobby() {
            return getDestination(CREATE_LOBBY);
        }

        public static String getFetchInviteLobby() {
            return FETCH_INVITE_LOBBY;
        }

        public static String getApiInviteFriend() {
            return getDestination(API_INVITE_FRIEND);
        }

        public static String getApiInviteAccept() {
            return API_INVITE_ACCEPT;
        }
    }

    public static class GAME {

        private static final String TOPIC_FIELD_RECEIVED = "/topic/game.field.{lobby_id}.received";
        private static final String API_GAME_START = "/api/game.start";
        private static final String API_TOWER_CREATE = "/api/game.create.tower";
        private static final String TOPIC_GAME_START = "/topic/game.{lobby_id}.start";

        public static String getTopicFieldReceived(String lobbyId) {
            return TOPIC_FIELD_RECEIVED.replace("{lobby_id}", lobbyId);
        }

        public static String getApiGameStart() {
            return API_GAME_START;
        }

        public static String getApiTowerCreate() {
            return API_TOWER_CREATE;
        }

        public static String getTopicGameStart(String lobbyId) {
            return TOPIC_GAME_START.replace("{lobby_id}", lobbyId);
        }
    }
}

