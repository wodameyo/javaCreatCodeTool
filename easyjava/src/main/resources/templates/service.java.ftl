<#import "common.ftl" as c>
package ${packages.service};

import ${packages.po}.${table.beanName};
import ${packages.query}.${table.queryName};
import ${packages.vo}.PaginationResultVO;
import java.util.List;

<@c.classComment description=table.docComment + "Service" author=author date=timestamp />
public interface ${table.serviceName} {

    /**
     * 根据条件查询列表
     */
	List<${table.beanName}> findListByParam(${table.queryName} query);

    /**
     * 根据条件查询数量
     */
	Integer findCountByParam(${table.queryName} query);

    /**
     * 分页查询
     */
	PaginationResultVO<${table.beanName}> findListByPage(${table.queryName} query);

    /**
     * 新增
     */
	Integer add(${table.beanName} bean);

    /**
     * 批量新增
     */
	Integer addBatch(List<${table.beanName}> listBean);

    /**
     * 批量新增或修改
     */
	Integer addOrUpdateBatch(List<${table.beanName}> listBean);
<#list table.indexes as idx>

    <@c.fieldComment comment="根据" + idx.methodSuffix + "查询" />
	${table.beanName} get${table.beanName}By${idx.methodSuffix}(${idx.paramDeclaration});

    <@c.fieldComment comment="根据" + idx.methodSuffix + "更新" />
	Integer update${table.beanName}By${idx.methodSuffix}(${table.beanName} bean, ${idx.paramDeclaration});

    <@c.fieldComment comment="根据" + idx.methodSuffix + "删除" />
	Integer delete${table.beanName}By${idx.methodSuffix}(${idx.paramDeclaration});
</#list>
}
