package tk.zedlabs.neubrowser

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import mozilla.components.browser.menu.item.SimpleBrowserMenuItem
import mozilla.components.browser.search.SearchEngineManager
import mozilla.components.browser.session.Session
import mozilla.components.browser.session.SessionManager
import mozilla.components.browser.session.usecases.EngineSessionUseCases
import mozilla.components.browser.state.selector.selectedTab
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.browser.toolbar.BrowserToolbar
import mozilla.components.concept.engine.Engine
import mozilla.components.concept.engine.EngineView
import mozilla.components.feature.downloads.DownloadsFeature
import mozilla.components.feature.downloads.DownloadsUseCases
import mozilla.components.feature.downloads.manager.FetchDownloadManager
import mozilla.components.feature.findinpage.FindInPageFeature
import mozilla.components.feature.findinpage.view.FindInPageBar
import mozilla.components.feature.search.SearchUseCases
import mozilla.components.feature.session.SessionFeature
import mozilla.components.feature.session.SessionUseCases
import mozilla.components.feature.toolbar.ToolbarFeature
import tk.zedlabs.neubrowser.browser.BrowserFragment
import tk.zedlabs.neubrowser.downloads.DownloadService
import javax.inject.Inject

@AndroidEntryPoint
class BrowserActivity : AppCompatActivity() {

    @Inject lateinit var engine: Engine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState == null){
            supportFragmentManager
                .beginTransaction()
                .add(android.R.id.content, BrowserFragment())
                .commit()
        }
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {

        if (name == EngineView::class.java.name) {
            return engine.createView(context, attrs).asView()
        }

        return super.onCreateView(parent, name, context, attrs)
    }

}