package com.easyjava.core.type;

import com.easyjava.core.config.GeneratorConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TypeMapperTest {

    @Test
    void normalizesTypeNames() {
        assertEquals("bigint", TypeMapper.normalize("BIGINT UNSIGNED"));
        assertEquals("varchar", TypeMapper.normalize("varchar(64)"));
        assertEquals("int", TypeMapper.normalize("int unsigned zerofill"));
        assertEquals("", TypeMapper.normalize(null));
    }

    @Test
    void mapsLegacyTypes() {
        assertEquals("Boolean", TypeMapper.javaType("tinyint(1)", GeneratorConfig.DateType.LEGACY));
        assertEquals("Boolean", TypeMapper.javaType("bit", GeneratorConfig.DateType.LEGACY));
        assertEquals("Integer", TypeMapper.javaType("int", GeneratorConfig.DateType.LEGACY));
        assertEquals("Long", TypeMapper.javaType("bigint", GeneratorConfig.DateType.LEGACY));
        assertEquals("BigInteger", TypeMapper.javaType("bigint unsigned", GeneratorConfig.DateType.LEGACY));
        assertEquals("BigDecimal", TypeMapper.javaType("decimal(10,2)", GeneratorConfig.DateType.LEGACY));
        assertEquals("String", TypeMapper.javaType("json", GeneratorConfig.DateType.LEGACY));
        assertEquals("byte[]", TypeMapper.javaType("blob", GeneratorConfig.DateType.LEGACY));
        assertEquals("Date", TypeMapper.javaType("datetime", GeneratorConfig.DateType.LEGACY));
        assertEquals("String", TypeMapper.javaType("geometry", GeneratorConfig.DateType.LEGACY));
    }

    @Test
    void mapsLocalDateTypes() {
        assertEquals("LocalDateTime", TypeMapper.javaType("timestamp", GeneratorConfig.DateType.LOCAL));
        assertEquals("LocalDate", TypeMapper.javaType("date", GeneratorConfig.DateType.LOCAL));
        assertEquals("LocalTime", TypeMapper.javaType("time", GeneratorConfig.DateType.LOCAL));
        assertEquals("java.time.LocalDateTime", TypeMapper.javaImport("timestamp", GeneratorConfig.DateType.LOCAL));
        assertEquals("java.util.Date", TypeMapper.javaImport("timestamp", GeneratorConfig.DateType.LEGACY));
        assertNull(TypeMapper.javaImport("varchar(10)", GeneratorConfig.DateType.LEGACY));
    }
}
