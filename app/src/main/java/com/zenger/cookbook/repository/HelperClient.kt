package com.zenger.cookbook.repository

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.BindingAdapter

class HelperClient(private val currentUrl: String): WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        return url != currentUrl
    }
}

object Util {

    @BindingAdapter("loadUrl")
    @JvmStatic
    fun WebView.viewUrl(url: String) {
        this.webViewClient = HelperClient(url)
        this.loadUrl(url)
    }
}