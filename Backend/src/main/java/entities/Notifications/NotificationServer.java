package entities.Notifications;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint("/notifications")
@Component
public class NotificationServer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServer.class);
    private static final CopyOnWriteArrayList<Session> sessions = new CopyOnWriteArrayList<>();

    @OnOpen
    public void onOpen(Session session) {
        logger.info("New connection: {}", session.getId());
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        logger.info("Connection closed: {}", session.getId());
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("Error on connection {}: {}", session.getId(), throwable.getMessage());
    }

    private static void broadcastWithTimestamp(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String messageWithTimestamp = "[" + timestamp + "] " + message;
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendText(messageWithTimestamp);
            } catch (IOException e) {
                logger.error("Failed to send message to session {}: {}", session.getId(), e.getMessage());
            }
        }
    }

    public static void sendNewRecipeNotification(String rname) {
        broadcastWithTimestamp(rname);
    }
}
