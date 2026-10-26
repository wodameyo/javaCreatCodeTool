<#import "common.ftl" as c>
package ${packages.query};

<#list table.javaImports as importName>
import ${importName};
</#list>
<#if lombok>
import lombok.Data;
</#if>

<@c.classComment description=table.docComment + "查询对象" author=author date=timestamp />
<#if lombok>
@Data
</#if>
public class ${table.queryName} extends BaseQuery {
<#list table.columns as col>
    <@c.fieldComment comment=col.docComment />
	private ${col.javaType} ${col.propertyName};

<#if col.stringType>
	private ${col.javaType} ${col.fuzzyPropertyName};

</#if>
<#if col.temporal>
	private String ${col.timeStartPropertyName};

	private String ${col.timeEndPropertyName};

</#if>
</#list>
<#if !lombok>
<#list table.columns as col>
	public void set${col.upperPropertyName}(${col.javaType} ${col.propertyName}) {
		this.${col.propertyName} = ${col.propertyName};
	}

	public ${col.javaType} get${col.upperPropertyName}() {
		return this.${col.propertyName};
	}

</#list>
<#list table.columns as col>
<#if col.stringType>
	public void set${col.fuzzyPropertyName?cap_first}(${col.javaType} ${col.fuzzyPropertyName}) {
		this.${col.fuzzyPropertyName} = ${col.fuzzyPropertyName};
	}

	public ${col.javaType} get${col.fuzzyPropertyName?cap_first}() {
		return this.${col.fuzzyPropertyName};
	}

</#if>
<#if col.temporal>
	public void set${col.timeStartPropertyName?cap_first}(String ${col.timeStartPropertyName}) {
		this.${col.timeStartPropertyName} = ${col.timeStartPropertyName};
	}

	public String get${col.timeStartPropertyName?cap_first}() {
		return this.${col.timeStartPropertyName};
	}

	public void set${col.timeEndPropertyName?cap_first}(String ${col.timeEndPropertyName}) {
		this.${col.timeEndPropertyName} = ${col.timeEndPropertyName};
	}

	public String get${col.timeEndPropertyName?cap_first}() {
		return this.${col.timeEndPropertyName};
	}

</#if>
</#list>
</#if>
}
