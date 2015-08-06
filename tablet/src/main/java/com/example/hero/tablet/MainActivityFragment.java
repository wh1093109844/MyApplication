package com.example.hero.tablet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private EditText mEditText;
    private Button mButton;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        attachView(v);
        attachListener();
        return v;
    }

    private void attachView(View v) {
        mEditText = (EditText) v.findViewById(R.id.edit_text);
        mButton = (Button) v.findViewById(R.id.button);
    }

    private void attachListener() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mEditText.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getActivity(), "姓名不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getActivity(), ResultActivity.class);
                    intent.putExtra("name", name);
                    startActivity(intent);
                }
            }
        });
    }
}
