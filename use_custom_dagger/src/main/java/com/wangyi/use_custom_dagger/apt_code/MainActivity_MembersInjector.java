package com.wangyi.use_custom_dagger.apt_code;

import com.wangyi.custom_dagger.MembersInjector;
import com.wangyi.custom_dagger.Provider;
import com.wangyi.use_custom_dagger.MainActivity;
import com.wangyi.use_custom_dagger.Student;

public class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
    private final Provider<Student> studentProvider;

    public MainActivity_MembersInjector(Provider<Student> studentProvider) {
        this.studentProvider = studentProvider;
    }
    public static MembersInjector<MainActivity> create(Provider<Student> studentProvider) {
        return new MainActivity_MembersInjector(studentProvider);
    }

    @Override
    public void injectMembers(MainActivity instance) {
        if (instance == null) {
            throw new NullPointerException("Cannot inject members into a null reference");
        }
        instance.student = studentProvider.get();
    }
}
