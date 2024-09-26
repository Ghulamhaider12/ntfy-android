package io.heckel.ntfy.ui

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.heckel.ntfy.R

class MainActivity2 : AppCompatActivity() {
    private lateinit var webview: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        // Enable the up button in the ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get the WebView from the layout
        webview = findViewById(R.id.webview)
        val url = intent.getStringExtra("url") ?: "https://staging.saito.io/redsquare"

        // Set the title and enable the back button in ActionBar
        title = url
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Configure WebView settings for persistence
        webview.settings.apply {
            javaScriptEnabled = true // Enable JavaScript
            domStorageEnabled = true // Enable DOM storage for localStorage
            databaseEnabled = true // Enable database storage
            cacheMode = WebSettings.LOAD_DEFAULT // Use cache when available
            allowFileAccess = true // Allow access to files
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW // Allow mixed content (HTTP + HTTPS)
            saveFormData = true // Save form data for persistence across sessions
        }

        // Set up the WebViewClient
        webview.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                // Optionally show loading indicator
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Optionally hide loading indicator
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                Toast.makeText(this@MainActivity2, "Failed to load page", Toast.LENGTH_SHORT).show()
            }

            // Handle URL loading behavior
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                return if (url.contains("saito.io")) {
                    // Load saito.io URLs in the WebView
                    false
                } else {
                    // Open external URLs in the default browser
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                    true
                }
            }
        }

        // Load the initial URL
        webview.loadUrl(url)
    }

    // Handle the WebView's state to ensure data persistence
    override fun onPause() {
        super.onPause()
        webview.onPause() // Pause WebView to retain session

        // Optionally, disconnect WebSocket or terminate connections
        webview.evaluateJavascript("disconnectWebSocket()", null)
    }

    override fun onResume() {
        super.onResume()
        webview.onResume() // Resume WebView

        // Optionally, reconnect WebSocket or reinitialize connections
        webview.evaluateJavascript("reconnectWebSocket()", null)
    }

    override fun onDestroy() {
        // Clear WebView cache and history to ensure proper cleanup
        webview.clearHistory()
        webview.clearCache(true)
        webview.loadUrl("about:blank") // Avoid memory leaks by loading blank page
        webview.onPause()
        webview.removeAllViews()
        webview.destroy()
        super.onDestroy()
    }

    // Handle the back button in the ActionBar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Navigate back when the back button is pressed
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
