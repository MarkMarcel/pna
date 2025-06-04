package com.marcel.pna.headlines.domain

enum class HeadlinesLoadError {
    ApiKeyDisabled,
    ApiKeyExhausted,
    ApiKeyInvalid,
    Debounced,
    Network,
    RateLimited,
    SourceDoesNotExist,
    SourcesTooMany,
    Server
}
