package org.sample.chat.config;

/**
 * Chat configuration settings.
 */
public enum Settings {
    PORT(10000),
    HOST("127.0.0.1"),
    MAX_THREADS(500),
    TEXT_FIELD_COLUMNS(50),
    TEXT_AREA_COLUMNS(50),
    TEXT_AREA_ROWS(16);

    /**
     * The setting value.
     */
    private final Object value;

    /**
     * Constructor.
     * @param value setting value
     */
    Settings(final Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}
