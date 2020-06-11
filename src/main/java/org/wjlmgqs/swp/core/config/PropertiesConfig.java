package org.wjlmgqs.swp.core.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySourcesPropertyResolver;

@Slf4j
@Configuration
@PropertySource(value = {"classpath:error.properties"})
public class PropertiesConfig implements EnvironmentAware {


    private static PropertySourcesPropertyResolver propertySourcesPropertyResolver;

    @Override
    public void setEnvironment(Environment environment) {
        this.propertySourcesPropertyResolver = new PropertySourcesPropertyResolver(((ConfigurableEnvironment) environment).getPropertySources());
    }

    public PropertySourcesPropertyResolver getPropertyResolver(){
        return propertySourcesPropertyResolver;
    }

    public void setPropertyResolver(PropertySourcesPropertyResolver propertySourcesPropertyResolver) {
        this.propertySourcesPropertyResolver = propertySourcesPropertyResolver;
    }


    public static String getContextProperty(String name,String... msgs) {
        if (null != name && !StringUtils.isBlank(propertySourcesPropertyResolver.getProperty(name))) {
            String msg = propertySourcesPropertyResolver.getProperty(name);
            if (null != msgs && msgs.length>0) {
                for (int i=0;i<msgs.length;i++){
                    msg = msg.replace("{"+i+"}",msgs[i]);
                }
            }
            return msg;
        }
        return "未定义错误";
    }

    public static String getContextProperty(String name) {
        if (null != name && !StringUtils.isBlank(propertySourcesPropertyResolver.getProperty(name))) {
            return propertySourcesPropertyResolver.getProperty(name);
        }
        return "未定义错误";
    }


}
