package top.fangwz.springboot.datasource;

import com.google.common.collect.Sets;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.util.Properties;
import java.util.Set;

import static com.google.common.base.Preconditions.*;

/**
 * @author: yuanwq
 * @date: 2018/8/30
 */
@Setter
public class PropertiesParser {
  private static final String DOT = ".";

  private String prefix;

  private Set<String> findAllNames(Properties properties) {
    Set<String> names = Sets.newLinkedHashSet();
    String prefixDot = StringUtils.isBlank(prefix) ? prefix : prefix + DOT;
    for (Object key : properties.keySet()) {
      String keyStr = String.valueOf(key);
      if (keyStr.startsWith(prefixDot)) {
        String name = StringUtils.substringBetween(keyStr, prefixDot, DOT);
        checkArgument(StringUtils.isNotBlank(name), "invalid name: " + name + ", key: " + keyStr);
        names.add(name);
      }
    }
    return names;
  }

  public void parse(Properties properties, MultiDataSourceProperties multiDataSourceProperties) {
    Set<String> names = findAllNames(properties);
    for (String name : names) {
      String prefixDot = StringUtils.isBlank(prefix) ? name + DOT : prefix + DOT + name + DOT;
      DataSourceProperties dataSourceProperties = parseDataSourceProperties(properties, prefixDot);
      multiDataSourceProperties.addDataSourceProperties(name, dataSourceProperties);
    }
  }

  private DataSourceProperties parseDataSourceProperties(Properties properties, String prefixDot) {
    DataSourceProperties dataSourceProperties = new DataSourceProperties();
    for (Object key : properties.keySet()) {
      String keyStr = String.valueOf(key);
      if (keyStr.startsWith(prefixDot)) {
        String propName = StringUtils.substringAfter(keyStr, prefixDot);
        setDataSourceProperties(dataSourceProperties, propName, properties.getProperty(keyStr));
      }
    }
    return dataSourceProperties;
  }

  private void setDataSourceProperties(DataSourceProperties dataSourceProperties, String propName,
      String propValue) {
    // TODO: use spring-way to set property
    switch (propName) {
      case "url":
        dataSourceProperties.setUrl(propValue);
        break;
      case "username":
        dataSourceProperties.setUsername(propValue);
        break;
      case "password":
        dataSourceProperties.setPassword(propValue);
        break;
      case "driver-class-name":
        dataSourceProperties.setDriverClassName(propValue);
        break;
      default:
        break;
    }
  }
}
