package top.fangwz.springboot.datasource;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: yuanwq
 * @date: 2018/8/28
 */
@Configuration
@EnableConfigurationProperties(MultiDataSourceProperties.class)
public class MultiDataSourceConfiguration {

  @Bean
  public MultiDataSourceRegistryPostProcessor multiDataSourceRegistryPostProcessor() {
    return new MultiDataSourceRegistryPostProcessor();
  }
}
