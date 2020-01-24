package org.sample.chat.client;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import static javax.swing.JOptionPane.PLAIN_MESSAGE;

import static org.sample.chat.config.CommandPrefix.MESSAGE;
import static org.sample.chat.config.CommandPrefix.NICK_NAME_ACCEPTED;
import static org.sample.chat.config.CommandPrefix.NICK_NAME_SUBMIT;
import static org.sample.chat.config.NotificationMessages.CHAT_TITLE;
import static org.sample.chat.config.NotificationMessages.CHOOSE_NICK_NAME;
import static org.sample.chat.config.NotificationMessages.NICK_NAME_SELECTION;
import static org.sample.chat.config.Settings.HOST;
import static org.sample.chat.config.Settings.PORT;
import static org.sample.chat.config.Settings.TEXT_AREA_COLUMNS;
import static org.sample.chat.config.Settings.TEXT_AREA_ROWS;
import static org.sample.chat.config.Settings.TEXT_FIELD_COLUMNS;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Chat client.
 */
public class ChatClient {
    private static final Logger LOGGER = Logger.getLogger(ChatClient.class.getName());

    private PrintWriter out;
    private final JFrame frame = new JFrame(CHAT_TITLE.getValue());
    private final JTextField textField = new JTextField((Integer) TEXT_FIELD_COLUMNS.getValue());
    private final JTextArea messageArea = new JTextArea((Integer) TEXT_AREA_ROWS.getValue(), (Integer) TEXT_AREA_COLUMNS.getValue());

    /**
     * Constructs the GUI and initializes a listener for the user input.
     */
    public ChatClient() {
        initChatGUI();
        textField.addActionListener(e -> processInput());
    }

    /**
     * Reads the user input and write it on the socket.
     */
    private void processInput() {
        out.println(textField.getText());
        textField.setText("");
    }

    private void initChatGUI() {
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), CENTER);
        frame.pack();
    }

    private String getName() {
        return JOptionPane.showInputDialog(
                frame,
                CHOOSE_NICK_NAME.getValue(),
                NICK_NAME_SELECTION.getValue(),
                PLAIN_MESSAGE
        );
    }

    private void run() throws IOException {
        int messagePrefixLength = MESSAGE.getValue().length() + 1;
        try (var socket = new Socket((String) HOST.getValue(), (Integer) PORT.getValue()); var in = new Scanner(socket.getInputStream())) {
            out = new PrintWriter(socket.getOutputStream(), true);
            while (in.hasNextLine()) {
                var userInput = in.nextLine();
                processUserInput(messagePrefixLength, userInput);
            }
        } catch (ConnectException e) {
            LOGGER.log(SEVERE, "Could not connect to the server, it may be stopped or currently unavailable. Error message: {0}", e.getMessage());
        } finally {
            LOGGER.log(INFO, "The server closed the connection, bye bye.");
            frame.setVisible(false);
            frame.dispose();
        }
    }

    /**
     * Process the user input message.
     * The managed case are: nick name request, nick name accepted, a user message
     * @param messagePrefixLength the length of the message label prefix
     * @param userInput the user input
     */
    private void processUserInput(final int messagePrefixLength, final String userInput) {
        if (userInput.startsWith(NICK_NAME_SUBMIT.getValue())) {
            out.println(getName());
        } else if (userInput.startsWith(NICK_NAME_ACCEPTED.getValue())) {
            textField.setEditable(true);
        } else if (userInput.startsWith(MESSAGE.getValue())) {
            messageArea.append(userInput.substring(messagePrefixLength) + "\n");
        }
    }

    public static void main(final String[] args) throws Exception {
        var client = new ChatClient();
        client.frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}
