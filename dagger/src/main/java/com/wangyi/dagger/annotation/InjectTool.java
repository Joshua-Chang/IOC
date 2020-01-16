package com.wangyi.dagger.annotation;

import android.util.Log;
import android.view.View;

import com.wangyi.dagger.annotation_common.OnBaseCommon;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InjectTool {

    private static final String TAG = InjectTool.class.getSimpleName();

    public static void inject(Object object) {
        injectSetContentView(object);

        injectBindView(object);
        injectClick(object);
        injectEvent(object); // 兼容Android一系列事件
    }

    /**
     * 兼容Android一系列事件，考虑到扩展
     */
    private static void injectEvent(final Object mainActivityObject) {
        Class<?> mainActivityClass = mainActivityObject.getClass();

        // 遍历MainActivity所有的方法
        Method[] declaredMethods = mainActivityClass.getDeclaredMethods();
        for (final Method eventMethod : declaredMethods) {
            eventMethod.setAccessible(true);

            Annotation[] annotations = eventMethod.getAnnotations();
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                OnBaseCommon common = annotationType.getAnnotation(OnBaseCommon.class);
                if (common == null) {
                    continue;
                }
                String listener = common.setCommonListener();
                Class listenerClass = common.setCommonObjectListener();

                try {
                    Method valueMethod = annotationType.getDeclaredMethod("value");
                    valueMethod.setAccessible(true);
                    int id = (int) valueMethod.invoke(annotation);

                    Method findViewByIdMethod = mainActivityClass.getMethod("findViewById", int.class);
                    final View resultView = (View) findViewByIdMethod.invoke(mainActivityObject, id); // 执行此函数---findViewById(R.id.bt_test1);

                    /**
                     *         button.setOnLongClickListener(new View.OnLongClickListener() {
                     *             @Override
                     *             public boolean onLongClick(View v) {
                     *                 return false;
                     *             }
                     *         });
                     */
                    final Method setListenerMethod = resultView.getClass().getMethod(listener, listenerClass);

                    Object proxy = Proxy.newProxyInstance(
                            listenerClass.getClassLoader(),
                            new Class[]{listenerClass},
                            new InvocationHandler() {
                                @Override
                                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                    // 执行MainActivity里面的方法
                                    return eventMethod.invoke(mainActivityObject, null);
                                }
                            }
                    );
                    setListenerMethod.invoke(resultView, proxy);
                    /**
                     *         button.setOnLongClickListener(new View.OnLongClickListener() {
                     *             @Override
                     *             public boolean onLongClick(View v) {
                     *                 return false;
                     *             }
                     *         });
                     */


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 把布局里面的控件ID 和 Activity方法绑定起来，建立事件
     * @param object == MainActivity
     */
    private static void injectClick(final Object object) {

        Class<?> mainActivityClass = object.getClass();

        // 遍历MainActivity所有的方法
        Method[] declaredMethods = mainActivityClass.getDeclaredMethods();
        for (final Method method : declaredMethods) {
            method.setAccessible(true);
            final Click click = method.getAnnotation(Click.class);
            if (click == null) {
                continue;
            }
            int id = click.value();
            try {
                Method findViewByIdMethod = mainActivityClass.getMethod("findViewById", int.class);
                final View resultView = (View) findViewByIdMethod.invoke(object, id); // 执行此函数---findViewById(R.id.bt_test1);
                resultView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (method.getParameterCount()==0) {
                                method.invoke(object);
                            }else {
                                method.invoke(object,resultView);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

        /**
         * 把一系列控件注入到 Activity中去
         * @param object == MainActivity
         */
    private static void injectBindView(Object object) {

        Class<?> mainActivityClass = object.getClass();

        // TODO 遍历MainActivity里面所有的注解 （字段上的）
        // 遍历 MainActivity里面所有的字段
        Field[] fields = mainActivityClass.getDeclaredFields();

        for (Field field : fields) { // Button button1;   TextView textView;   String string;
            field.setAccessible(true);

            BindView bindView = field.getAnnotation(BindView.class);
            if (null == bindView) {
                Log.d(TAG, "BindView is null");
                continue; // 结束本次循环，进入下一个循环
            }

            // get R.id.bt_test1
            int viewID = bindView.value();

            // 把控件给实例化出来
            // button1 = findViewById(R.id.bt_test1);

            try {
                Method findViewByIdMethod = mainActivityClass.getMethod("findViewById", int.class);
                Object resultView = findViewByIdMethod.invoke(object, viewID); // 执行此函数---findViewById(R.id.bt_test1);

                // 给我们的字段，字段赋值了
                // button1 = findViewById(R.id.bt_test1);
                // filed = findViewById(viewID);
                field.set(object, resultView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 把布局注入到 Activity中去
     * @param object == MainActivity
     */
    private static void injectSetContentView(Object object) {

        Class<?> mMainActivityClass = object.getClass();

        // 拿到MainActivity里面的（ContentView）注解
        ContentView mContentView = mMainActivityClass.getAnnotation(ContentView.class);

        if (null == mContentView) {
            Log.d(TAG, "ContentView is null ");
            return;
        }

        // 拿到用户设置的布局ID
        int layoutID = mContentView.value();

        // 我们需要执行 setContentView(R.layout.activity_main); 把布局注入到Activity
        try {
            Method setContentViewMethod = mMainActivityClass.getMethod("setContentView", int.class);
            setContentViewMethod.invoke(object, layoutID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
