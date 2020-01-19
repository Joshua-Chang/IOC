package com.wangyi.use_custom_dagger;

import com.wangyi.custom_dagger.ann.Component;

@Component(modules = StudentModule.class)
public interface StudentComponent {
    void inject(MainActivity activity);
}

