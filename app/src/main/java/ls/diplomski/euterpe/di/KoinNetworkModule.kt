package ls.diplomski.euterpe.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.gson.gson
import ls.diplomski.euterpe.data.impl.MusicSnippetRemoteRepositoryImpl
import ls.diplomski.euterpe.data.impl.MusicSnippetsApiServiceImpl
import ls.diplomski.euterpe.domain.MusicSnippetRemoteRepository
import ls.diplomski.euterpe.domain.api.MusicSnippetsApiService
import ls.diplomski.euterpe.ui.camerascreen.CameraViewModel
import ls.diplomski.euterpe.utils.MediaPlayerHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

object KoinNetworkModule {
    private val networkModule = module {
        single {
            HttpClient(Android) {
                install(ContentNegotiation) {
                    gson()
                }
                install(Logging) {
                    level = LogLevel.INFO
                }
            }
        }

        singleOf(::MusicSnippetsApiServiceImpl) bind MusicSnippetsApiService::class
    }

    private val repositoryModule = module {
        singleOf(::MusicSnippetRemoteRepositoryImpl) bind MusicSnippetRemoteRepository::class
    }

    private val helperModule = module {
        single { MediaPlayerHelper(androidContext()) }
    }

    private val viewModelModule = module {
        viewModelOf(::CameraViewModel)
    }

    val appModules = listOf(networkModule, repositoryModule, helperModule, viewModelModule)

}