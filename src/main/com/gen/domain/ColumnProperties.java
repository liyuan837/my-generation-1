package com.gen.domain;


import org.apache.commons.lang.StringUtils;
/**
 * @Author: LiYuan
 * @Description:拼接各实体类数内容
 * @Date: 17:58 2018/5/9
 */
public class ColumnProperties {

	/**
	 * 数据库字段名
	 */
	private String fieldId;

	/**
	 * java属性名
	 */
	private String name;

	/**
	 * java类型
	 */
	private String clazz;

	/**
	 * 注释
	 */
	private String comment;

	/**
	 * 是否主键
	 */
	private boolean isPrimary;

	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public void setPrimary(boolean primary) {
		isPrimary = primary;
	}

	/**
	 * 属性格式化为pojo代码
	 *
	 * @return
	 */
	public StringBuilder toBeanFile() {
		StringBuilder propertiesData = new StringBuilder();
		propertiesData.append("\t/**\n");
		propertiesData.append("\t * " + comment + "\n");
		propertiesData.append("\t*/\n");
		propertiesData.append("\tprivate ").append(clazz).append(" ")
				.append(name).append(";\r\n");
		return propertiesData;
	}

	/**
	 * 属性格式化为form代码
	 *
	 * @return
	 */
	public StringBuilder toFormFile(String emptyAnnotation) {
		StringBuilder propertiesData = new StringBuilder();

		String dateFormatPattern = "DateUtil.FORMAT";
		propertiesData.append("\t@ApiModelProperty(value = \"" + comment + (clazz.equals("Date") ? ",格式为:\" + " + dateFormatPattern : "\"") + (StringUtils.isNotBlank(emptyAnnotation) ? ", required = true" : "") + ")\r\n");
		if (clazz.equals("Date")) {
			propertiesData.append("\t@DateTimeFormat(pattern = " + dateFormatPattern + ")\r\n");
		}
		if (StringUtils.isNotBlank(emptyAnnotation)) {
			propertiesData.append("\t@" + emptyAnnotation + "(message = \"" + comment + "不能为空" + "\")\r\n");
		}
		propertiesData.append("\tprivate ").append(clazz).append(" ")
				.append(name).append(";\r\n\r\n");
		return propertiesData;
	}


	/**
	 * 属性格式化为vo代码
	 *
	 * @return
	 */
	public StringBuilder toVoFile() {
		StringBuilder propertiesData = new StringBuilder();

		propertiesData.append("\t@ApiModelProperty(value = \"" + comment + "\"" + (isPrimary ? ", required = true" : "") + ")\r\n");
		propertiesData.append("\tprivate ").append(clazz).append(" ")
				.append(name).append(";\r\n\r\n");
		return propertiesData;
	}

}
