package com.example.hero.tablet;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

/**
 * Created by hero on 8/5/2015.
 */
public class ResultActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mTextView = (TextView) findViewById(R.id.text);
        String name = getIntent().getStringExtra("name");
        if (!TextUtils.isEmpty(name)) {
            mTextView.setText("你好，" + name + "!");
        }
    }
}
