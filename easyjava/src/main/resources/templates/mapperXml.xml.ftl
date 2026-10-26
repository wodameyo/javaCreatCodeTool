<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${packages.mappers}.${table.mapperName}">

	<!--实体映射-->
	<resultMap id="base_result_map" type="${packages.po}.${table.beanName}">
<#list table.columns as col>
<#if col.primaryKey>
		<id column="${col.columnName}" property="${col.propertyName}" />
<#else>
		<result column="${col.columnName}" property="${col.propertyName}" />
</#if>
</#list>
	</resultMap>

	<!--通用查询结果列-->
	<sql id="Base_Column_List">
		${table.columnsCsv}
	</sql>

	<!--基础查询条件-->
	<sql id="Base_Query_Condition">
<#list table.columns as col>
		<if test="query.${col.propertyName} != null<#if col.stringType> and query.${col.propertyName} != ''</#if>">
			and t.${col.columnName} = #{query.${col.propertyName}}
		</if>
</#list>
	</sql>

	<!--扩展查询条件-->
	<sql id="Base_Query_Condition_Extend">
<#list table.columns as col>
<#if col.stringType>
		<if test="query.${col.fuzzyPropertyName} != null and query.${col.fuzzyPropertyName} != ''">
			and t.${col.columnName} like concat('%', #{query.${col.fuzzyPropertyName}}, '%')
		</if>
</#if>
<#if col.temporal>
		<if test="query.${col.timeStartPropertyName} != null and query.${col.timeStartPropertyName} != ''">
			and t.${col.columnName} >= str_to_date(#{query.${col.timeStartPropertyName}}, '%Y-%m-%d')
		</if>
		<if test="query.${col.timeEndPropertyName} != null and query.${col.timeEndPropertyName} != ''">
			and t.${col.columnName} &lt; date_sub(str_to_date(#{query.${col.timeEndPropertyName}}, '%Y-%m-%d'), interval 1 day)
		</if>
</#if>
</#list>
	</sql>

	<!--通用查询条件-->
	<sql id="Query_Condition">
		<where>
			<include refid="Base_Query_Condition"/>
			<include refid="Base_Query_Condition_Extend"/>
		</where>
	</sql>

	<!--查询列表-->
	<select id="selectList" resultMap="base_result_map">
		SELECT <include refid="Base_Column_List"/> FROM ${table.tableName} t<include refid="Query_Condition"/>
		<if test="query.orderBy!=null and query.orderBy!=''">order by ${r"${query.orderBy}"}</if>
		<if test="query.simplePage!=null">limit #{query.simplePage.start},#{query.simplePage.pageSize}</if>
	</select>

	<!--查询数量-->
	<select id="selectCount" resultType="java.lang.Integer">
		SELECT count(1) FROM ${table.tableName} t<include refid="Query_Condition"/>
	</select>

	<!--单条插入(匹配有值的字段)-->
<#if autoIncrementColumn??>
	<insert id="insert" useGeneratedKeys="true" keyProperty="bean.${autoIncrementColumn.propertyName}">
<#else>
	<insert id="insert">
</#if>
		INSERT INTO ${table.tableName}
		<trim prefix="(" suffix=")" suffixOverrides=",">
<#list table.columns as col>
			<if test="bean.${col.propertyName} != null">
				${col.columnName},
			</if>
</#list>
		</trim>
		<trim prefix="values (" suffix=")" suffixOverrides=",">
<#list table.columns as col>
			<if test="bean.${col.propertyName} != null">
				#{bean.${col.propertyName}},
			</if>
</#list>
		</trim>
	</insert>

	<!--插入或更新(匹配有值的字段)-->
<#if autoIncrementColumn??>
	<insert id="insertOrUpdate" useGeneratedKeys="true" keyProperty="bean.${autoIncrementColumn.propertyName}">
<#else>
	<insert id="insertOrUpdate">
</#if>
		INSERT INTO ${table.tableName}
		<trim prefix="(" suffix=")" suffixOverrides=",">
<#list table.columns as col>
			<if test="bean.${col.propertyName} != null">
				${col.columnName},
			</if>
</#list>
		</trim>
		VALUES
		<trim prefix="(" suffix=")" suffixOverrides=",">
<#list table.columns as col>
			<if test="bean.${col.propertyName} != null">
				#{bean.${col.propertyName}},
			</if>
</#list>
		</trim>
		ON DUPLICATE KEY UPDATE
		<trim suffixOverrides=",">
<#list table.columns as col>
<#if !col.keyColumn>
			<if test="bean.${col.propertyName} != null">
				${col.columnName} = VALUES(${col.columnName}),
			</if>
</#if>
</#list>
		</trim>
	</insert>

	<!--批量插入-->
<#if table.insertableColumns?size gt 0>
<#if autoIncrementColumn??>
	<insert id="insertBatch" parameterType="java.util.List" useGeneratedKeys="true" keyProperty="${autoIncrementColumn.propertyName}">
<#else>
	<insert id="insertBatch" parameterType="java.util.List">
</#if>
		INSERT INTO ${table.tableName}( <#list table.insertableColumns as col>${col.columnName}<#if col?has_next>,</#if></#list> )VALUES
		<foreach collection="list" item="item" separator="," >
			(<#list table.insertableColumns as col>#{item.${col.propertyName}}<#if col?has_next>,</#if></#list>)
		</foreach>
	</insert>
<#else>
	<!--表 ${table.tableName} 只有自增列，未生成批量插入语句-->
</#if>

	<!--批量插入或更新-->
<#if table.insertableColumns?size gt 0>
<#if autoIncrementColumn??>
	<insert id="insertOrUpdateBatch" parameterType="java.util.List" useGeneratedKeys="true" keyProperty="${autoIncrementColumn.propertyName}">
<#else>
	<insert id="insertOrUpdateBatch" parameterType="java.util.List">
</#if>
		INSERT INTO ${table.tableName} (<#list table.insertableColumns as col>${col.columnName}<#if col?has_next>,</#if></#list>) VALUES
		<foreach collection="list" item="item" separator="," >
			(<#list table.insertableColumns as col>#{item.${col.propertyName}}<#if col?has_next>,</#if></#list>)
		</foreach>
		ON DUPLICATE KEY UPDATE
		<#list table.insertableColumns as col>${col.columnName} = VALUES(${col.columnName})<#if col?has_next>,</#if></#list>
	</insert>
<#else>
	<!--表 ${table.tableName} 只有自增列，未生成批量插入或更新语句-->
</#if>

<#list table.indexes as idx>
	<!--根据${idx.methodSuffix}查询-->
	<select id="selectBy${idx.methodSuffix}" resultMap="base_result_map">
		SELECT <include refid="Base_Column_List"/> from ${table.tableName} where ${idx.whereClause}
	</select>

	<!--根据${idx.methodSuffix}更新-->
	<update id="updateBy${idx.methodSuffix}">
		UPDATE ${table.tableName}
		<set>
<#list table.insertableColumns as col>
			<if test="bean.${col.propertyName} != null">
				${col.columnName} = #{bean.${col.propertyName}},
			</if>
</#list>
		</set>
		where ${idx.whereClause}
	</update>

	<!--根据${idx.methodSuffix}删除-->
	<delete id="deleteBy${idx.methodSuffix}">
		DELETE FROM ${table.tableName} where ${idx.whereClause}
	</delete>

</#list>
</mapper>
