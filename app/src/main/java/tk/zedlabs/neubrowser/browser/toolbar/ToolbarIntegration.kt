package tk.zedlabs.neubrowser.browser.toolbar

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.drawerlayout.widget.DrawerLayout
import mozilla.components.browser.menu.item.SimpleBrowserMenuItem
import mozilla.components.browser.session.SessionManager
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.browser.toolbar.BrowserToolbar
import mozilla.components.feature.search.SearchUseCases
import mozilla.components.feature.session.SessionUseCases
import mozilla.components.feature.tabs.toolbar.TabsToolbarFeature
import mozilla.components.feature.toolbar.ToolbarFeature
import mozilla.components.support.base.feature.LifecycleAwareFeature
import tk.zedlabs.neubrowser.R

class ToolbarIntegration (
    context: Context,
    store: BrowserStore,
    toolbar: BrowserToolbar,
    drawer: DrawerLayout,
    sessionManager: SessionManager,
    sessionUseCases: SessionUseCases,
    private val searchUseCases: SearchUseCases,
    private val findInPage: () -> Unit
    ): LifecycleAwareFeature{

    private val toolbarFeature = ToolbarFeature(
    toolbar,
    store,
    sessionUseCases.loadUrl,
    { searchTerms -> searchUseCases.defaultSearch(searchTerms) }
    )

    private val tabsToolbarFeature = TabsToolbarFeature(
        toolbar,
        sessionManager
    ){
        drawer.open()
    }

    init {
        toolbar.display.menuBuilder = mozilla.components.browser.menu.BrowserMenuBuilder(
            items = listOf(
                SimpleBrowserMenuItem(
                    label = "find in Page",
                    listener = { findInPage() }
                )
            )
        )
        toolbar.display.setUrlBackground(
            AppCompatResources.getDrawable(context, R.drawable.url_background)
        )
        toolbar.display.colors = toolbar.display.colors.copy(
            text = 0xFFFFFFFF.toInt()
        )
        toolbar.edit.colors = toolbar.edit.colors.copy(
            text = 0xFFFFFFFF.toInt()
        )
        
    }

    override fun start() {
        toolbarFeature.start()
    }

    override fun stop() {
        toolbarFeature.stop()
    }
}