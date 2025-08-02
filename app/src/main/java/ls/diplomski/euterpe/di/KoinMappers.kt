package ls.diplomski.euterpe.di

import ls.diplomski.euterpe.data.mappers.MusicSnippetToViewStateMapperImpl
import ls.diplomski.euterpe.domain.mappers.MusicSnippetToViewStateMapper
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

object KoinMappers {
    val module = module {
        singleOf(::MusicSnippetToViewStateMapperImpl) bind MusicSnippetToViewStateMapper::class
    }
}