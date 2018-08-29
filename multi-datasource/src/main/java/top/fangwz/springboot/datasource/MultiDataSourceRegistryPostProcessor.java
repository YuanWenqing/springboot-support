package top.fangwz.springboot.datasource;

import com.google.common.base.Suppliers;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @author: yuanwq
 * @date: 2018/8/29
 */
public class MultiDataSourceRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
  private MultiDataSourceProperties properties;

  public void setProperties(MultiDataSourceProperties properties) {
    this.properties = properties;
  }

  @Override
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
      throws BeansException {
    if (properties == null) {
      return;
    }
    for (Map.Entry<String, DataSourceProperties> entry : properties.getMulti().entrySet()) {
      String beanName = entry.getKey() + "DataSource";
      DataSource dataSource = entry.getValue().initializeDataSourceBuilder().build();
      BeanDefinition beanDefinition = BeanDefinitionBuilder
          .genericBeanDefinition(DataSource.class, Suppliers.ofInstance(dataSource))
          .getBeanDefinition();
      registry.registerBeanDefinition(beanName, beanDefinition);
    }
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
  }
}
