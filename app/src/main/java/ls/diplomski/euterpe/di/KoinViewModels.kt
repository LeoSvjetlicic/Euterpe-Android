package ls.diplomski.euterpe.di

import ls.diplomski.euterpe.ui.musicsnippetlist.MusicSnippetListScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

object KoinViewModels {
    val module = module {
        viewModelOf(::MusicSnippetListScreenViewModel)
    }
}