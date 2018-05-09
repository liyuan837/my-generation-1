package com.gen.domain;


import com.gen.conf.Config;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: LiYuan
 * @Description:
 * @Date: 17:59 2018/5/9
 */
public class BeanFile {

    /**
     * 继承类名
     */
    private String extendsClassName;

    /**
     * 完整的包名
     */
    private String packageName;

    /**
     * 类名
     */
    private String className;

    /**
     * 注释
     */
    private String comment;

    /**
     * 属性列表
     */
    private List<ColumnProperties> propertiesList;

    /**
     * 引入的包
     */
    private List<String> importList;

    /**
     * bean类型: 0 普通pojo(po和condition), 1 createForm和queryForm, 2 updateForm, 3 vo
     */
    private int beanType = 0;

    /**
     * 所在模块名
     */
    private String moduleName;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<ColumnProperties> getPropertiesList() {
        return propertiesList;
    }

    public void setPropertiesList(List<ColumnProperties> propertiesList) {
        this.propertiesList = propertiesList;
    }

    public List<String> getImportList() {
        return importList;
    }

    public void setImportList(List<String> importList) {
        this.importList = importList;
    }

    public String getExtendsClassName() {
        return extendsClassName;
    }

    public void setExtendsClassName(String extendsClassName) {
        this.extendsClassName = extendsClassName;
    }

    public boolean isForm() {
        return beanType != 0;
    }

    public void setBeanType(int beanType) {
        this.beanType = beanType;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getFilePath() {
        String path =  Config.projectPath + moduleName + Config.javaPath + packageName.replaceAll("\\.", "/") + "/";
        return path;
    }

    /**
     * 生成Bean代码
     *
     * @return
     * @throws SQLException
     */
    public String toString() {

        List<ColumnProperties> columnPropertiesList = new ArrayList<>();
        columnPropertiesList.addAll(propertiesList);

        StringBuilder data = new StringBuilder();

        data.append("@Data\r\n");
        if (this.isForm()) {
            data.append("@ApiModel");
            if (StringUtils.isNotBlank(comment)) {
                data.append("(description = \"" + comment + "\")");
            }
            data.append("\r\n");
        }
        data.append("public class ").append(className);
        if (StringUtils.isNotBlank(extendsClassName)) {
            data.append(" extends " + extendsClassName);
        }
        data.append(" {\r\n\r\n");

        for (ColumnProperties columnProperties : columnPropertiesList) {
            String clazz = columnProperties.getClazz();

            if (beanType == 1 || beanType == 2) {

                String emptyAnnotation = null;
                String dateUtilImport = Config.basePackage + ".util.DateUtil";
                String DateTimeFormatImport = "org.springframework.format.annotation.DateTimeFormat";
                if (columnProperties.getClazz().equals("Date")) {

                    if (!importList.contains(dateUtilImport)) {
                        importList.add(dateUtilImport);
                    }
                    if (!importList.contains(DateTimeFormatImport)) {
                        importList.add(DateTimeFormatImport);
                    }
                }

                if (beanType == 2 && columnProperties.isPrimary()) {
                    String emptyImportPath;
                    if (clazz.equals("String")) {
                        emptyImportPath = "org.hibernate.validator.constraints.NotEmpty";
                        emptyAnnotation = "NotEmpty";

                    } else {
                        emptyImportPath = "javax.validation.constraints.NotNull";
                        emptyAnnotation = "NotNull";
                    }

                    if (!importList.contains(emptyImportPath)) {
                        importList.add(emptyImportPath);
                    }
                }

                data.append(columnProperties.toFormFile(emptyAnnotation));

            } else if (beanType == 3) {
                data.append(columnProperties.toVoFile());

            } else {
                data.append(columnProperties.toBeanFile());
            }

            String importPath;
            if (clazz.equals("Date")) {
                importPath = "java.util.Date";
            } else if (clazz.equals("BigDecimal")) {
                importPath = "java.math.BigDecimal";
            } else {
                continue;
            }

            if (!importList.contains(importPath)) {
                importList.add(importPath);
            }
        }

        if (this.isForm()) {
            importList.add("io.swagger.annotations.ApiModel");
            importList.add("io.swagger.annotations.ApiModelProperty");
        }

        StringBuilder headData = new StringBuilder();
        headData.append("package " + this.packageName + ";\r\n\r\n");
        if (importList != null) {
            for (String importStr : importList) {
                if (packageName.equals(importStr)) {
                    continue;
                }
                headData.append("import " + importStr + ";\r\n");
            }
        }
        headData.append("import lombok.Data;\r\n");

        headData.append("\r\n");
        data.append("}");

        return headData.toString() + data.toString();
    }


}
