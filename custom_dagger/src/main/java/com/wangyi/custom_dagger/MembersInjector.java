package com.wangyi.custom_dagger;

public interface MembersInjector <T>{
    void injectMembers(T instance);//注入的位置MainActivity
}
