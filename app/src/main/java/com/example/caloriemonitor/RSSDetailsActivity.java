package com.example.caloriemonitor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RSSDetailsActivity extends AppCompatActivity {

    TextView tvTitle;
    TextView tvPubDate;
    TextView tvContent;
    Button btnWeb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssdetails);
        tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvPubDate = (TextView)findViewById(R.id.tvPubDate);
        tvContent = (TextView)findViewById(R.id.tvContent);
        btnWeb = (Button)findViewById(R.id.btWeb);

        final Intent intent = getIntent();
        tvTitle.setText(intent.getStringExtra("title"));
        tvPubDate.setText(intent.getStringExtra("pubDate"));
        tvContent.setText(Html.fromHtml(intent.getStringExtra("description")));

        btnWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(intent.getStringExtra("link")));
                startActivity(browserIntent);
            }
        });
    }
}

