package com.example.ioc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.annotation.BindView;
import com.example.annotation.OnClick;
import com.example.library.ButterKnife;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.tv2)
    TextView tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        tv.setText("aaa");
        tv2.setText("bbb");
    }

    @OnClick(R.id.tv)
    public void click(View view) {
        Toast.makeText(this, tv.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.tv2)
    public void click2(View view) {
        Toast.makeText(this, tv2.getText().toString(), Toast.LENGTH_SHORT).show();
    }
}
