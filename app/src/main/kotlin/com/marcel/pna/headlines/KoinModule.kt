package com.marcel.pna.headlines

import com.marcel.pna.core.BACKGROUND_DISPATCHER
import com.marcel.pna.core.IO_DISPATCHER
import com.marcel.pna.core.PNAMDatabase
import com.marcel.pna.headlines.data.ArticleLocalDataSource
import com.marcel.pna.headlines.data.DefaultHeadlinesRepository
import com.marcel.pna.headlines.data.HeadlinesApi
import com.marcel.pna.headlines.data.HeadlinesRemoteDataSource
import com.marcel.pna.headlines.domain.HeadlinesRepository
import com.marcel.pna.headlines.domain.HeadlinesUseCaseProvider
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val NEWS_API_RETROFIT = "newsApiRetrofit"

val HeadlinesModule = module {
    // Todo: add interceptor to track number of requests in a day
    single(named(NEWS_API_RETROFIT)) {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(get<MoshiConverterFactory>())
            .client(get())
            .build()
    }

    single {
        get<Retrofit>(named(NEWS_API_RETROFIT)).create(HeadlinesApi::class.java)
    }

    single {
        ArticleLocalDataSource(
            articleRoomDao = get(),
            logger = get()
        )
    }

    single { get<PNAMDatabase>().articleRoomDao() }

    single {
        HeadlinesRemoteDataSource(
            appConfigProvider = get(),
            api = get(),
            logger = get(),
            moshi = get()
        )
    }

    single<HeadlinesRepository> {
        DefaultHeadlinesRepository(
            ioDispatcher = get(named(IO_DISPATCHER)),
            remoteDataSource = get(),
            userSettingsRepository = get()
        )
    }

    single {
        HeadlinesUseCaseProvider(
            backgroundDispatcher = get(named(BACKGROUND_DISPATCHER)),
            headlinesRepository = get()
        )
    }
}