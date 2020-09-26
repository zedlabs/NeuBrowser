package tk.zedlabs.neubrowser.modules

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import mozilla.components.browser.engine.gecko.GeckoEngine
import mozilla.components.browser.session.SessionManager
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.concept.engine.Engine
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class CoreComponentsModule {

    @Provides
    @Singleton
    public fun providesEngine(application: Application): Engine{
        return GeckoEngine(application)
    }

    @Provides
    @Singleton
    fun providesStore(): BrowserStore {
        return BrowserStore()
    }

    @Provides
    @Singleton
    fun providesSessionManager(engine: Engine, store: BrowserStore): SessionManager{
        return SessionManager(engine, store)
    }

}