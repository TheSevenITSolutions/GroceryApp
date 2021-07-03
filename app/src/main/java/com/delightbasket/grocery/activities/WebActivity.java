package com.delightbasket.grocery.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.databinding.ActivityWebBinding;

public class WebActivity extends AppCompatActivity {
    ActivityWebBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web);


        Intent intent = getIntent();
        if(intent != null) {
            String url = intent.getStringExtra("URL");
            String title = intent.getStringExtra("TITLE");
            if(title != null) {
                binding.tvtitle.setText(title);
            }
            if(url != null) {
                binding.webView.loadUrl(url);
                binding.webView.setWebViewClient(new WebViewClient());

            }
        }
    }

    public void onClickBack(View view) {
        finish();
    }
}