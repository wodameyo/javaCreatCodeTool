<#import "common.ftl" as c>
package ${packages.serviceImpl};

import ${packages.po}.${table.beanName};
import ${packages.query}.${table.queryName};
import ${packages.query}.SimplePage;
import ${packages.mappers}.${table.mapperName};
import ${packages.service}.${table.serviceName};
import ${packages.vo}.PaginationResultVO;
import ${packages.enums}.PageSize;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
<#if transaction>
import org.springframework.transaction.annotation.Transactional;
</#if>

<@c.classComment description=table.docComment + "ServiceImpl" author=author date=timestamp />
@Service("${table.serviceBeanName}")
<#if transaction>
@Transactional(rollbackFor = Exception.class)
</#if>
public class ${table.serviceImplName} implements ${table.serviceName} {

	@Autowired
	private ${table.mapperName} ${table.mapperBeanName};

	@Override
	public List<${table.beanName}> findListByParam(${table.queryName} query) {
		return this.${table.mapperBeanName}.selectList(query);
	}

	@Override
	public Integer findCountByParam(${table.queryName} query) {
		return this.${table.mapperBeanName}.selectCount(query);
	}

	@Override
	public PaginationResultVO<${table.beanName}> findListByPage(${table.queryName} query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<${table.beanName}> list = this.findListByParam(query);
		return new PaginationResultVO<${table.beanName}>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
	}

	@Override
	public Integer add(${table.beanName} bean) {
		return this.${table.mapperBeanName}.insert(bean);
	}

	@Override
	public Integer addBatch(List<${table.beanName}> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.${table.mapperBeanName}.insertBatch(listBean);
	}

	@Override
	public Integer addOrUpdateBatch(List<${table.beanName}> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.${table.mapperBeanName}.insertOrUpdateBatch(listBean);
	}
<#list table.indexes as idx>

	@Override
	public ${table.beanName} get${table.beanName}By${idx.methodSuffix}(${idx.paramDeclaration}) {
		return this.${table.mapperBeanName}.selectBy${idx.methodSuffix}(${idx.paramNames});
	}

	@Override
	public Integer update${table.beanName}By${idx.methodSuffix}(${table.beanName} bean, ${idx.paramDeclaration}) {
		return this.${table.mapperBeanName}.updateBy${idx.methodSuffix}(bean, ${idx.paramNames});
	}

	@Override
	public Integer delete${table.beanName}By${idx.methodSuffix}(${idx.paramDeclaration}) {
		return this.${table.mapperBeanName}.deleteBy${idx.methodSuffix}(${idx.paramNames});
	}
</#list>
}
