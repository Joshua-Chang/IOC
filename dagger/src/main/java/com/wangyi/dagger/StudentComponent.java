package com.wangyi.dagger;

import dagger.Component;

@Component(modules = StudentModule.class)
public interface StudentComponent {
    void injectMainActivity(MainActivity activity);
}
