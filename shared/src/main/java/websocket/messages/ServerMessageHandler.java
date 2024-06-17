package websocket.messages;

public interface ServerMessageHandler {
    void handle(String message);
}
