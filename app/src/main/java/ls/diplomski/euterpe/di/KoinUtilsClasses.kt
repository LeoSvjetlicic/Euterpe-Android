package ls.diplomski.euterpe.di

import ls.diplomski.euterpe.utils.MidiParser
import ls.diplomski.euterpe.utils.MediaPlayerHelper
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object KoinUtilsClasses {
    val module = module {
        singleOf(::MediaPlayerHelper)
        singleOf(::MidiParser)
    }
}