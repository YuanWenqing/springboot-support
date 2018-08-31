# springboot-support
some extended supports for SpringBoot

## multi-datasource

Support multi-datasource for SpringBoot.

### How To Enable Multi-DataSource Support?

Just do it in a SpringBoot way:

~~~java
@Configuration
@EnableMultiDataSource
public class XXXConfiguration {
  // code...
}
~~~

or

~~~java
@SpringBootApplication
@EnableMultiDataSource
public class XXXApplication {
  // code...
}
~~~

In purpose to specify your configuration of multi-datasource, inside `EnableMultiDataSource` you can change:

* `location`: location of configuration properties, default `classpath:application.properties`.
* `prefix`: prefix of configuration, default `multi-datasource.multi`.
* `loader`: class of loader to load configuration properties, default `DefaultPropertiesLoader`; If some ConfigurationService, like Spring Cloud Config or Aliyun ACM, is in use, change `loader` to a customized loader class implementing `PropertiesLoader` interface to load it in your way.  

### How To Configure Multi-DataSource?

Configuration properties is parsed by `PropertiesParser`. As a convention, every item will be treated in a `<prefix>.<name>.<property>` pattern:

* `prefix`: `prefix` defined in `EnableMultiDataSource`
* `name`: prefix of bean name for DataSource and JdbcTemplate, thus generating bean names in pattern `<name>DataSource` and `<name>JdbcTemplate`
* `property`: property name in `DataSourceProperties`; We reuse utilized classes `DataSourceProperties` and `DataSourceBuilder` in SpringBoot to construct `DataSource` and `JdbcTemplate`

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

### Others You May Need To Know

Type of DataSource will be determined by SpringBoot automatically, just like `spring-boot-starter-jdbc` does.