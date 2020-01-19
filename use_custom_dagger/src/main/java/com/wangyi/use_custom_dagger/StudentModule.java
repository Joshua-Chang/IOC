package com.wangyi.use_custom_dagger;

import com.wangyi.custom_dagger.ann.Module;
import com.wangyi.custom_dagger.ann.Providers;

@Module
public class StudentModule {
    @Providers
    public Student providerStudent(){
        return new Student();
    }
}
