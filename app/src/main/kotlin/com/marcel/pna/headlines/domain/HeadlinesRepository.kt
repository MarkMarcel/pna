package com.marcel.pna.headlines.domain

import com.marcel.pna.core.Result

interface HeadlinesRepository {
    suspend fun getHeadlines(request: HeadlinesRequest): Result<HeadlinesLoadError, HeadlinesPage>
}