package com.android.offeringhands;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.offeringhands.broadcast.CreateBroadcastActivity;
import com.android.offeringhands.chat.GroupsActivity;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        WebView webView = findViewById(R.id.web);


        ImageView chatsView = findViewById(R.id.chats);
        ImageView orphanages = findViewById(R.id.orphanages);
        ImageView oldAgeHomes = findViewById(R.id.oldAgeHomes);
        ImageView createBroadcastOpt = findViewById(R.id.createBroadcastOpt);
        ImageView home = findViewById(R.id.home);
        ImageView donate = findViewById(R.id.webView);


        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Set a WebViewClient to handle the redirects and links within the WebView
        webView.setWebViewClient(new CustomWebViewClient());

        webView.loadUrl("https://offerhands.netlify.app/");


        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WebViewActivity.this, WebViewActivity.class));

            }
        });

        chatsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WebViewActivity.this, GroupsActivity.class));
                finish();
            }
        });

        oldAgeHomes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps("Show+near+by+Old-age+Homes");
            }
        });


        orphanages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps("Show+nearby+Orphanages");
            }
        });


        createBroadcastOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WebViewActivity.this, CreateBroadcastActivity.class));
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WebViewActivity.this, HomeActivity.class));
            }
        });


    }

    private void openGoogleMaps(String query) {
        // Create a URI for the Google Maps app with the query
        String url = "https://www.google.com/maps/search/?api=1&query=" + query;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);

    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Open all links within the app
            if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                view.loadUrl(url);
                return true;
            } else {
                // If the link is not a web URL, handle it as needed
                return false;
            }
        }
    }

}
