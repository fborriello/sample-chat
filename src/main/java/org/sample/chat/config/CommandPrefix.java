package org.sample.chat.config;

public enum CommandPrefix {
    NICK_NAME_SUBMIT("{nickname submitted}"),
    NICK_NAME_ACCEPTED("{nickname accepted}"),
    MESSAGE("{new message}"),
    EXIT("/exit");

    private final String value;

    CommandPrefix(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
