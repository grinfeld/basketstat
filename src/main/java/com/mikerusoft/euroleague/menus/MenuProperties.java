package com.mikerusoft.euroleague.menus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "menu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuProperties {

    private List<Menu> menus;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Menu {
        private String name;
        private String view;
    }
}
