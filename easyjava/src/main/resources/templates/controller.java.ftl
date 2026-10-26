<#import "common.ftl" as c>
package ${packages.controller};

import ${packages.po}.${table.beanName};
import ${packages.query}.${table.queryName};
import ${packages.service}.${table.serviceName};
import ${packages.vo}.ResponseVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

<@c.classComment description=table.docComment + "Controller" author=author date=timestamp />
@RestController
@RequestMapping("/${table.requestMapping}")
public class ${table.controllerName} extends ABaseController {

	@Autowired
	private ${table.serviceName} ${table.serviceBeanName};

	/**
     * 根据条件分页查询
     */
	@RequestMapping("/loadDataList")
	public ResponseVO<?> loadDataList(${table.queryName} query) {
		return getSuccessResponseVO(this.${table.serviceBeanName}.findListByPage(query));
	}

	/**
     * 新增
     */
	@RequestMapping("/add")
	public ResponseVO<?> add(@RequestBody ${table.beanName} bean) {
		this.${table.serviceBeanName}.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
     * 批量新增
     */
	@RequestMapping("/addBatch")
	public ResponseVO<?> addBatch(@RequestBody List<${table.beanName}> listBean) {
		this.${table.serviceBeanName}.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
     * 批量新增或修改
     */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO<?> addOrUpdateBatch(@RequestBody List<${table.beanName}> listBean) {
		this.${table.serviceBeanName}.addOrUpdateBatch(listBean);
		return getSuccessResponseVO(null);
	}
<#list table.indexes as idx>

	/**
     * 根据${idx.methodSuffix}查询
     */
	@RequestMapping("/get${table.beanName}By${idx.methodSuffix}")
	public ResponseVO<?> get${table.beanName}By${idx.methodSuffix}(${idx.paramDeclaration}) {
		return getSuccessResponseVO(this.${table.serviceBeanName}.get${table.beanName}By${idx.methodSuffix}(${idx.paramNames}));
	}

	/**
     * 根据${idx.methodSuffix}更新
     */
	@RequestMapping("/update${table.beanName}By${idx.methodSuffix}")
	public ResponseVO<?> update${table.beanName}By${idx.methodSuffix}(@RequestBody ${table.beanName} bean, ${idx.paramDeclaration}) {
		this.${table.serviceBeanName}.update${table.beanName}By${idx.methodSuffix}(bean, ${idx.paramNames});
		return getSuccessResponseVO(null);
	}

	/**
     * 根据${idx.methodSuffix}删除
     */
	@RequestMapping("/delete${table.beanName}By${idx.methodSuffix}")
	public ResponseVO<?> delete${table.beanName}By${idx.methodSuffix}(${idx.paramDeclaration}) {
		this.${table.serviceBeanName}.delete${table.beanName}By${idx.methodSuffix}(${idx.paramNames});
		return getSuccessResponseVO(null);
	}
</#list>
}
