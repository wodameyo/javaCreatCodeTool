<#import "common.ftl" as c>
package ${packages.mappers};

import ${packages.po}.${table.beanName};
import ${packages.query}.${table.queryName};
<#list table.javaImports as importName>
import ${importName};
</#list>
import org.apache.ibatis.annotations.Param;

<@c.classComment description=table.docComment + "Mapper" author=author date=timestamp />
public interface ${table.mapperName} extends BaseMapper<${table.beanName}, ${table.queryName}> {
<#list table.indexes as idx>

    <@c.fieldComment comment="根据" + idx.methodSuffix + "查询" />
	${table.beanName} selectBy${idx.methodSuffix}(${idx.mapperParamDeclaration});

    <@c.fieldComment comment="根据" + idx.methodSuffix + "更新" />
	Integer updateBy${idx.methodSuffix}(@Param("bean") ${table.beanName} bean, ${idx.mapperParamDeclaration});

    <@c.fieldComment comment="根据" + idx.methodSuffix + "删除" />
	Integer deleteBy${idx.methodSuffix}(${idx.mapperParamDeclaration});
</#list>
}
