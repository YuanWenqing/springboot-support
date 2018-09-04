package top.fangwz.springboot.datasource;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Properties;

import static com.google.common.base.Preconditions.*;

/**
 * @author: yuanwq
 * @date: 2018/8/30
 */
class PropertiesParser {
  private static final String DOT = ".";
  private static final Splitter SPLITTER = Splitter.on(DOT).trimResults();

  private String prefix = StringUtils.EMPTY;
  private final MultiDataSourceProperties multiDataSourceProperties =
      new MultiDataSourceProperties();

  public void setPrefix(String prefix) {
    checkNotNull(prefix);
    checkArgument(!StringUtils.containsWhitespace(prefix));
    checkArgument(!prefix.startsWith(DOT) && !prefix.endsWith(DOT));
    this.prefix = prefix;
  }

  public MultiDataSourceProperties parse(Properties properties) {
    String prefixDot = StringUtils.isBlank(prefix) ? StringUtils.EMPTY : prefix + DOT;
    for (Object key : properties.keySet()) {
      String keyStr = String.valueOf(key);
      if (keyStr.startsWith(prefixDot)) {
        List<String> parts = SPLITTER.splitToList(StringUtils.substringAfter(keyStr, prefixDot));
        checkArgument(parts.size() == 3, String
            .format("invalid property: %s, should be %s.<dsName>.<configName>.<propName>", key,
                prefix));
        String dsName = parts.get(0);
        String configName = parts.get(1);
        String propName = parts.get(2);
        String propValue = properties.getProperty(keyStr);
        multiDataSourceProperties.putProperty(dsName, configName, propName, propValue);
      }
    }
    return multiDataSourceProperties;
  }

}
