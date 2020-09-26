package tk.zedlabs.neubrowser.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import mozilla.components.browser.engine.gecko.GeckoEngine
import mozilla.components.browser.engine.gecko.fetch.GeckoViewFetchClient
import mozilla.components.browser.session.SessionManager
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.concept.engine.Engine
import mozilla.components.concept.fetch.Client
import mozilla.components.feature.downloads.DownloadMiddleware
import tk.zedlabs.neubrowser.downloads.DownloadService
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class CoreComponentsModule {

    @Provides
    @Singleton
    fun providesEngine(application: Application): Engine{
        return GeckoEngine(application)
    }

    @Provides
    @Singleton
    fun providesStore(application: Application): BrowserStore {
        return BrowserStore(
            middleware = listOf(
                DownloadMiddleware(application.applicationContext, DownloadService::class.java)
            )
        )
    }

    @Provides
    @Singleton
    fun providesSessionManager(engine: Engine, store: BrowserStore): SessionManager{
        return SessionManager(engine, store)
    }

    @Provides
    @Singleton
    fun providesClient(application: Application): Client{
        return GeckoViewFetchClient(application)
    }
}