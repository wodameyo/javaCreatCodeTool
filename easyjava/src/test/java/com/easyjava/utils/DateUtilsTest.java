package com.easyjava.utils;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateUtilsTest {

    @Test
    void shouldParseDate() {
        Date date = DateUtils.parse("2026-10-25", DateUtils.YYYY_MM_DD);
        assertNotNull(date);
        assertEquals("2026-10-25", DateUtils.format(date, DateUtils.YYYY_MM_DD));
    }

    @Test
    void shouldThrowForInvalidDate() {
        assertThrows(IllegalArgumentException.class,
                () -> DateUtils.parse("2026-13-99", DateUtils.YYYY_MM_DD));
    }
}
