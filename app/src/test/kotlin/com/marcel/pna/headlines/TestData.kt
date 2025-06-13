package com.marcel.pna.headlines

import com.marcel.pna.headlines.data.models.ArticleApiResponse
import com.marcel.pna.headlines.data.models.NewsApiResponse
import com.marcel.pna.headlines.data.models.SourceApiResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody

val headlinesResponseTestData = NewsApiResponse(
    status = "ok",
    totalResults = 5,
    articles = listOf(
        ArticleApiResponse(
            source = SourceApiResponse(id = "cnbc", name = "CNBC"),
            author = "Jenni Reid",
            title = "European markets open lower",
            description = "Markets react to economic reports.",
            url = "https://www.example.com/article1",
            urlToImage = "https://www.example.com/image1.jpg",
            publishedAt = "2025-06-12T07:21:00Z",
            content = "Content of article one..."
        ),
        ArticleApiResponse(
            source = SourceApiResponse(id = "ap", name = "Associated Press"),
            author = "John Doe",
            title = "New trade policies announced",
            description = "The latest update on trade discussions.",
            url = "https://www.example.com/article2",
            urlToImage = "https://www.example.com/image2.jpg",
            publishedAt = "2025-06-12T08:30:00Z",
            content = "Content of article two..."
        ),
        ArticleApiResponse(
            source = SourceApiResponse(id = "bbc", name = "BBC News"),
            author = "Alice Smith",
            title = "Climate talks resume in Geneva",
            description = "Leaders meet to address climate change.",
            url = "https://www.example.com/article3",
            urlToImage = "https://www.example.com/image3.jpg",
            publishedAt = "2025-06-12T09:00:00Z",
            content = "Content of article three..."
        ),
        ArticleApiResponse(
            source = SourceApiResponse(id = "reuters", name = "Reuters"),
            author = "Mark Thomas",
            title = "Tech stocks rebound",
            description = "Major tech firms see gains after downturn.",
            url = "https://www.example.com/article4",
            urlToImage = "https://www.example.com/image4.jpg",
            publishedAt = "2025-06-12T10:15:00Z",
            content = "Content of article four..."
        ),
        ArticleApiResponse(
            source = SourceApiResponse(id = "nyt", name = "New York Times"),
            author = "Jane Wilson",
            title = "Education reforms proposed",
            description = "Government outlines new education strategy.",
            url = "https://www.example.com/article5",
            urlToImage = "https://www.example.com/image5.jpg",
            publishedAt = "2025-06-12T11:45:00Z",
            content = "Content of article five..."
        )
    )
)

val apiKeyDisabledErrorBody = """
{
  "status": "error",
  "code": "apiKeyDisabled",
  "message": "Your API key has been disabled. Please contact support."
}
""".trimIndent().toResponseBody("application/json".toMediaType())

val apiKeyExhaustedErrorBody = """
{
  "status": "error",
  "code": "apiKeyExhausted",
  "message": "You have exceeded the usage limit for your API key."
}
""".trimIndent().toResponseBody("application/json".toMediaType())

val apiKeyInvalidErrorBody = """
{
  "status": "error",
  "code": "apiKeyInvalid",
  "message": "The provided API key is invalid. Please verify your key."
}
""".trimIndent().toResponseBody("application/json".toMediaType())

val apiKeyMissingErrorBody = """
{
  "status": "error",
  "code": "apiKeyMissing",
  "message": "An API key is required to access this resource."
}
""".trimIndent().toResponseBody("application/json".toMediaType())

val parameterInvalidErrorBody = """
{
  "status": "error",
  "code": "parameterInvalid",
  "message": "One or more parameters are invalid. Please check your request."
}
""".trimIndent().toResponseBody("application/json".toMediaType())

val parametersMissingErrorBody = """
{
  "status": "error",
  "code": "parametersMissing",
  "message": "Required parameters are missing in the request."
}
""".trimIndent().toResponseBody("application/json".toMediaType())

val rateLimitedErrorBody = """
{
  "status": "error",
  "code": "rateLimited",
  "message": "Too many requests. Please slow down and try again later."
}
""".trimIndent().toResponseBody("application/json".toMediaType())

val sourcesTooManyErrorBody = """
{
  "status": "error",
  "code": "sourcesTooMany",
  "message": "The number of sources requested is too high. Please reduce and try again."
}
""".trimIndent().toResponseBody("application/json".toMediaType())

val sourceDoesNotExistErrorBody = """
{
  "status": "error",
  "code": "sourceDoesNotExist",
  "message": "The requested source does not exist. Please verify the source identifier."
}
""".trimIndent().toResponseBody("application/json".toMediaType())

val unexpectedErrorBody = """
{
  "status": "error",
  "code": "unexpectedError",
  "message": "An unexpected error occurred on the server."
}
""".trimIndent().toResponseBody("application/json".toMediaType())

