package com.example.chinesesurnames;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by hang dong on 12/05/2016.
 */
public class WorkInfoPage extends Activity {
    private WebView webView;
    //private Button buttonBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workinfo_page);

// in onCreate

        //********************* Here Prepare the WebView *********
        webView = (WebView) findViewById(R.id.webView2); // Find in Resource and Assign to variable
        webView.getSettings().setJavaScriptEnabled(true); //allow it to suuport java scripts
        webView.getSettings().setLoadsImagesAutomatically(true);
        //webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.clearCache(true);

        webView.setScrollBarStyle(webView.SCROLLBARS_INSIDE_OVERLAY);

        //=====================[ HERE GET THE URL PASSED FROM First Page(activity)====================================
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String PageTitle = extras.getString("PAGE_TITLE");
            if (PageTitle != null) setTitle(PageTitle);

            String UrlLink = extras.getString("LINK_CLICKED");
            String rawHtml = extras.getString("RAW_HTML");

            if (rawHtml.length() > 5)
            {
                Log.d("看哟",rawHtml);
                webView.loadDataWithBaseURL(null,rawHtml, "text/html", "UTF-8","about:blank");
                //webView.loadData(rawHtml, "text/html; charset=utf-8", "UTF-8");
                //Log.d("看结束咯","呵呵");
                return;
            }

            if (UrlLink.length() > 5)
            {
                //Log.d("看哦",UrlLink);
                webView.loadUrl(UrlLink);
                return;
            }
        }

        //======================USED TO CALL Next Page with Link ================================================
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                Log.d("看这里url",url);
                view.loadUrl(url);
                //we have intercepted the desired url call
                return true;
            } //end of shouldOverrideUrlLoading()

        }); //end of setWebViewClient()
        //======================================================================
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wiki_page, menu);
        return true;
    }

}
