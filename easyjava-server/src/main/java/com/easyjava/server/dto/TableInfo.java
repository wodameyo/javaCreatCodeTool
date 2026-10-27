package com.easyjava.server.dto;

/**
 * 表清单条目，行数为 information_schema 的估算值。
 */
public record TableInfo(String name, String comment, long rowCount, int columnCount) {
}
