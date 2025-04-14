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

public class BuildController {
    private  static  final Logger logger = LoggerFactory.getLogger(BuildController.class);
    public static void excute(TableInfo tableInfo) {
        File folder = new File(Constans.PATH_CONTROLLER);
        if(!folder.exists()){
            folder.mkdirs();
        }

        String classname = tableInfo.getBeanName()+"Controller";
        String mapperName = StringUtils.uperCaseFirstLetter(tableInfo.getBeanName())+"Mapper";
        String serviceName = StringUtils.uperCaseFirstLetter(tableInfo.getBeanName())+"Service";
        String mapperBeanName = StringUtils.lowerCaseFirstLetter(mapperName);
        File poFile = new File(folder, classname + ".java");

        OutputStream out = null;
        OutputStreamWriter outw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(poFile);
            outw = new OutputStreamWriter(out,"utf8");
            bw = new BufferedWriter(outw);

            bw.write("package "+Constans.PACKAGE_CONTROLLER+";");
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

            //引入PO
            bw.write("import "+Constans.PACKAGE_PO+"."+tableInfo.getBeanName()+";");
            bw.newLine();
            //引入Query
            bw.write("import "+Constans.PACKAGE_QUERY+"."+tableInfo.getBeanParamName()+";");
            bw.newLine();
            //引入Mapper
            bw.write("import "+Constans.PACKAGE_MAPPERS+"."+mapperName+";");
            bw.newLine();
            //引入service
            bw.write("import "+Constans.PACKAGE_SERVICE+"."+serviceName+";");
            bw.newLine();
            //引入ResponseVO
            bw.write("import "+Constans.PACKAGE_VO+"."+"ResponseVO;");
            bw.newLine();
            //引入springframework.stereotype.Service
            bw.write("import org.springframework.stereotype.Service;");
            bw.newLine();
            //引入SpringFrameworkRequestMapping
            bw.write("import org.springframework.web.bind.annotation.RequestMapping;");
            bw.newLine();
            //引入RestController
            bw.write("import org.springframework.web.bind.annotation.RestController;");
            bw.newLine();
            //引入RequestBody
            bw.write("import org.springframework.web.bind.annotation.RequestBody;");
            bw.newLine();
            //引入javax.annotation.Resource
            bw.write("import javax.annotation.Resource;");
            bw.newLine();
            //引入集合
            bw.write("import java.util.List;");
            bw.newLine();

            BuildComment.createClassComment(bw, tableInfo.getComment()+"Controller");
            bw.newLine();
            bw.write("@RestController");
            bw.newLine();
            bw.write("@RequestMapping(\""+StringUtils.uperCaseFirstLetter(tableInfo.getBeanName())+"\")");
            bw.newLine();
            bw.write("public class "+classname+" extends ABaseController{");
            bw.newLine();
            bw.newLine();

            //开始生成内容
            bw.write("\t@Resource");
            bw.newLine();
            bw.write("\tprivate "+serviceName+" "+serviceName+";");
            bw.newLine();
            BuildComment.createFieldComment(bw, "根据条件分页查询");
            bw.write("\t@RequestMapping(\"loadDataList\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO loadDataList("+tableInfo.getBeanParamName()+" query) {");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO("+serviceName+".findListByPage(query));");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "新增");
            bw.write("\t@RequestMapping(\"add\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO add("+tableInfo.getBeanName()+" bean){");
            bw.newLine();
            bw.write("\t\tthis." + serviceName + ".add(bean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "批量新增");
            bw.write("\t@RequestMapping(\"addBatch\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO addBatch(@RequestBody List<"+tableInfo.getBeanName()+"> listBean) {");
            bw.newLine();
            bw.write("\t\tthis." + serviceName + ".addBatch(listBean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
            bw.newLine();

            BuildComment.createFieldComment(bw, "批量新增或修改");
            bw.write("\t@RequestMapping(\"addOrUpdateBatch\")");
            bw.newLine();
            bw.write("\tpublic ResponseVO addOrUpdateBatch(@RequestBody List<"+tableInfo.getBeanName()+"> listbean) {");
            bw.newLine();
            bw.write("\t\tthis." + serviceName + ".addOrUpdateBatch(listbean);");
            bw.newLine();
            bw.write("\t\treturn getSuccessResponseVO(null);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();
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
                bw.write("\t@RequestMapping(\"get"+tableInfo.getBeanName()+"By" + methodName + "\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO get"+tableInfo.getBeanName()+"By" + methodName + "(" + methodParam + ") {");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(this."+serviceName+".get"+tableInfo.getBeanName()+"By"+methodName+"("+paramNames+"));");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "更新");
                bw.write("\t@RequestMapping(\"update"+tableInfo.getBeanName()+"By" + methodName + "\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO update"+tableInfo.getBeanName()+"By" + methodName + "("+tableInfo.getBeanName()+" bean, " + methodParam + ") {");
                bw.newLine();
                bw.write("\t\tthis."+serviceName+".update"+tableInfo.getBeanName()+"By"+methodName+"(bean,"+paramNames+");");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(null);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
                bw.newLine();

                BuildComment.createFieldComment(bw, "根据" + methodName + "删除");
                bw.write("\t@RequestMapping(\"delete"+tableInfo.getBeanName()+"By" + methodName + "\")");
                bw.newLine();
                bw.write("\tpublic ResponseVO delete"+tableInfo.getBeanName()+"By" + methodName + "(" + methodParam + ") {");
                bw.newLine();
                bw.write("\t\tthis."+serviceName+".delete"+tableInfo.getBeanName()+"By"+methodName+"("+paramNames+");");
                bw.newLine();
                bw.write("\t\treturn getSuccessResponseVO(null);");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();
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
