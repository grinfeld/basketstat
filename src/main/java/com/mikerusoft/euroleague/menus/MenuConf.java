package com.mikerusoft.euroleague.menus;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({MenuProperties.class})
public class MenuConf {
}
