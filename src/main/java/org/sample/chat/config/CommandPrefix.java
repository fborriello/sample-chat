package org.sample.chat.config;

/**
 * Command prefixes.
 */
public enum CommandPrefix {
    NICK_NAME_SUBMIT("{nickname submitted}"),
    NICK_NAME_ACCEPTED("{nickname accepted}"),
    MESSAGE("{new message}"),
    EXIT("/exit");

    /**
     * The command prefix.
     */
    private final String value;

    /**
     * Constructor.
     * @param value the command prefix.
     */
    CommandPrefix(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
