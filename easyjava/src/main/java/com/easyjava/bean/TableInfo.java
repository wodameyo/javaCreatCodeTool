package com.easyjava.bean;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableInfo {

    /**
     * 表名
     */
    private String tableName;

    /**
     * Bean名
     */
    private String BeanName;

    /**
     * 参数名称
     */
    private String BeanParamName;

    /**
     * 表注释
     */
    private String Comment;

    /**
     * 字段信息
     */
    private List<FieldInfo> fieldList;
    /**
     * 扩展字段信息
     */
    private List<FieldInfo> extendFieldList;

    /**
     * 唯一索引集合
     */
    private Map<String,List<FieldInfo>> keyIndexMap = new LinkedHashMap<>();

    /**
     * 是否有date类型
     */
    private boolean haveDate;

    public List<FieldInfo> getExtendFieldList() {
        return extendFieldList;
    }

    public void setExtendFieldList(List<FieldInfo> extendFieldList) {
        this.extendFieldList = extendFieldList;
    }

    /**
     * 是否有时间类型
     */
    private boolean haveDateTime;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getBeanName() {
        return BeanName;
    }

    public void setBeanName(String beanName) {
        BeanName = beanName;
    }

    public String getBeanParamName() {
        return BeanParamName;
    }

    public void setBeanParamName(String beanParamName) {
        BeanParamName = beanParamName;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public List<FieldInfo> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<FieldInfo> fieldList) {
        this.fieldList = fieldList;
    }

    public Map<String, List<FieldInfo>> getKeyIndexMap() {
        return keyIndexMap;
    }

    public void setKeyIndexMap(Map<String, List<FieldInfo>> keyIndexMap) {
        this.keyIndexMap = keyIndexMap;
    }

    public boolean isHaveDate() {
        return haveDate;
    }

    public void setHaveDate(boolean haveDate) {
        this.haveDate = haveDate;
    }

    public boolean isHaveDateTime() {
        return haveDateTime;
    }

    public void setHaveDateTime(boolean haveDateTime) {
        this.haveDateTime = haveDateTime;
    }

    public boolean isHaveBigDecimal() {
        return haveBigDecimal;
    }

    public void setHaveBigDecimal(boolean haveBigDecimal) {
        this.haveBigDecimal = haveBigDecimal;
    }

    /**
     * 是否有BigDecimal类型
     */
    private boolean haveBigDecimal;

}
