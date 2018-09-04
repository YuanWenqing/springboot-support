package top.fangwz.springboot.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

/**
 * @author: yuanwq
 * @date: 2018/8/30
 */
public class TestPropertiesParser {
  @Test
  public void testPrefix() {
    PropertiesParser parser = new PropertiesParser();
    parser.setPrefix("p");
    Properties properties = new Properties();
    properties.setProperty("p.a.datasource.url", "aaa");
    properties.setProperty("p.a.datasource.type", HikariDataSource.class.getName());
    MultiDataSourceProperties multiDataSourceProperties = parser.parse(properties);
    assertEquals(1, multiDataSourceProperties.getMulti().size());
    assertNotNull(multiDataSourceProperties.getDataSourceProperties("a"));
    assertEquals("aaa", multiDataSourceProperties.getDataSourceProperties("a").getUrl());
    assertEquals(HikariDataSource.class,
        multiDataSourceProperties.getDataSourceProperties("a").getType());
  }

  @Test
  public void testEmptyPrefix() {
    PropertiesParser parser = new PropertiesParser();
    parser.setPrefix("");
    Properties properties = new Properties();
    properties.setProperty("a.datasource.url", "aaa");
    properties.setProperty("a.datasource.type", HikariDataSource.class.getName());
    MultiDataSourceProperties multiDataSourceProperties = parser.parse(properties);
    assertEquals(1, multiDataSourceProperties.getMulti().size());
    assertNotNull(multiDataSourceProperties.getDataSourceProperties("a"));
    assertEquals("aaa", multiDataSourceProperties.getDataSourceProperties("a").getUrl());
    assertEquals(HikariDataSource.class,
        multiDataSourceProperties.getDataSourceProperties("a").getType());
  }

  @Test
  public void testWrongType() {
    PropertiesParser parser = new PropertiesParser();
    Properties properties = new Properties();
    properties.setProperty("a.datasource.type", "a");
    try {
      MultiDataSourceProperties multiDataSourceProperties = parser.parse(properties);
      multiDataSourceProperties.getDataSourceProperties("a");
      fail();
    } catch (RuntimeException e) {
      System.err.println(e.getClass() + ": " + e.getMessage());
    }
  }

  @Test
  public void testIllegalPrefix() {
    PropertiesParser parser = new PropertiesParser();
    try {
      parser.setPrefix(null);
      fail();
    } catch (NullPointerException e) {
    }
    try {
      parser.setPrefix("a b");
      fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      parser.setPrefix(".");
      fail();
    } catch (IllegalArgumentException e) {
    }
    try {
      parser.setPrefix("a.");
      fail();
    } catch (IllegalArgumentException e) {
    }

  }
}
