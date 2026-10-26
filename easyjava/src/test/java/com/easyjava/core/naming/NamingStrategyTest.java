package com.easyjava.core.naming;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NamingStrategyTest {

    @Test
    void convertsSnakeCaseToCamelCase() {
        assertEquals("userName", NamingStrategy.toPropertyName("user_name"));
        assertEquals("isDel", NamingStrategy.toPropertyName("is_del"));
        assertEquals("id", NamingStrategy.toPropertyName("id"));
        assertEquals("createDate", NamingStrategy.toPropertyName("create-date"));
    }

    @Test
    void stripsMatchingTablePrefix() {
        assertEquals("UserInfo", NamingStrategy.toBeanName("ts_user_info", List.of("ts_")));
        assertEquals("UserInfo", NamingStrategy.toBeanName("ts_user_info", List.of("t_", "ts_")));
        assertEquals("UserInfo", NamingStrategy.toBeanName("sys_user_info", List.of("sys_", "ts_")));
        assertEquals("TsUserInfo", NamingStrategy.toBeanName("ts_user_info", List.of()));
    }

    @Test
    void handlesEmptyInput() {
        assertEquals("", NamingStrategy.toPropertyName(null));
        assertEquals("", NamingStrategy.toPropertyName(""));
        assertEquals("", NamingStrategy.capitalize(""));
        assertEquals("", NamingStrategy.lowerFirst(""));
    }
}
