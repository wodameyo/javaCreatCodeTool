package com.easyjava.builder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuildTableTypeTest {

    @Test
    void shouldMapCommonTypes() {
        assertEquals("Boolean", BuildTable.processJavaType("tinyint(1)"));
        assertEquals("Integer", BuildTable.processJavaType("tinyint(4)"));
        assertEquals("Integer", BuildTable.processJavaType("smallint(6)"));
        assertEquals("Integer", BuildTable.processJavaType("mediumint(9)"));
        assertEquals("Long", BuildTable.processJavaType("bigint"));
        assertEquals("BigInteger", BuildTable.processJavaType("bigint unsigned"));
        assertEquals("BigDecimal", BuildTable.processJavaType("decimal(10,2)"));
        assertEquals("String", BuildTable.processJavaType("varchar(64)"));
        assertEquals("String", BuildTable.processJavaType("json"));
        assertEquals("String", BuildTable.processJavaType("enum('a','b')"));
        assertEquals("Date", BuildTable.processJavaType("datetime(3)"));
        assertEquals("Date", BuildTable.processJavaType("date"));
        assertEquals("byte[]", BuildTable.processJavaType("longblob"));
        assertEquals("Boolean", BuildTable.processJavaType("bit(1)"));
    }

    @Test
    void shouldFallbackToStrForUnknownType() {
        assertEquals("String", BuildTable.processJavaType("geometry"));
    }

    @Test
    void shouldNormalizeType() {
        assertEquals("bigint", BuildTable.normalizeSqlType("BIGINT UNSIGNED"));
        assertEquals("datetime", BuildTable.normalizeSqlType("datetime(3)"));
        assertEquals("", BuildTable.normalizeSqlType(null));
    }

    @Test
    void shouldProcessFieldName() {
        assertEquals("UserInfo", BuildTable.processField("user_info", true));
        assertEquals("userInfo", BuildTable.processField("user_info", false));
        assertEquals("Id", BuildTable.processField("id", true));
    }
}
