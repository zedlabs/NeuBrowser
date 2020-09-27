package tk.zedlabs.neubrowser.browser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import mozilla.components.browser.search.SearchEngineManager
import mozilla.components.browser.session.Session
import mozilla.components.browser.session.SessionManager
import mozilla.components.browser.session.usecases.EngineSessionUseCases
import mozilla.components.browser.state.selector.selectedTab
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.browser.toolbar.BrowserToolbar
import mozilla.components.concept.engine.EngineView
import mozilla.components.feature.downloads.DownloadsFeature
import mozilla.components.feature.downloads.DownloadsUseCases
import mozilla.components.feature.downloads.manager.FetchDownloadManager
import mozilla.components.feature.findinpage.FindInPageFeature
import mozilla.components.feature.findinpage.view.FindInPageBar
import mozilla.components.feature.search.SearchUseCases
import mozilla.components.feature.session.SessionFeature
import mozilla.components.feature.session.SessionUseCases
import tk.zedlabs.neubrowser.R
import tk.zedlabs.neubrowser.browser.toolbar.ToolbarIntegration
import tk.zedlabs.neubrowser.downloads.DownloadService
import javax.inject.Inject

@AndroidEntryPoint
class BrowserFragment : Fragment() {

    @Inject lateinit var store: BrowserStore
    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var searchEngineManager: SearchEngineManager

    @Inject lateinit var sessionUseCases: SessionUseCases
    @Inject lateinit var engineUseCases: EngineSessionUseCases
    @Inject lateinit var downloadsUseCases: DownloadsUseCases
    @Inject lateinit var searchUseCases: SearchUseCases

    private lateinit var sessionFeature: SessionFeature
    private lateinit var downloadsFeature: DownloadsFeature
    private lateinit var findInPageFeature: FindInPageFeature

    private lateinit var toolbarIntegration: ToolbarIntegration

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_browser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val engineView: EngineView = view.findViewById<View>(R.id.engineView) as EngineView

        sessionFeature = SessionFeature(
            store,
            sessionUseCases.goBack,
            engineUseCases,
            engineView
        )

        findInPageFeature = FindInPageFeature(
            store,
            view.findViewById<FindInPageBar>(R.id.findInPageBar),
            engineView
        ) {
            view.findViewById<FindInPageBar>(R.id.findInPageBar).visibility = View.GONE
        }

        val toolbar = view.findViewById<BrowserToolbar>(R.id.toolbar)

        toolbarIntegration = ToolbarIntegration(
            store,
            toolbar,
            sessionUseCases,
            searchUseCases
        ){
            view.findViewById<FindInPageBar>(R.id.findInPageBar).visibility = View.VISIBLE
            findInPageFeature.bind(store.state.selectedTab!!)
        }

        downloadsFeature = DownloadsFeature(
            requireContext(),
            store,
            downloadsUseCases,
            onNeedToRequestPermissions = { permissions ->
                requestPermissions(permissions, 1)
            },
            downloadManager = FetchDownloadManager(
                requireContext(),
                store,
                DownloadService::class
            ),
            fragmentManager = childFragmentManager
        )

        sessionManager.add(
            Session("https://www.theverge.com")
        )

        lifecycle.addObserver(sessionFeature)
        lifecycle.addObserver(toolbarIntegration)
        lifecycle.addObserver(downloadsFeature)
        lifecycle.addObserver(findInPageFeature)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        downloadsFeature.onPermissionsResult(permissions, grantResults)
    }

}