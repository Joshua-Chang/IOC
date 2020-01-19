package com.wangyi.use_custom_dagger.apt_code;

import com.wangyi.custom_dagger.DoubleCheck;
import com.wangyi.custom_dagger.MembersInjector;
import com.wangyi.custom_dagger.Provider;
import com.wangyi.use_custom_dagger.MainActivity;
import com.wangyi.use_custom_dagger.Student;
import com.wangyi.use_custom_dagger.StudentComponent;
import com.wangyi.use_custom_dagger.StudentModule;

public class DaggerStudentComponent implements StudentComponent {
    public DaggerStudentComponent(Builder builder) {
        initialize(builder);
    }
    private Provider<Student> studentProvider;

    private MembersInjector<MainActivity> mainActivityMembersInjector;
    private void initialize(Builder builder) {
//        studentProvider=StudentModule_ProviderStudentFactory.create(builder.studentModule);
        //单例
        studentProvider= DoubleCheck.provider(StudentModule_ProviderStudentFactory.create(builder.studentModule));//此时Provider get出来的单例
        mainActivityMembersInjector=MainActivity_MembersInjector.create(studentProvider);
    }

    @Override
    public void inject(MainActivity activity) {
        mainActivityMembersInjector.injectMembers(activity);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static StudentComponent create() {
        return builder().build();
    }

    public static final class Builder {
        private StudentModule studentModule;

        private Builder() {
        }
        public DaggerStudentComponent build(){
            if (studentModule == null) {
                studentModule=new StudentModule();
            }
            return new DaggerStudentComponent(this);
        }
    }
}
