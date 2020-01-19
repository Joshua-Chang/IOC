package com.wangyi.use_custom_dagger.apt_code;

import com.wangyi.custom_dagger.Factory;
import com.wangyi.use_custom_dagger.Student;

public enum  Student_Factory implements Factory<Student> {
    INSTANCE;

    @Override
    public Student get() {
        return new Student();
    }

    public static Factory create() {
        return INSTANCE;
    }

}
