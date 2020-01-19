package com.wangyi.dagger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wangyi.dagger.annotation.BindView;
import com.wangyi.dagger.annotation.Click;
import com.wangyi.dagger.annotation.ContentView;
import com.wangyi.dagger.annotation.InjectTool;
import com.wangyi.dagger.annotation_common.OnClickCommon;
import com.wangyi.dagger.annotation_common.OnClickLongCommon;
import com.wangyi.dagger.annotation_common.OnDragCommon;

import javax.inject.Inject;
@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    @Inject
    Student student;
    @BindView(R.id.bt_test1)
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        InjectTool.inject(this);

//        student=new Student();
//        DaggerStudentComponent.create().injectMainActivity(this);
        button.setText("aaaaa");

    }
    @Click(R.id.bt_test2)
    public void bt2(){
        Toast.makeText(this, "hhh", Toast.LENGTH_SHORT).show();
    }
    @Click(R.id.bt_test3)
    public void bt3(View view){
        Toast.makeText(this, "xxx", Toast.LENGTH_SHORT).show();
    }

    //////////////////////////////////////////////// 下面是兼容事件代码

    // 点击事件
//    @Deprecated
    @OnClickCommon(R.id.bt_t1) // 变化的
    private void test111() {
        Toast.makeText(this, "兼容 点击事件 run", Toast.LENGTH_SHORT).show();


        // 我们需要动态变化事件  事件三要素
//        Button button = null;
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        button.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                return false;
//            }
//        });
//
//        button.setOnDragListener(new View.OnDragListener() {
//            @Override
//            public boolean onDrag(View v, DragEvent event) {
//                return false;
//            }
//        });

        // todo 事件三要素1 订阅方式  setOnClickListener， setOnLongClickListener  ...

        // todo 事件三要素2 事件源对象 View.OnClickListener，  View.OnLongClickListener  ...

        // todo 事件三要素3 具体执行的方法（消费事件的方法）   onClick(View v) ，  onLongClick(View v)
    }

    // 长按事件
    @OnClickLongCommon(R.id.bt_t2)  // 变化的
    private boolean test222() {
        Toast.makeText(this, "兼容 长按事件 run", Toast.LENGTH_SHORT).show();
        return false;
    }

@OnDragCommon(R.id.bt_t3)
    private boolean test3333() {
        Toast.makeText(this, "兼容 test3333 run", Toast.LENGTH_SHORT).show();
        return false;
    }
}
