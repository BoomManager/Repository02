package com.javawxid.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
//自定义注解，在其他方法上加了@LoginRequired之后，才可以生效
@Target(ElementType.METHOD)//注解范围为方法
@Retention(RetentionPolicy.RUNTIME)//注解为运行时
public @interface LoginRequired {

    boolean isNeedLogin() default true;//是否必须登录，默认为true
}
