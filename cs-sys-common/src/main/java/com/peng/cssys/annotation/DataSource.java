package com.peng.cssys.annotation;

import com.peng.cssys.annotation.enums.DataSourceType;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DataSource {
    /**
     * 切换数据源
     */
    public DataSourceType value() default DataSourceType.MASTER;
}
