package com.easyjava.builder;

import com.easyjava.bean.Constans;
import com.easyjava.bean.FieldInfo;
import com.easyjava.bean.TableInfo;
import com.easyjava.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;

public class BuildServiceImpl {
    private  static  final Logger logger = LoggerFactory.getLogger(BuildServiceImpl.class);
    public static void excute(TableInfo tableInfo) {
        File folder = new File(Constans.PATH_SERVICE_IMPL);
        if(!folder.exists()){
            folder.mkdirs();
        }

        String interfaceName = tableInfo.getBeanName()+"Service";
        String classname = tableInfo.getBeanName()+"ServiceImpl";
        String mapperName = StringUtils.uperCaseFirstLetter(tableInfo.getBeanName())+"Mapper";
        String mapperBeanName = StringUtils.lowerCaseFirstLetter(mapperName);
        File poFile = new File(folder, classname + ".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(out,"utf8");
            bw = new BufferedWriter(outw);

            bw.write("package "+Constans.PACKAGE_SERVICE_IMPL+";");
            bw.newLine();
            bw.newLine();


            if(tableInfo.isHaveDate() || tableInfo.isHaveDateTime()) {
                bw.write(Constans.BEAN_DATE_PARSE_CLASS);
                bw.newLine();
                bw.write(Constans.BEAN_DATE_FORMAT_CLASS);
                bw.newLine();
                bw.write("import " + Constans.PACKAGE_ENUMS+ ".DateTimePatternEnum;");
                bw.newLine();
                bw.write("import "+ Constans.PACKAGE_UTILS + ".DateUtils;");
                bw.newLine();
                bw.write("import java.util.Date;");
                bw.newLine();
            }
            bw.write("import java.io.Serializable;");
            bw.newLine();

            //引入PaginationResultVO
            bw.write("import "+Constans.PACKAGE_VO+"."+"PaginationResultVO;");
            bw.newLine();
            //引入PO
            bw.write("import "+Constans.PACKAGE_PO+"."+tableInfo.getBeanName()+";");
            bw.newLine();
            //引入Query
            bw.write("import "+Constans.PACKAGE_QUERY+"."+tableInfo.getBeanParamName()+";");
            bw.newLine();
            //引入Service
            bw.write("import "+Constans.PACKAGE_SERVICE+"."+interfaceName+";");
            bw.newLine();
            //引入Mapper
            bw.write("import "+Constans.PACKAGE_MAPPERS+"."+mapperName+";");
            bw.newLine();
            //引入分页相关包
            bw.write("import "+Constans.PACKAGE_QUERY+"."+"SimplePage;");
            bw.newLine();
            //引入PageSize
            bw.write("import "+Constans.PACKAGE_ENUMS+"."+"PageSize;");
            bw.newLine();
            //引入springframework.stereotype.Service
            bw.write("import org.springframework.stereotype.Service;");
            bw.newLine();
            //引入javax.annotation.Resource
            bw.write("import javax.annotation.Resource;");
            bw.newLine();
            //引入集合
            bw.write("import java.util.List;");
            bw.newLine();

            BuildComment.createClassComment(bw, tableInfo.getComment()+"ServiceImpl");
            bw.write("@Service(\""+StringUtils.lowerCaseFirstLetter(interfaceName)+"\")");
            bw.newLine();
            bw.write("public class "+classname+" implements "+interfaceName+" {");
            bw.newLine();
            bw.newLine();

            //开始生成内容
            bw.write("\t@Resource");
            bw.newLine();
            bw.write("\tprivate "+mapperName+"<"+tableInfo.getBeanName()+","+tableInfo.getBeanParamName()+"> "+StringUtils.lowerCaseFirstLetter(mapperName)+";");

            BuildComment.createFieldComment(bw, "根据条件查询列表");
            bw.write("\tpublic List<"+tableInfo.getBeanName()+"> findListByParam("+tableInfo.getBeanParamName()+" query) {");
            bw.newLine();
            bw.write("\t\treturn this."+mapperBeanName+".selectList(query);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            BuildComment.createFieldComment(bw, "根据条件查询数量");
            bw.write("\tpublic Integer findCountByParam("+tableInfo.getBeanParamName()+" query) {");
            bw.newLine();
            bw.write("\t\treturn this."+mapperBeanName+".selectCount(query);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            BuildComment.createFieldComment(bw, "分页查询");
            bw.write("\tpublic PaginationResultVO<"+tableInfo.getBeanName()+"> findListByPage("+tableInfo.getBeanParamName()+" query) {");
            bw.newLine();
            bw.write("\t\tInteger count = this.findCountByParam(query);");
            bw.newLine();
            bw.write("\t\tInteger pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();");
            bw.newLine();
            bw.write("\t\tSimplePage page = new SimplePage(query.getPageNo(), count, pageSize);");
            bw.newLine();
            bw.write("\t\tquery.setSimplePage(page);");
            bw.newLine();
            bw.write("\t\tList<"+tableInfo.getBeanName()+"> list = this.findListByParam(query);");
            bw.newLine();
            bw.write("\t\tPaginationResultVO<"+tableInfo.getBeanName()+"> result = new PaginationResultVO<"+tableInfo.getBeanName()+">();");
            bw.newLine();
            bw.write("\t\treturn null;");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            BuildComment.createFieldComment(bw, "新增");
            bw.write("\tpublic Integer add("+tableInfo.getBeanName()+" bean){");
            bw.newLine();
            bw.write("\t\treturn this."+mapperBeanName+".insert(bean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            BuildComment.createFieldComment(bw, "批量新增");
            bw.write("\tpublic Integer addBatch(List<"+tableInfo.getBeanName()+"> listBean) {");
            bw.newLine();
            bw.write("\t\tif (listBean == null || listBean.isEmpty()) {");
            bw.newLine();
            bw.write("\t\t\treturn 0;");
            bw.newLine();
            bw.write("\t\t}");
            bw.newLine();
            bw.write("\t\treturn this."+mapperBeanName+".insertBatch(listBean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            BuildComment.createFieldComment(bw, "批量新增或修改");
            bw.write("\tpublic Integer addOrUpdateBatch(List<"+tableInfo.getBeanName()+"> listbean) {");
            bw.newLine();
            bw.write("\t\tif (listbean == null || listbean.isEmpty()) {");
            bw.newLine();
            bw.write("\t\t\treturn 0;");
            bw.newLine();
            bw.write("\t\t}");
            bw.newLine();
            bw.write("\t\treturn this."+mapperBeanName+".insertOrUpdateBatch(listbean);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            for(Map.Entry<String, List<FieldInfo>> entry : tableInfo.getKeyIndexMap().entrySet()) {
                List<FieldInfo> keyFieldInfoList = entry.getValue();

                int index = 0;
                StringBuilder methodName = new StringBuilder();

                StringBuilder methodParam = new StringBuilder();

                StringBuilder paramNames = new StringBuilder();
                for(FieldInfo fieldInfo : keyFieldInfoList) {
                    index++;
                    methodName.append(StringUtils.uperCaseFirstLetter(fieldInfo.getPropertyName()));
                    if(index < keyFieldInfoList.size()) {
                        methodName.append("And");
                    }

                    methodParam.append(fieldInfo.getJavaType()+" " + fieldInfo.getPropertyName());

                    paramNames.append(fieldInfo.getPropertyName());
                    if (index < keyFieldInfoList.size()) {
                        methodParam.append(", ");
                        paramNames.append(", ");
                    }
                }
                BuildComment.createFieldComment(bw, "根据" + methodName + "查询");
                bw.write("\tpublic "+tableInfo.getBeanName()+" get"+tableInfo.getBeanName()+"By" + methodName + "(" + methodParam + ") {");
                bw.newLine();
                bw.write("\t\treturn this."+mapperBeanName+".selectBy"+methodName+"("+paramNames+");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
                bw.write("\tpublic Integer update"+tableInfo.getBeanName()+"By" + methodName + "("+tableInfo.getBeanName()+" bean, " + methodParam + ") {");
                bw.newLine();
                bw.write("\t\treturn this."+mapperBeanName+".updateBy"+methodName+"(bean,"+paramNames+");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\tpublic Integer delete"+tableInfo.getBeanName()+"By" + methodName + "(" + methodParam + ") {");
                bw.newLine();
                bw.write("\t\treturn this."+mapperBeanName+".deleteBy"+methodName+"("+paramNames+");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
            }

            bw.newLine();
            bw.write("}");
            bw.flush();
        }catch (Exception e) {
            logger.error("生成SERVICEIMPL失败",e);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (outw != null) {
                    outw.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
