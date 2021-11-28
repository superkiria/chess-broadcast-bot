package com.github.superkiria.props;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:prop.secret")
public class SecretProps {

    @Value( "${bot.key}" )
    private String key;

    @Value( "${bot.name}" )
    private String name;

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }
}
