package top.fangwz.springboot.datasource;

import javax.sql.DataSource;

/**
 * @author: yuanwq
 * @date: 2018/8/3
 */
public interface DataSourceAware extends DataSourceRoutingAware{
  void setDataSource(DataSource dataSource);
}