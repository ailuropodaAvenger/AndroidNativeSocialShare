package com.example.salvin.androidnativesocialshare;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText linkET;
    TextView linkTV;
    Button shareButton;
    public String link;
    private String DEFAULT_LINK = "http://bangla.bdnews24.com/samagrabangladesh/article1132594.bdnews";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linkET = (EditText) findViewById(R.id.editText);
        linkTV = (TextView) findViewById(R.id.linkTextView);
        shareButton = (Button) findViewById(R.id.button);
        linkTV.setText("(default link)"+ "\n"+ DEFAULT_LINK);

        linkET.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                link = s.toString();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                linkTV.setText(s);
            }
        });

        updateLink();

        // share via only FB, G+, Twitter
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nativeFilterShare();
            }
        });
    }

    private void updateLink() {
        if (!linkET.getText().toString().isEmpty()) link = linkET.getText().toString();
        if (link == null || link.isEmpty())
            link = DEFAULT_LINK;
    }


    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                updateLink();
                nativeShare();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void nativeShare() {
        Intent i = new Intent(android.content.Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(android.content.Intent.EXTRA_TEXT, link);
        startActivity(Intent.createChooser(i, "Share via"));
    }

    private void nativeFilterShare() {

        List<Intent> targetShareIntents = new ArrayList<Intent>();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        List<ResolveInfo> resInfos = this.getPackageManager().queryIntentActivities(shareIntent, 0);

        if (!resInfos.isEmpty()) {

            for (ResolveInfo resInfo : resInfos) {
                String packageName = resInfo.activityInfo.packageName;

                if (packageName.contains("com.twitter.android") || packageName.contains("com.facebook.katana") || packageName.contains("com.google.android.apps.plus")) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, link);
                    intent.setPackage(packageName);
                    targetShareIntents.add(intent);
                }
            }
            if (!targetShareIntents.isEmpty()) {

                Intent chooserIntent = Intent.createChooser(targetShareIntents.remove(0), "Share via");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
                startActivity(chooserIntent);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Sorry, No sharing app is installed !")
                        .setMessage("                            ")
                        .show();
            }
        }

    }
}

