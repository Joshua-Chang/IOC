package com.wangyi.use_custom_dagger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.wangyi.custom_dagger.ann.Inject;
import com.wangyi.use_custom_dagger.apt_code.DaggerStudentComponent;

public class MainActivity extends AppCompatActivity {
    @Inject
    public Student student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaggerStudentComponent.create().inject(this);
        Log.d("MainActivity", "student.hashCode():" + student.hashCode());
    }
}
