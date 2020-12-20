package com.peng.cssys.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {
    /**
     * 部门别名
     * @return
     */
    public String deptAlias() default "";

    /**
     * 用户别名
     * @return
     */
    public String userAlias() default "";
}
