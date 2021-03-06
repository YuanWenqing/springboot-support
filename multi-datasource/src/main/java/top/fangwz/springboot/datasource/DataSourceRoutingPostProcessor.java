package top.fangwz.springboot.datasource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.MethodInvocationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.google.common.base.Preconditions.*;

/**
 * @author: yuanwq
 * @date: 2018/8/2
 */
class DataSourceRoutingPostProcessor implements BeanPostProcessor, ApplicationContextAware {
  private static final String SETTER_DATA_SOURCE = "setDataSource";
  private static final String SETTER_JDBC_TEMPLATE = "setJdbcTemplate";

  private ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    DataSourceRouting routing =
        AnnotationUtils.findAnnotation(bean.getClass(), DataSourceRouting.class);
    if (routing == null) {
      return bean;
    }
    checkArgument(
        StringUtils.isNotBlank(routing.value()) && !StringUtils.containsWhitespace(routing.value()),
        "Illegal datasource name: " + routing.value() + " on " + bean.getClass().getName());
    DataSource dataSource = findDataSource(routing.value());
    JdbcTemplate jdbcTemplate = findJdbcTemplate(routing.value());
    boolean dsSet = false;
    boolean jtSet = false;
    if (bean instanceof DataSourceAware) {
      ((DataSourceAware) bean).setDataSource(dataSource);
      dsSet = true;
    } else {
      // try set datasource by setter
      dsSet = trySet(bean, SETTER_DATA_SOURCE, dataSource);
    }
    if (bean instanceof JdbcTemplateAware) {
      ((JdbcTemplateAware) bean).setJdbcTemplate(jdbcTemplate);
      jtSet = true;
    } else {
      // try set jdbcTemplate by setter
      jtSet = trySet(bean, SETTER_JDBC_TEMPLATE, jdbcTemplate);
    }
    if (!dsSet && !jtSet) {
      // nothing set, trigger an exception for unhandled DataSourceRouting annotation
      // TODO use a proper spring exception
      throw new RuntimeException("no way to set a DataSource or JdbcTemplate to bean " + bean,
          null);
    }
    return bean;
  }

  private boolean trySet(Object bean, String setterName, Object value) {
    Method method =
        MethodUtils.getMatchingAccessibleMethod(bean.getClass(), setterName, value.getClass());
    if (method == null) {
      return false;
    }
    try {
      method.invoke(bean, value);
      return true;
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new MethodInvocationException(new PropertyChangeEvent(bean, setterName, null, value),
          e);
    }
  }

  private JdbcTemplate findJdbcTemplate(String baseName) {
    String beanName = DataSourceUtils.generateJdbcTemplateBeanName(baseName);
    return applicationContext.getBean(beanName, JdbcTemplate.class);
  }

  private DataSource findDataSource(String baseName) {
    String beanName = DataSourceUtils.generateDataSourceBeanName(baseName);
    return applicationContext.getBean(beanName, DataSource.class);
  }

}
