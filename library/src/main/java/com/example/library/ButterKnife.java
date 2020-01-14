package com.example.library;

import android.app.Activity;
/**
 * 核心类：接口 = 接口实现类
 * ButterKnife用的是构造方法.newInstance()
 * 我们使用的是直接调用接口实现类的方法viewBinder.bind(activity);
 */
public class ButterKnife {

    public static void bind(Activity activity) {
        // 拼接类名，如：MainActivity$ViewBinder
        String className = activity.getClass().getName() + "$ViewBinder";
        try {
            // 加载上述拼接类（可能apt生成失败，这里会抛出ClassNotFountException异常）
            Class<?> viewBindClass = Class.forName(className);
            // 接口 = 接口实现类
            ViewBinder viewBinder = (ViewBinder) viewBindClass.newInstance();
            // 调用接口方法
            viewBinder.bind(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
