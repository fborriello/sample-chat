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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

/**
 * The client handler task.
 */
class ClientHandler implements Runnable {
    /**
     * All connected client names, this is required to check for duplicated nicknames.
     */
    protected static final List<String> CLIENT_NAMES = Collections.synchronizedList(new ArrayList<>());

    /**
     * All the print writers for all the clients, used for broadcast.
     */
    protected static final Set<PrintWriter> CLINT_WRITERS = new HashSet<>();

    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());

    private final Socket socket;

    /**
     * Constructs a handler thread, squirreling away the socket.
     * @param socket the socket.
     */
    ClientHandler(final Socket socket) {
        this.socket = socket;
    }

    /**
     * Gets the Socket inputStream from which reads the messages and
     * use it for handling them.
     */
    public void run() {
        try (var in = new Scanner(socket.getInputStream())) {
            handleMessages(in);
        } catch (IOException e) {
            LOGGER.log(SEVERE, "An error occurred.", e);
        }
    }

    /**
     * Requests the client nickname and broadcast all messages sent by a client.
     * @param in the client input stream
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
        String nickName;
        while (true) {
            out.println(NICK_NAME_SUBMIT.getValue());
            nickName = in.nextLine();
            if (nickName != null && !nickName.isBlank()) {
                if (!CLIENT_NAMES.contains(nickName.toLowerCase())) {
                    out.println(NICK_NAME_ACCEPTED.getValue() + " " + nickName);
                    notifyClientAlreadyIn(nickName, out);
                    CLIENT_NAMES.add(nickName);
                    sendAll(format(SOMEONE_JOINED.getValue(), nickName));
                    CLINT_WRITERS.add(out);
                    break;
                } else {
                    out.println(MESSAGE.getValue() + " " + NICK_NAME_EXISTING.getValue());
                }
            }
        }
        return nickName;
    }

    /**
     * Informs the new client on all other people already in.
     * @param nickName the user nick name.
     * @param out the new client output.
     */
    protected void notifyClientAlreadyIn(final String nickName, final PrintWriter out) {
        String message = CLIENT_NAMES.isEmpty() ? format(NO_ONE_ONLINE.getValue(), nickName) : format(PEOPLE_ALREADY_IN.getValue(), String.join(",", CLIENT_NAMES));
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
