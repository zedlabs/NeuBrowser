package tk.zedlabs.neubrowser

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import mozilla.components.browser.engine.gecko.GeckoEngine
import mozilla.components.concept.engine.Engine
import mozilla.components.concept.engine.EngineView

class BrowserActivity : Activity() {

    private val engine: Engine by lazy { GeckoEngine(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)

    }

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {

        if(name == EngineView::class.java.name){
            val engineView = engine.createView(this, attrs)
            val engineSession =  engine.createSession()

            engineSession.loadUrl("http://www.duckduckgo.com")
            engineView.render(engineSession)

            return engineView.asView()
        }

        return super.onCreateView(parent, name, context, attrs)
    }

}