package studios.luxurious.mmustsolution;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import studios.luxurious.mmustsolution.Utils.Constants;
import studios.luxurious.mmustsolution.Utils.SharedPref;

public class Portal extends AppCompatActivity {

    WebView webView;
    ProgressBar progressBar;
    InterstitialAd interstitialAd;
    AdView adView;
    int num = 1;

    String login_url;
    String currentUrl;

    SharedPref sharedPref;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Mmust Portal");

        webView = findViewById(R.id.webview);

        progressBar = findViewById(R.id.progressbar);

        sharedPref = new SharedPref(this);


        final String regno = getIntent().getExtras().getString("regno");
        final String password = getIntent().getExtras().getString("password");


        login_url = Constants.PORTAL_URL;
        currentUrl = login_url;


        InitializeAds();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                String filename = URLUtil.guessFileName(url, contentDisposition, mimetype);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);

                Toast.makeText(Portal.this, "Downloading " + filename, Toast.LENGTH_SHORT).show();

            }
        });


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                num++;
                progressBar.setVisibility(View.GONE);
                view.loadUrl("javascript: var x = document.getElementById('UserName').value = '" + regno + "';");
                view.loadUrl("javascript: var y = document.getElementById('Password').value = '" + password + "';");
                view.loadUrl("javascript: var b = document.getElementsByTagName('input');  b[5].click(); ");
                currentUrl = webView.getUrl();

            }
        });

        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        CookieSyncManager.getInstance().startSync();


        webView.loadUrl(login_url);

    }

    public void InitializeAds() {
        MobileAds.initialize(this, getString(R.string.app_id));

        adView = findViewById(R.id.ad);
        AdRequest adRequest = new AdRequest.Builder().build();

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_portal));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        adView.loadAd(adRequest);

        interstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int i) {
                interstitialAd.loadAd(new AdRequest.Builder().build());

            }
        });


        adView.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int i) {
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
            }


            @Override
            public void onAdClosed() {
                startActivity(new Intent(Portal.this, HomeActivity.class));
                finish();
            }

        });
    }


    @Override
    public void onBackPressed() {

        if (currentUrl.contains("Home")) {
            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
            } else {
                startActivity(new Intent(Portal.this, HomeActivity.class));
                finish();
            }
        } else {
            if (webView.canGoBack()) {
                webView.goBack();
            } else if (interstitialAd.isLoaded()) {
                interstitialAd.show();
            } else {
                startActivity(new Intent(Portal.this, HomeActivity.class));
                finish();
            }
        }
    }


}
