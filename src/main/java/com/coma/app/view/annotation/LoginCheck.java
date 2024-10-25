package com.coma.app.view.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 어노테이션이 메서드에 적용될 수 있도록 설정
@Retention(RetentionPolicy.RUNTIME) // 런타임 시에 어노테이션 정보가 유지되도록 설정
public @interface LoginCheck {}
