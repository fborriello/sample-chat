package org.sample.chat.client;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;

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

import static javax.swing.JOptionPane.PLAIN_MESSAGE;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Chat client.
 */
public class ChatClient {
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
                var line = in.nextLine();
                if (line.startsWith(NICK_NAME_SUBMIT.getValue())) {
                    out.println(getName());
                } else if (line.startsWith(NICK_NAME_ACCEPTED.getValue())) {
                    textField.setEditable(true);
                } else if (line.startsWith(MESSAGE.getValue())) {
                    messageArea.append(line.substring(messagePrefixLength) + "\n");
                }
            }
        } finally {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    public static void main(String[] args) throws Exception {
        var client = new ChatClient();
        client.frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}
