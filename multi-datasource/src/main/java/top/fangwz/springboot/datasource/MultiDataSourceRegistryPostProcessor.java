package top.fangwz.springboot.datasource;

import com.google.common.base.Suppliers;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;

/**
 * TODO: 用ConfigurationProperties的方式读取配置，注入在这里处理是不可行的
 * ConfigurationProperties是在ConfigurationPropertiesBindingPostProcessor中才处理,
 * 所以只能使用BeanPostProcessor的方式，且优先级低于ConfigurationPropertiesBindingPostProcessor
 *
 * @author: yuanwq
 * @date: 2018/8/29
 */
public class MultiDataSourceRegistryPostProcessor implements ImportBeanDefinitionRegistrar {

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
      BeanDefinitionRegistry registry) {
    MultiDataSourceProperties properties = new MultiDataSourceProperties();
    DataSourceProperties dataSourceProperties = new DataSourceProperties();
    dataSourceProperties.setUrl("jdbc:mysql://localhost:3306/test");
    dataSourceProperties.setUsername("test");
    dataSourceProperties.setPassword("123456");
    dataSourceProperties.setDriverClassName("com.mysql.cj.jdbc.Driver");
    properties.addDataSourceProperties("a", dataSourceProperties);
    for (Map.Entry<String, DataSourceProperties> entry : properties.getMulti().entrySet()) {
      String dataSourceName = entry.getKey() + "DataSource";
      DataSource dataSource = entry.getValue().initializeDataSourceBuilder().build();
      BeanDefinition dataSourceBean = BeanDefinitionBuilder
          .genericBeanDefinition(DataSource.class, Suppliers.ofInstance(dataSource))
          .getBeanDefinition();
      registry.registerBeanDefinition(dataSourceName, dataSourceBean);
      BeanDefinition jdbcBean = BeanDefinitionBuilder.genericBeanDefinition(JdbcTemplate.class)
          .addConstructorArgReference(dataSourceName).getBeanDefinition();
      String jdbcName = entry.getKey() + "JdbcTemplate";
      registry.registerBeanDefinition(jdbcName, jdbcBean);
    }
  }

  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
  }
}
