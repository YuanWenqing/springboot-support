package top.fangwz.springboot.datasource;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author: yuanwq
 * @date: 2018/8/28
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MultiDataSourceAutoConfiguration.class)
public @interface EnableMultiDataSource {
}
