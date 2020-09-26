package tk.zedlabs.neubrowser

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import mozilla.components.browser.session.Session
import mozilla.components.browser.session.SessionManager
import mozilla.components.browser.session.usecases.EngineSessionUseCases
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.browser.toolbar.BrowserToolbar
import mozilla.components.concept.engine.Engine
import mozilla.components.concept.engine.EngineView
import mozilla.components.feature.downloads.DownloadsFeature
import mozilla.components.feature.downloads.DownloadsUseCases
import mozilla.components.feature.downloads.manager.FetchDownloadManager
import mozilla.components.feature.session.SessionFeature
import mozilla.components.feature.session.SessionUseCases
import mozilla.components.feature.toolbar.ToolbarFeature
import tk.zedlabs.neubrowser.downloads.DownloadService
import javax.inject.Inject

@AndroidEntryPoint
class BrowserActivity : AppCompatActivity() {

    @Inject lateinit var engine: Engine
    @Inject lateinit var  store: BrowserStore
    @Inject lateinit var  sessionManager: SessionManager

    private lateinit var sessionFeature: SessionFeature
    private lateinit var toolbarFeature: ToolbarFeature
    private lateinit var downloadsFeature: DownloadsFeature

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)

        val sessionUseCases = SessionUseCases(sessionManager)
        val engineUseCases = EngineSessionUseCases(sessionManager)
        val downloadsUseCases = DownloadsUseCases(store)

        sessionFeature = SessionFeature(
            store,
            sessionUseCases.goBack,
            engineUseCases,
            findViewById<View>(R.id.engineView) as EngineView
        )

        toolbarFeature = ToolbarFeature(
            findViewById<BrowserToolbar>(R.id.toolbar),
            store,
            sessionUseCases.loadUrl
        )

        downloadsFeature = DownloadsFeature(
            applicationContext,
            store,
            downloadsUseCases,
            onNeedToRequestPermissions = {permissions ->
                requestPermissions(permissions, 1)
            },
            downloadManager = FetchDownloadManager(
                applicationContext,
                store,
                DownloadService::class
            ),
            fragmentManager = supportFragmentManager
        )

        sessionManager.add(
            Session("https://www.theverge.com")
        )

        lifecycle.addObserver(sessionFeature)
        lifecycle.addObserver(toolbarFeature)
        lifecycle.addObserver(downloadsFeature)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        downloadsFeature.onPermissionsResult(permissions, grantResults)
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