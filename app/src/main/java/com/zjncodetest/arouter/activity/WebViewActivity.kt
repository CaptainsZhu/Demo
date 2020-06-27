package com.zjncodetest.arouter.activity

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.zjncodetest.R
import com.zjncodetest.arouter.PATH_WEBVIEW_ACTIVITY

@Route(path = PATH_WEBVIEW_ACTIVITY)
class WebViewActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        val webView = findViewById<WebView>(R.id.webView)
        webView.loadUrl("file:///android_asset/schema-test.html")
    }
}