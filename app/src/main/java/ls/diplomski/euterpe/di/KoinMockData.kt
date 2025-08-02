package ls.diplomski.euterpe.di

import ls.diplomski.euterpe.data.mock.MusicSnippetsRepositoryMockRepository
import ls.diplomski.euterpe.domain.MusicSnippetsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

object KoinMockData {
    val module = module {
        singleOf(::MusicSnippetsRepositoryMockRepository) bind MusicSnippetsRepository::class
    }
}