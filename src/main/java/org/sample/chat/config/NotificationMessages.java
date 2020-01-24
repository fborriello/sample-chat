package org.sample.chat.config;

import static org.sample.chat.config.CommandPrefix.EXIT;

/**
 * Notification messages.
 */
public enum NotificationMessages {
    NICK_NAME_EXISTING("Nickname already existing, please choose a different one."),
    SOMEONE_JOINED("%s has joined"),
    SOMEONE_LEFT("%s has left"),
    NO_ONE_ONLINE("%s no one is online. Please wait for someone to start the conversation."),
    PEOPLE_ALREADY_IN("People already in: %s"),
    CHAT_TITLE("Bla Bla chat, type " + EXIT.getValue() + " to exit."),
    CHOOSE_NICK_NAME("Choose a nickname:"),
    NICK_NAME_SELECTION("Nickname selection");

    /**
     * The notification message value.
     */
    private final String value;

    /**
     * Constructor.
     * @param value notification message value
     */
    NotificationMessages(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
