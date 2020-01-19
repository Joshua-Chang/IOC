package com.wangyi.use_custom_dagger.apt_code;

import com.wangyi.custom_dagger.Factory;
import com.wangyi.custom_dagger.Preconditions;
import com.wangyi.use_custom_dagger.Student;
import com.wangyi.use_custom_dagger.StudentModule;

public class StudentModule_ProviderStudentFactory implements Factory<Student> {

    private final StudentModule module;

    @Override
    public Student get() {
        return Preconditions.checkNotNull(
                module.providerStudent(), "Cannot return null from a non-@Nullable @Provides method");
    }

    public StudentModule_ProviderStudentFactory(StudentModule module) {
        assert module != null;
        this.module = module;
    }

    public static Factory<Student> create(StudentModule module) {
        return new StudentModule_ProviderStudentFactory(module);
    }
}
