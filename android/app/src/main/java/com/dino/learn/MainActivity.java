package com.dino.learn;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Locale;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TARGET_URL = "http://YOUR_SERVER_IP:3000";

    private WebView webView;
    private View errorView;
    private TextToSpeech tts;
    private TtsBridge ttsBridge;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupImmersiveMode();

        FrameLayout root = new FrameLayout(this);
        root.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        webView = new WebView(this);
        webView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        root.addView(webView);

        errorView = createErrorView();
        errorView.setVisibility(View.GONE);
        root.addView(errorView);

        setContentView(root);

        initTts();
        configureWebView();
        webView.addJavascriptInterface(ttsBridge, "AndroidTts");

        loadLocalFrontend();

        setupBackNavigation();
    }

    private void initTts() {
        ttsBridge = new TtsBridge();
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int zhResult = tts.setLanguage(Locale.CHINESE);
                int enResult = tts.setLanguage(Locale.US);
                ttsBridge.setReady(true);
            }
        });
    }

    private void setupImmersiveMode() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (controller != null) {
            controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    private void configureWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setTextZoom(100);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        webView.setBackgroundColor(Color.WHITE);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.startsWith(TARGET_URL + "/assets/")) {
                    String assetPath = url.substring(TARGET_URL.length() + 1);
                    try {
                        java.io.InputStream is = getAssets().open(assetPath);
                        String ext = assetPath.contains(".") ? assetPath.substring(assetPath.lastIndexOf('.') + 1) : "";
                        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
                        if (mime == null) mime = "application/octet-stream";
                        return new WebResourceResponse(mime, "UTF-8", is);
                    } catch (java.io.IOException e) {
                        // fall through to network
                    }
                }
                return null;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                errorView.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) {
                    showErrorView();
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                runOnUiThread(() -> request.grant(request.getResources()));
            }
        });
    }

    private View createErrorView() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(android.view.Gravity.CENTER);
        layout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        layout.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        TextView title = new TextView(this);
        title.setText("Network Error");
        title.setTextSize(20f);
        title.setTextColor(Color.parseColor("#333333"));
        title.setPadding(0, 0, 0, 32);
        layout.addView(title);

        TextView message = new TextView(this);
        message.setText("Unable to connect to the server.\nPlease check your network connection.");
        message.setTextSize(16f);
        message.setTextColor(Color.parseColor("#666666"));
        message.setGravity(android.view.Gravity.CENTER);
        message.setPadding(32, 0, 32, 48);
        layout.addView(message);

        TextView retryBtn = new TextView(this);
        retryBtn.setText("  Retry  ");
        retryBtn.setTextSize(18f);
        retryBtn.setTextColor(Color.WHITE);
        retryBtn.setGravity(android.view.Gravity.CENTER);
        retryBtn.setPadding(64, 24, 64, 24);
        retryBtn.setBackgroundColor(Color.parseColor("#4EC9B0"));
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        retryBtn.setLayoutParams(btnParams);
        retryBtn.setOnClickListener(v -> {
            errorView.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            webView.reload();
        });
        layout.addView(retryBtn);

        return layout;
    }

    private void showErrorView() {
        webView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }

    private void loadLocalFrontend() {
        try {
            java.io.InputStream is = getAssets().open("index.html");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String html = new String(buffer, java.nio.charset.StandardCharsets.UTF_8);
            webView.loadDataWithBaseURL(TARGET_URL + "/", html, "text/html", "UTF-8", null);
        } catch (Exception e) {
            webView.loadUrl(TARGET_URL);
        }
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (webView != null) {
            webView.onPause();
        }
        super.onPause();
    }

    private class TtsBridge {
        private boolean ready = false;
        private String pendingText;
        private String pendingLanguage;

        void setReady(boolean r) { ready = r; }

        @JavascriptInterface
        public boolean isReady() { return ready; }

        @JavascriptInterface
        public void speak(String text) {
            speakWithLanguage(text, "Chinese");
        }

        @JavascriptInterface
        public void speakWithLanguage(String text, String language) {
            if (!ready) {
                pendingText = text;
                pendingLanguage = language;
                return;
            }
            Locale locale = "Chinese".equalsIgnoreCase(language) || "zh".equalsIgnoreCase(language) ?
                    Locale.CHINESE : Locale.US;
            tts.setLanguage(locale);

            String utteranceId = String.valueOf(System.currentTimeMillis());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
            } else {
                HashMap<String, String> params = new HashMap<>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, params);
            }
        }

        @JavascriptInterface
        public void stop() {
            tts.stop();
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
