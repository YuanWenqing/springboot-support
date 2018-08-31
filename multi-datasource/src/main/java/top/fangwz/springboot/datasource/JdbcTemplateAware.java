package top.fangwz.springboot.datasource;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author: yuanwq
 * @date: 2018/8/2
 */
public interface JdbcTemplateAware extends DataSourceRoutingAware {

  void setJdbcTemplate(JdbcTemplate jdbcTemplate);
}
