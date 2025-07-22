package com.marcel.pna.headlines.domain

enum class HeadlinesLoadError {
    API_KEY_DISABLED,
    API_KEY_EXHAUSTED,
    API_KEY_INVALID,
    DEBOUNCED,
    NETWORK,
    RATE_LIMITED,
    SOURCE_DOES_NOT_EXIST,
    SOURCES_TOO_MANY,
    SERVER
}
