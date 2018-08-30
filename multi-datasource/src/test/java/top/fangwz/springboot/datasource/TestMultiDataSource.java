package top.fangwz.springboot.datasource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
  private JdbcTemplate jdbcTemplate;

  @Test
  public void testProperties() {
    assertEquals(2, properties.getMulti().size());
  }

  @Test
  public void testJdbcTemplate() {
    Map<String, Object> map = jdbcTemplate.queryForMap("select * from ti where i =21");
    System.out.println(map);
  }
}
