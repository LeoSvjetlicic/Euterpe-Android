package ls.diplomski.euterpe

import android.app.Application
import ls.diplomski.euterpe.di.KoinMappers
import ls.diplomski.euterpe.di.KoinMockData
import ls.diplomski.euterpe.di.KoinUseCases
import ls.diplomski.euterpe.di.KoinUtilsClasses
import ls.diplomski.euterpe.di.KoinViewModels
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class EuterpeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@EuterpeApplication)
            androidLogger()
            modules(
                KoinViewModels.module,
                KoinUtilsClasses.module,
                KoinMockData.module,
                KoinMappers.module,
                KoinUseCases.module
            )
        }
    }
}