<#import "common.ftl" as c>
package ${packages.po};

<#if table.haveTemporal>
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
<#if legacyDate>
import ${packages.enums}.DateTimePatternEnum;
import ${packages.utils}.DateUtils;
</#if>
</#if>
<#list table.javaImports as importName>
import ${importName};
</#list>
import java.io.Serializable;
<#if table.haveJsonIgnore>
import com.fasterxml.jackson.annotation.JsonIgnore;
</#if>
<#if lombok>
import lombok.Data;
</#if>

<@c.classComment description=table.docComment author=author date=timestamp />
<#if lombok>
@Data
</#if>
public class ${table.beanName} implements Serializable {
<#list table.columns as col>
    <@c.fieldComment comment=col.docComment />
<#if col.dateTimeType>
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
</#if>
<#if col.dateType>
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
</#if>
<#if col.jsonIgnore>
	@JsonIgnore
</#if>
	private ${col.javaType} ${col.propertyName};

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
	@Override
	public String toString() {
<#if table.columns?size == 0>
		return "${table.beanName}";
<#else>
		return <#list table.columns as col>${col.toStringExpression}<#if col?has_next> + ", " + </#if></#list>;
</#if>
	}
</#if>
}
