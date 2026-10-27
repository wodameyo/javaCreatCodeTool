package com.easyjava.server.dto;

import java.util.List;

/**
 * 索引信息，列按 seq_in_index 排序。
 */
public record IndexInfo(String name, boolean primary, List<String> columns) {
}
