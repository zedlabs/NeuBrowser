package tk.zedlabs.neubrowser.modules

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import mozilla.components.browser.search.SearchEngineManager
import mozilla.components.browser.session.SessionManager
import mozilla.components.browser.session.usecases.EngineSessionUseCases
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.feature.downloads.DownloadsUseCases
import mozilla.components.feature.search.SearchUseCases
import mozilla.components.feature.session.SessionUseCases
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class UseCasesModule {

    @Provides
    @Singleton
    fun providesSessionUseCases(sessionManager: SessionManager): SessionUseCases {
        return SessionUseCases(sessionManager)
    }

    @Provides
    @Singleton
    fun providesEngineUseCases(sessionManager: SessionManager): EngineSessionUseCases {
        return EngineSessionUseCases(sessionManager)
    }

    @Provides
    @Singleton
    fun providesDownloadUseCases(store: BrowserStore): DownloadsUseCases {
        return DownloadsUseCases(store)
    }

    @Provides
    @Singleton
    fun providesSearchUseCases(
        application: Application,
        searchEngineManager: SearchEngineManager,
        sessionManager: SessionManager
    ): SearchUseCases{

        return SearchUseCases(application, searchEngineManager, sessionManager)
    }

}