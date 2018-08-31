# springboot-support
some extension and support for spring boot

## multi-datasource

Support multi-datasource for SpringBoot.

### EnableMultiDataSource

* `location`: location of configuration properties, default `classpath:application.properties`
* `prefix`: prefix of configuration, default `multi-datasource.multi`
* `locader`: class of loader to load configuration properties, default `DefaultPropertiesLoader`; If some ConfigurationService, like Spring Cloud Config or Aliyun ACM, is used, developer can change `loader` to a customized loader class implementing `PropertiesLoader` interface  

To enable multi-datasource support, just do it in a SpringBoot way:

~~~java
@EnableMultiDataSource(location="classpath:")
public class Configuration {
  // beans
}
~~~

### Configuration

Example:

~~~properties
multi-datasource.multi.a.url = jdbc:mysql://localhost:3306/test
multi-datasource.multi.a.username=test
multi-datasource.multi.a.password=123456
multi-datasource.multi.a.driver-class-name=com.mysql.cj.jdbc.Driver

multi-datasource.multi.b.url = jdbc:mysql://localhost:3306/test
multi-datasource.multi.b.username=test
multi-datasource.multi.b.password=123456
multi-datasource.multi.b.driver-class-name=com.mysql.cj.jdbc.Driver
~~~

This will construct:

* 2 `DataSource`s with bean names: `aDataSource` and `bDataSource`
* 2 `JdbcTemplate`s with bean names: `aJdbcTemplate` and `bJdbcTemplate`

### Others

Type of DataSource will be determined by SpringBoot automatically, just like `spring-boot-starter-jdbc` does.