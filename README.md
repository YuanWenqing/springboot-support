# springboot-support
some extended supports for SpringBoot

## multi-datasource

Support multi-datasource for SpringBoot.

### EnableMultiDataSource

* `location`: location of configuration properties, default `classpath:application.properties`
* `prefix`: prefix of configuration, default `multi-datasource.multi`
* `loader`: class of loader to load configuration properties, default `DefaultPropertiesLoader`; If some ConfigurationService, like Spring Cloud Config or Aliyun ACM, is in use, developer can change `loader` to a customized loader class implementing `PropertiesLoader` interface  

To enable multi-datasource support, just do it in a SpringBoot way:

~~~java
@EnableMultiDataSource(location="classpath:")
public class Configuration {
  // beans
}
~~~

or

~~~java
@SpringBootApplication
@EnableMultiDataSource(location="classpath:")
public class XXXApplication {
  // beans
}
~~~

### Configuration

Configuration will be parsed by `PropertiesParser`. As a convention, every item will be treated in a `<prefix>.<name>.<property>` pattern:

* `prefix`: `prefix` defined in `EnableMultiDataSource`
* `name`: prefix of bean name for DataSource and JdbcTemplate, thus generating `<name>DataSource` and `<name>JdbcTemplate`
* `property`: property name in `DataSourceProperties`

We reuse utilized classes `DataSourceProperties` and `DataSourceBuilder` in SpringBoot to construct `DataSource` and `JdbcTemplate`

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

This will lead to construction of:

* 2 `DataSource`s with bean names: `aDataSource` and `bDataSource`
* 2 `JdbcTemplate`s with bean names: `aJdbcTemplate` and `bJdbcTemplate`

### Others

Type of DataSource will be determined by SpringBoot automatically, just like `spring-boot-starter-jdbc` does.