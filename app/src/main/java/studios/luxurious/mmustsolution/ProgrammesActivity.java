package studios.luxurious.mmustsolution;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.daasuu.cat.CountAnimationTextView;

public class ProgrammesActivity extends AppCompatActivity {
    CountAnimationTextView studentCount, programesCount, schoolsCount;

    WebView webView;
    ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programmes);
        studentCount = findViewById(R.id.studentsCount);
        programesCount = findViewById(R.id.programmesCount);
        schoolsCount = findViewById(R.id.schoolsCount);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progress_bar);

        studentCount.setAnimationDuration(5000).countAnimation(0, 17000);
        programesCount.setAnimationDuration(4000).countAnimation(0, 400);
        schoolsCount.setAnimationDuration(2000).countAnimation(0, 11);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);

            }
        });


        webView.loadUrl("http://www.mmust.ac.ke/index.php/student-programmes");

    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }


}
