package com.easyjava.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StringUtilsTest {

    @Test
    void shouldUpperCaseFirstLetter() {
        assertEquals("UserInfo", StringUtils.uperCaseFirstLetter("userInfo"));
    }

    @Test
    void shouldLowerCaseFirstLetter() {
        assertEquals("userInfo", StringUtils.lowerCaseFirstLetter("UserInfo"));
    }

    @Test
    void shouldReturnEmptyForEmptyInput() {
        assertEquals("", StringUtils.uperCaseFirstLetter(""));
        assertEquals("", StringUtils.lowerCaseFirstLetter(""));
        assertNull(StringUtils.uperCaseFirstLetter(null));
    }
}
