package top.fangwz.springboot.datasource;

import org.h2.util.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author: yuanwq
 * @date: 2018/8/29
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = TestMultiDataSource.ConfigurationForTest.class)
public class TestMultiDataSource {
  @Configuration
  @EnableMultiDataSource
  public static class ConfigurationForTest {

  }

  @Autowired
  private MultiDataSourceProperties properties;
  @Autowired
  @Qualifier("aJdbcTemplate")
  private JdbcTemplate aJdbcTemplate;
  @Autowired
  @Qualifier("bJdbcTemplate")
  private JdbcTemplate bJdbcTemplate;

  @Test
  public void testProperties() {
    assertEquals(2, properties.getMulti().size());
  }

  @Test
  public void testJdbcTemplate() throws IOException {
    String sql = initSql();
    aJdbcTemplate.execute(sql);
    aJdbcTemplate.execute(
        "create table user (`id` BIGINT(20) NOT NULL AUTO_INCREMENT,`name` VARCHAR(20) DEFAULT 0,)");
    List<String> list = aJdbcTemplate.queryForList("show tables;", String.class);
    System.out.println(list);
    Map<String, Object> map = aJdbcTemplate.queryForMap("select * from user where id =1");
    System.out.println(map);
  }

  private String initSql() throws IOException {
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("init.sql");
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    IOUtils.copy(inputStream, outputStream);
    return new String(outputStream.toByteArray());
  }
}
