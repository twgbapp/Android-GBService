package goldenbrother.gbmobile.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import goldenbrother.gbmobile.R;

public class WebViewActivity extends AppCompatActivity {

    // ui reference
    private WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // ui reference
        wv = (WebView) findViewById(R.id.wv_web_view);
        // extra
        String url = getIntent().getStringExtra("url");
        // init WebView
        WebSettings webSettings = wv.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        wv.setWebViewClient(new MyWebViewClient());
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAppCacheEnabled(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // load
        wv.loadUrl(url);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
//            tv_title.setText(view.getTitle());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {
            wv.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
