package org.sample.chat.server;

import static org.junit.Assert.assertTrue;
import static org.sample.chat.config.Settings.HOST;
import static org.sample.chat.config.Settings.PORT;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for class: {@link ChatServer}.
 */
public class ChatServerIntegrationTest {
    private ChatServer underTest;

    @Before
    public void before() throws InterruptedException {
        underTest = new ChatServer();
        Executors.newSingleThreadExecutor().submit(() -> underTest.startServer());
        Thread.sleep(500);
    }

    @Test
    public void testStartServer() throws IOException {
        // WHEN
        var socket = new Socket((String) HOST.getValue(), (Integer) PORT.getValue());
        // THEN
        assertTrue(socket.isConnected());
        underTest.stopServer();
    }

    @Test(expected = ConnectException.class)
    public void testStopServer() throws IOException {
        // GIVEN
        underTest.stopServer();
        // WHEN
        new Socket((String) HOST.getValue(), (Integer) PORT.getValue());
    }
}