package top.fangwz.springboot.datasource;

import com.google.common.collect.Maps;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: yuanwq
 * @date: 2018/8/29
 */
@Getter
class MultiDataSourceProperties {

  private Map<String, CompositeProperties> multi = Maps.newLinkedHashMap();

  public void putProperty(String dsName, String configName, String propName, String propValue) {
    CompositeProperties compositeProperties =
        multi.computeIfAbsent(dsName, k -> new CompositeProperties());
    Map<String, String> config =
        compositeProperties.propertiesMap.computeIfAbsent(configName, k -> new HashMap<>());
    config.put(propName, propValue);
  }

  public DataSourceProperties getDataSourceProperties(String dataSourceBaseName) {
    CompositeProperties compositeProperties = multi.get(dataSourceBaseName);
    if (compositeProperties == null) {
      return null;
    }
    return compositeProperties.getDataSourceProperties();
  }

  static class CompositeProperties {
    Map<String, Map<String, String>> propertiesMap = new LinkedHashMap<>();

    public DataSourceProperties getDataSourceProperties() {
      Map<String, String> properties =
          propertiesMap.getOrDefault(ConfigNaming.DATASOURCE, Collections.emptyMap());
      // learn from DataSourceBuilder.build()
      DataSourceProperties dataSourceProperties = new DataSourceProperties();
      ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
      ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases();
      aliases.addAliases("url", "jdbc-url");
      aliases.addAliases("username", "user");
      Binder binder = new Binder(source.withAliases(aliases));
      binder.bind(ConfigurationPropertyName.EMPTY, Bindable.ofInstance(dataSourceProperties));
      return dataSourceProperties;
    }

    public void bindProperty(DataSource dataSource) {
      Map<String, String> properties = null;
      if ("com.zaxxer.hikari.HikariDataSource".equals(dataSource.getClass().getName())) {
        properties = propertiesMap.get(ConfigNaming.HIKARI);
      } else if ("org.apache.tomcat.jdbc.pool.DataSource".equals(dataSource.getClass().getName())) {
        properties = propertiesMap.get(ConfigNaming.TOMCAT);
      } else if ("org.apache.commons.dbcp2.BasicDataSource"
          .equals(dataSource.getClass().getName())) {
        properties = propertiesMap.get(ConfigNaming.DBCP2);
      }
      if (properties != null && !properties.isEmpty()) {
        ConfigurationPropertySource source = new MapConfigurationPropertySource(properties);
        Binder binder = new Binder(source);
        binder.bind(ConfigurationPropertyName.EMPTY, Bindable.ofInstance(dataSource));
      }
    }
  }

  private interface ConfigNaming {
    String DATASOURCE = "datasource";
    String HIKARI = "hikari";
    String TOMCAT = "tomcat";
    String DBCP2 = "dbcp2";
  }

  /**
   * 用来生成{@code application.properties}中的配置项提示，不用做逻辑中
   * <p>
   * {@code DataSource}类型在实际使用中才能确定，这里只用做编译。
   * <p>
   * 注意：命名方式与 {@link ConfigNaming} 定义保持一致
   */
  @ConfigurationProperties("multi-datasource")
  @Data
  private static class MultiConfigurationProperties {
    @Data
    private static class SingleConfigurationProperties {
      private DataSourceProperties datasource;
      private com.zaxxer.hikari.HikariDataSource hikari;
      private org.apache.tomcat.jdbc.pool.DataSource tomcat;
      private org.apache.commons.dbcp2.BasicDataSource dbcp2;
    }

    private Map<String, SingleConfigurationProperties> multi = Collections.emptyMap();
  }
}
