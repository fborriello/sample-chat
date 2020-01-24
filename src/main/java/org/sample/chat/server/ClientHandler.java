package org.sample.chat.server;

import static java.lang.String.format;
import static java.util.logging.Level.SEVERE;

import static org.sample.chat.config.CommandPrefix.MESSAGE;
import static org.sample.chat.config.CommandPrefix.NICK_NAME_ACCEPTED;
import static org.sample.chat.config.CommandPrefix.NICK_NAME_SUBMIT;
import static org.sample.chat.config.CommandPrefix.EXIT;
import static org.sample.chat.config.NotificationMessages.NICK_NAME_EXISTING;
import static org.sample.chat.config.NotificationMessages.NO_ONE_ONLINE;
import static org.sample.chat.config.NotificationMessages.PEOPLE_ALREADY_IN;
import static org.sample.chat.config.NotificationMessages.SOMEONE_JOINED;
import static org.sample.chat.config.NotificationMessages.SOMEONE_LEFT;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

/**
 * The client handler task.
 */
class ClientHandler extends Thread {
    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());

    /**
     * All connected client names, this is required to check for duplicated nicknames.
     */
    protected static final Set<String> CLIENT_NAMES = new HashSet<>();

    /**
     * All the print writers for all the clients, used for broadcast
     */
    protected static final Set<PrintWriter> CLINT_WRITERS = new HashSet<>();

    private final Socket socket;

    /**
     * Constructs a handler thread, squirreling away the socket.
     */
    public ClientHandler(final Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (var in = new Scanner(socket.getInputStream())) {
            handleMessages(in);
        } catch (IOException e) {
            LOGGER.log(SEVERE, "An error occurred.", e);
        }
    }

    /**
     * Requests the client nickname and broadcast all messages sent by a client.
     */
    protected void handleMessages(final Scanner in) {
        String name = null;
        PrintWriter out = null;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            name = getClientName(out, in);
            while (true) {
                String input = in.nextLine();
                if (input.toLowerCase().startsWith(EXIT.getValue())) {
                    return;
                }
                sendAll(name + ": " + input);
            }
        } catch (Exception e) {
            LOGGER.log(SEVERE, "An error occurred.", e);
        } finally {
            if (out != null) {
                CLINT_WRITERS.remove(out);
            }
            if (name != null) {
                CLIENT_NAMES.remove(name);
                sendAll(format(SOMEONE_LEFT.getValue(), name));
            }
            try {
                socket.close();
            } catch (IOException e) {
                LOGGER.log(SEVERE, "{0}", e.getMessage());
            }
        }
    }

    /**
     * Requests the client name and stores the client output writer.
     * @param out the client output writer.
     * @param in the client input.
     * @return the client name
     */
    protected String getClientName(final PrintWriter out, final Scanner in) {
        String name;
        while (true) {
            out.println(NICK_NAME_SUBMIT.getValue());
            name = in.nextLine();
            if (name != null && !name.isBlank()) {
                synchronized (CLIENT_NAMES) {
                    if (!CLIENT_NAMES.contains(name)) {
                        out.println(NICK_NAME_ACCEPTED.getValue() + " " + name);
                        notifyClientAlreadyIn(out);
                        CLIENT_NAMES.add(name);
                        sendAll(format(SOMEONE_JOINED.getValue(), name));
                        CLINT_WRITERS.add(out);
                        break;
                    } else {
                        out.println(MESSAGE.getValue() + " " + NICK_NAME_EXISTING.getValue());
                    }
                }
            }
        }
        return name;
    }

    /**
     * Informs the new client on all other people already in.
     * @param out the new client output.
     */
    protected void notifyClientAlreadyIn(final PrintWriter out) {
        String message = CLIENT_NAMES.isEmpty() ? NO_ONE_ONLINE.getValue() : format(PEOPLE_ALREADY_IN.getValue(), String.join(",", CLIENT_NAMES));
        out.println(MESSAGE.getValue() + " " + message);
    }

    /**
     * Sends the message to all connected clients.
     * @param message the message to send.
     */
    protected void sendAll(final String message) {
        CLINT_WRITERS.forEach(printWriter -> printWriter.println(MESSAGE.getValue() + " " + message));
    }
}
