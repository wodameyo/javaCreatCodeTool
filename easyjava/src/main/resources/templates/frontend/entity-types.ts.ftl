import type { PageQuery, PaginationResultVO } from './common'

/**
 * ${table.docComment?js_string}
 */
export interface ${table.beanName} {
<#list table.columns as column>
  /** ${column.docComment?js_string} */
  ${column.propertyName}?: ${column.tsType}
</#list>
}

export interface ${table.queryName} extends PageQuery {
<#list table.searchColumns as column>
<#if column.stringType>
  ${column.fuzzyPropertyName}: string
<#elseif column.temporal>
  ${column.timeStartPropertyName}: string
  ${column.timeEndPropertyName}: string
<#else>
  ${column.propertyName}: string
</#if>
</#list>
}

export type ${table.beanName}Page = PaginationResultVO<${table.beanName}>
