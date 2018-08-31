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
* `name`: base name of DataSource and JdbcTemplate beans, thus generating bean names in pattern `<name>DataSource` and `<name>JdbcTemplate`
* `property`: property name in `DataSourceProperties`; We reuse utilized classes `DataSourceProperties` and `DataSourceBuilder` in SpringBoot to construct DataSource and JdbcTemplate

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

* 2 DataSources with bean names: `aDataSource` and `bDataSource`
* 2 JdbcTemplates with bean names: `aJdbcTemplate` and `bJdbcTemplate`

### How To Inject DataSource/JdbcTemplate?

Beans depending on a DataSource or JdbcTemplate must be annotated by `DataSourceRouting`,
and `value` should be one of base names parsed from configuration properties.
Besides, to accept DataSource or JdbcTemplate specified by `DataSourceRouting`,
bean also must satisfied one of the following convention: 

* implement interface `DataSourceAware` or `JdbcTemplateAware`.
* own a method named as `setDataSource` or `setJdbcTemplate`.

Injection of DataSource and JdbcTemplate is processed by `DataSourceRoutingPostProcessor`,
which will detect interfaces and methods introduced above and do the injection. 

### Others You May Need To Know

Type of DataSource will be determined by SpringBoot automatically, just like `spring-boot-starter-jdbc` does.