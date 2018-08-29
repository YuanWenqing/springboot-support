package top.fangwz.springboot.datasource;

import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author: yuanwq
 * @date: 2018/8/29
 */
@ConfigurationProperties("top.fangwz.springboot.datasource")
@Data
public class MultiDataSourceProperties {

  private Map<String, DataSourceProperties> multi = Maps.newLinkedHashMap();

}
