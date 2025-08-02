package ls.diplomski.euterpe.di

import ls.diplomski.euterpe.domain.FetchMusicSnippetsUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object KoinUseCases {
    val module = module {
        singleOf(::FetchMusicSnippetsUseCase)
    }
}

