package com.easyjava.core.metadata;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TableFilterTest {

    @Test
    void includesAllWhenIncludeEmpty() {
        TableFilter filter = new TableFilter(List.of(), List.of());
        assertTrue(filter.accept("any_table"));
    }

    @Test
    void supportsWildcards() {
        TableFilter filter = new TableFilter(List.of("ts_*"), List.of("*_log"));
        assertTrue(filter.accept("ts_user_info"));
        assertFalse(filter.accept("sys_user_info"));
        assertFalse(filter.accept("ts_user_log"));
    }

    @Test
    void excludeWins() {
        TableFilter filter = new TableFilter(List.of("user_?"), List.of("user_2"));
        assertTrue(filter.accept("user_1"));
        assertFalse(filter.accept("user_2"));
    }

    @Test
    void wildcardIsConvertedToRegex() {
        assertTrue("a.*b".matches(TableFilter.toRegex("a*b")));
    }
}
