package org.sample.chat.server;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;

import static org.sample.chat.config.Settings.MAX_THREADS;
import static org.sample.chat.config.Settings.PORT;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Multithread chat server.
 */
public class ChatServer {
    private static final Logger LOGGER = Logger.getLogger(ChatServer.class.getName());
    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool((Integer) MAX_THREADS.getValue());

    private ServerSocket serverSocket;

    public static void main(String[] args) {
        new ChatServer().startServer();
    }

    public void startServer() {
        LOGGER.log(INFO, "Chat server started and listening on port: {0}", String.valueOf(PORT.getValue()));
        try {
            serverSocket = new ServerSocket((Integer) PORT.getValue());
            while (true) {
                THREAD_POOL.execute(new ClientHandler(serverSocket.accept()));
                if (serverSocket.isClosed()) {
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.log(SEVERE, "{0}", e.getMessage());
        } finally {
            stopServer();
        }
        LOGGER.log(INFO, "Chat server stopped.");
    }

    public void stopServer() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.log(SEVERE, "{0}", e.getMessage());
        }

    }

}