package top.fangwz.springboot.datasource;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static com.google.common.base.Preconditions.*;

/**
 * @author: yuanwq
 * @date: 2018/8/30
 */
public class PropertiesParser {
  private static final String DOT = ".";

  private String prefix = StringUtils.EMPTY;

  public void setPrefix(String prefix) {
    checkNotNull(prefix);
    checkArgument(!StringUtils.containsWhitespace(prefix));
    checkArgument(!prefix.startsWith(DOT) && !prefix.endsWith(DOT));
    this.prefix = prefix;
  }

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
    Map<String, String> map = new HashMap<>();
    for (Object key : properties.keySet()) {
      String keyStr = String.valueOf(key);
      if (keyStr.startsWith(prefixDot)) {
        String propName = StringUtils.substringAfter(keyStr, prefixDot);
        map.put(propName, properties.getProperty(keyStr));
      }
    }
    bind(dataSourceProperties, map);
    return dataSourceProperties;
  }

  private void bind(DataSourceProperties dataSourceProperties, Map<String, String> map) {
    // learn from DataSourceBuilder.build()
    ConfigurationPropertySource source = new MapConfigurationPropertySource(map);
    ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases();
    aliases.addAliases("url", "jdbc-url");
    aliases.addAliases("username", "user");
    Binder binder = new Binder(source.withAliases(aliases));
    binder.bind(ConfigurationPropertyName.EMPTY, Bindable.ofInstance(dataSourceProperties));
  }

}
