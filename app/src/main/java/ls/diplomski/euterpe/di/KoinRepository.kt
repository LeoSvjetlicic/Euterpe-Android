package ls.diplomski.euterpe.di

import ls.diplomski.euterpe.data.impl.MusicSnippetRepositoryImpl
import ls.diplomski.euterpe.domain.MusicSnippetsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

object KoinRepository {
    val module = module {
        singleOf(::MusicSnippetRepositoryImpl) bind MusicSnippetsRepository::class
    }
}