package com.gen.dao.detail;

import com.gen.conf.Config;
import com.gen.dao.DatabaseTableInfo;
import com.gen.domain.ColumnProperties;

import java.sql.SQLException;
import java.util.List;


/**
 * @Author: LiYuan
 * @Description:生成sql
 * @Date: 11:44 2018/5/11
 */
public class SqlMapXMLGenerator {


	// 生成sql语句
	public String generate(DatabaseTableInfo dbTableInfo) throws SQLException {
		StringBuilder sqlData=new StringBuilder();

		sqlData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n")
			   .append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >")
			   .append("\r\n<mapper namespace=\"" + Config.mapperJavaPackage + "." + dbTableInfo.getMapperClassName() + "\">\r\n")
				.append("\r\n" + this.generateResultMapSql(dbTableInfo))

				.append("\r\n" + this.generateFieldSql(dbTableInfo))
				.append("\r\n" + this.generateWhereSql(dbTableInfo))
				.append("\r\n" + this.generateSelectSql(dbTableInfo))
				.append("\r\n" + this.generateCountSql(dbTableInfo))
				.append("\r\n" + this.generateSelectListSql(dbTableInfo))
			   .append("\r\n" + this.generateInsertSql(dbTableInfo))
			   .append("\r\n" + this.generateUpdateSql(dbTableInfo))
			   .append("\r\n" + this.generateDeleteSql(dbTableInfo))
			   .append("\r\n</mapper>");
		return sqlData.toString();

	}

	/**
	 * 生成resultMap语句
	 *
	 * @param dbTableInfo
	 * @return
	 * @throws SQLException
     */
	private String generateResultMapSql(DatabaseTableInfo dbTableInfo) throws SQLException {

		List<ColumnProperties> columnPropertiesList = dbTableInfo.getPo().getPropertiesList();

		StringBuilder sqlData=new StringBuilder();// 返回的sqlData
		StringBuilder selectContent=new StringBuilder();
		for (ColumnProperties columnProperties : columnPropertiesList) {
			selectContent.append("\r\n\t	<result column=\"" + columnProperties.getFieldId() + "\" property=\"" + columnProperties.getName() + "\" jdbcType=\"" + getJDBCType(columnProperties.getClazz()) + "\"/>");
		}
		sqlData.append("\r\n\t<resultMap id=\"" + dbTableInfo.getPo().getClassName() + "\" type=\"" + dbTableInfo.getPo().getPackageName() + "." + dbTableInfo.getPo().getClassName()  + "\">")
				.append(selectContent)
				.append("\r\n\t</resultMap>\r\n");
		return sqlData.toString();

	}

	/**
	 * 生成字段语句
	 *
	 * @param dbTableInfo
	 * @return
	 * @throws SQLException
     */
	private String generateFieldSql(DatabaseTableInfo dbTableInfo) throws SQLException {
		List<ColumnProperties> columnPropertiesList = dbTableInfo.getPo().getPropertiesList();

		StringBuilder sqlData=new StringBuilder();// 返回的sqlData
		StringBuilder selectContent=new StringBuilder();
		for (int i = 0; i < columnPropertiesList.size(); i++) {
			ColumnProperties columnProperties = columnPropertiesList.get(i);
			if (i > 0){
				selectContent.append("\r\n");
			}
			selectContent.append("\t\t");
			if(i > 0){
				selectContent.append(",");
			}else{
				selectContent.append(" ");
			}
			selectContent.append("t." + columnProperties.getFieldId());
		}
		sqlData.append("\t<sql id=\"fieldSql\">\r\n")
				.append(selectContent)
				.append("\r\n\t</sql>\r\n");
		return sqlData.toString();

	}


	/**
	 * 生成where语句
	 *
	 * @param dbTableInfo
	 * @return
	 * @throws SQLException
     */
	protected String generateWhereSql(DatabaseTableInfo dbTableInfo) throws SQLException {
		List<ColumnProperties> columnPropertiesList = dbTableInfo.getCondition().getPropertiesList();

		StringBuilder sqlData=new StringBuilder();// 返回的sqlData
		StringBuilder where = new StringBuilder();
		for (ColumnProperties columnProperties : columnPropertiesList) {

			String colName = columnProperties.getName();
			String clazz = columnProperties.getClazz();
			String fieldId = columnProperties.getFieldId();
			String emptyTest = "<if test=\"" + colName + " != null";
			if (columnProperties.getClazz().equals("String")){
				emptyTest = emptyTest + " and " + colName + " != ''";
			}
			emptyTest += "\">";

			String childWhere;
			if (clazz.startsWith("List<")) {
				String childClazz = clazz.substring(5);
				childClazz = childClazz.substring(0, childClazz.length() - 1);
				String childName = colName.replaceAll("List", "Item");

				childWhere = "\r\n\t\t\t<foreach collection=\"" + colName + "\" item=\"" + childName + "\" open=\"(\" close=\")\" separator=\"OR\">";
				childWhere += "\r\n\t\t\t\tt." +fieldId + " = #{" + childName + ",jdbcType = " + getJDBCType(childClazz) + "}";
				childWhere += "\r\n\t\t\t</foreach>";

			} else {
				String testSymbol = "=";
				if (clazz.equals("Date")) {
					if (colName.startsWith("min")) {
						testSymbol = "&gt;=";
					} else if (colName.startsWith("max")) {
						testSymbol = "&lt;=";
					}

				}
				childWhere = "t." + fieldId + " " + testSymbol + " #{" + colName + ",jdbcType = " + getJDBCType(clazz) + "}";
			}
			where.append("\r\n\t\t" + emptyTest
					+"\r\n\t\t\tAND " + childWhere + ""
					+ "\r\n\t\t</if>");
		}
		sqlData.append("\t<sql id=\"whereSql\">")
				.append(where)
				.append("\r\n\t</sql>\r\n");
		return sqlData.toString();
	}

	/**
	 * 生成select语句
	 *
	 * @param dbTableInfo
	 * @return
	 * @throws SQLException
     */
	private String generateSelectSql(DatabaseTableInfo dbTableInfo) throws SQLException {
		StringBuilder sqlData=new StringBuilder();// 返回的sqlData
		StringBuilder whereContent=new StringBuilder();

		ColumnProperties primaryKey = dbTableInfo.getPrimaryKey();

		String javaName=primaryKey.getName();
		javaName=javaName.substring(0,1).toLowerCase()+javaName.substring(1);
		String valueStr = "#{" + javaName + ",jdbcType = " + this.getJDBCType(primaryKey.getClazz()) + "}";
		whereContent.append("t." + primaryKey.getFieldId() + " = "+valueStr+" and ");
		whereContent.delete(whereContent.length()-4, whereContent.length());

		sqlData.append("\t<select id=\"select\" parameterType=\"" + primaryKey.getClazz() + "\" resultMap=\"" + dbTableInfo.getPo().getClassName() + "\">")
		       .append("\r\n\t\tSELECT ")
		       .append("\r\n\t\t<include refid=\"fieldSql\"/>")
		       .append("\r\n\t\t  FROM " + dbTableInfo.getTableName() + " t ")
		       .append("\r\n\t\t WHERE " + whereContent)
		       .append("\r\n\t</select>\r\n");
		return sqlData.toString();

	}


	/**
	 * 生成count语句
	 *
	 * @param dbTableInfo
	 * @return
	 * @throws SQLException
     */
	private String generateCountSql(DatabaseTableInfo dbTableInfo) throws SQLException {
		StringBuilder sqlData=new StringBuilder();// 返回的sqlData

		sqlData.append("\t<select id=\"count\" parameterType=\"" + dbTableInfo.getCondition().getPackageName() + "." + dbTableInfo.getCondition().getClassName() + "\" resultType=\"Integer\" >")
				.append("\r\n\t\tSELECT ")
				.append("\r\n\t\t\tcount(1)")
				.append("\r\n\t\t  FROM " + dbTableInfo.getTableName() + " t ")
				.append("\r\n\t\t<where>")
				.append("\r\n\t\t\t<include refid=\"whereSql\"/>")
				.append("\r\n\t\t</where>")
				.append("\r\n\t</select>\r\n");
		return sqlData.toString();

	}

	/**
	 * 生成selectList语句
	 *
	 * @param dbTableInfo
	 * @return
	 * @throws SQLException
     */
	private String generateSelectListSql(DatabaseTableInfo dbTableInfo) throws SQLException {
		StringBuilder sqlData=new StringBuilder();// 返回的sqlData

		sqlData.append("\t<select id=\"selectList\" parameterType=\"" + dbTableInfo.getCondition().getPackageName() + "." + dbTableInfo.getCondition().getClassName() + "\" resultMap=\"" + dbTableInfo.getPo().getClassName() + "\">")
				.append("\r\n\t\tSELECT ")
				.append("\r\n\t\t<include refid=\"fieldSql\"/>")
				.append("\r\n\t\t  FROM " + dbTableInfo.getTableName() + " t ")
				.append("\r\n\t\t<where>")
				.append("\r\n\t\t\t<include refid=\"whereSql\"/>")
				.append("\r\n\t\t</where>")
				.append("\r\n\t</select>\r\n");
		return sqlData.toString();

	}

	/**
	 * 生成insert语句
	 *
	 * @param dbTableInfo
	 * @return
	 * @throws SQLException
     */
	private String generateInsertSql(DatabaseTableInfo dbTableInfo) throws SQLException {
		ColumnProperties primaryKey = dbTableInfo.getPrimaryKey();
		List<ColumnProperties> columnPropertiesList = dbTableInfo.getPo().getPropertiesList();

		StringBuilder sqlData=new StringBuilder();// 返回的sqlData

		StringBuilder insertFields=new StringBuilder();// 要插入的字段
		
		StringBuilder insertValues = new StringBuilder();// 要插入的字段的值
		
		for (int i = 0; i < columnPropertiesList.size(); i++) {
			ColumnProperties columnProperties = columnPropertiesList.get(i);
			String colName = columnProperties.getName();
			String fieldId = columnProperties.getFieldId();
			String emptyTest = "<if test=\"" + colName + " != null";
			if (colName.equals("String")) {
				emptyTest += " and " + colName + " != ''";
			}
			emptyTest += "\">";

			insertFields.append((columnProperties.isPrimary() ? "" : "\r\n\t\t" + emptyTest)
							  + "\r\n\t\t\t" + (columnProperties.isPrimary() ? "" : ",") + fieldId + (columnProperties.isPrimary() ? "" : "\r\n\t\t</if>"));
			insertValues.append((columnProperties.isPrimary() ? "" : "\r\n\t\t" + emptyTest)
							  + "\r\n\t\t\t" + (columnProperties.isPrimary() ? "" : ",") + "#{" + colName + ",jdbcType=" + this.getJDBCType(columnProperties.getClazz()) + "}" + (columnProperties.isPrimary() ? "" : "\r\n\t\t</if>"));
		}
		sqlData.append("\t<insert id=\"insert\" parameterType=\"" + dbTableInfo.getPo().getPackageName() + "." + dbTableInfo.getPo().getClassName() + "\">");
		if (dbTableInfo.isAutoIncrement()) {
			sqlData.append("\r\n\t\t<selectKey resultType=\"" + primaryKey.getClazz() + "\" order=\"AFTER\" keyProperty=\"" + primaryKey.getFieldId() + "\">")
					.append("\r\n\t\t\t\tSELECT LAST_INSERT_ID()")
					.append("\r\n\t\t</selectKey>\r\n");
		}

		sqlData.append("\r\n\t\tINSERT INTO " + dbTableInfo.getTableName() + "(  " + insertFields+"\r\n\t\t)")
		       .append("\r\n\t\tVALUES( " + insertValues + "\r\n\t\t)")
		       .append("\r\n\t</insert>\r\n");
		return sqlData.toString();

	}

	/**
	 * 生成update语句
	 *
	 * @param dbTableInfo
	 * @return
	 * @throws SQLException
     */
	private String generateUpdateSql(DatabaseTableInfo dbTableInfo) throws SQLException {
		List<ColumnProperties> columnPropertiesList = dbTableInfo.getPo().getPropertiesList();

		StringBuilder sqlData=new StringBuilder();// 返回的sqlData
		
		StringBuilder updateContent=new StringBuilder();
		
		StringBuilder whereContent=new StringBuilder();

		for (int i = 0; i < columnPropertiesList.size(); i++) {
			ColumnProperties columnProperties = columnPropertiesList.get(i);
			String colName = columnProperties.getName();
			String fieldId = columnProperties.getFieldId();

			String emptyTest = "    <if test=\"" + colName + " != null\">";
			String valueStr = "#{" + colName + ",jdbcType = " + this.getJDBCType(columnProperties.getClazz()) + "}";
			if (columnProperties.isPrimary() && whereContent.length() == 0) {
				whereContent.append("t." + fieldId +"= " + valueStr);
			} else {
				updateContent.append("\r\n\t\t" + emptyTest
						+ "\r\n\t\t\t    t." + fieldId + " = " + valueStr+ ", \r\n\t\t    </if>");
			}
		}
		sqlData.append("\t<update id=\"update\" parameterType=\"" + dbTableInfo.getPo().getPackageName() + "." + dbTableInfo.getPo().getClassName() + "\">")
			   .append("\r\n\t\tUPDATE " + dbTableInfo.getTableName() + " t")
			   .append("\r\n\t\t <set>"+updateContent+"\r\n\t    </set>")
			   .append("\r\n\t\t WHERE ")
			   .append(whereContent)
				.append("\r\n\t</update>\r\n");
			   
		return sqlData.toString();
	}

	/**
	 * 生成delete语句
	 *
	 * @param dbTableInfo
	 * @return
	 * @throws SQLException
     */
	private String generateDeleteSql(DatabaseTableInfo dbTableInfo) throws SQLException {
		StringBuilder sqlData=new StringBuilder();// 返回的sqlData

		StringBuilder whereContent=new StringBuilder();
		
		ColumnProperties primaryKey = dbTableInfo.getPrimaryKey();
		
		String valueStr = "#{" + primaryKey.getName() + ",jdbcType=" + this.getJDBCType(primaryKey.getClazz()) + "}";

		whereContent.append("" + primaryKey.getFieldId() + " = "+valueStr + " and ");

		whereContent.delete(whereContent.length()-4, whereContent.length());
		sqlData.append("\t<delete id=\"delete\" parameterType=\"" + primaryKey.getClazz() + "\">")
			   .append("\r\n\t\t DELETE FROM " + dbTableInfo.getTableName() + "")
			   .append("\r\n\t\t  WHERE ")
			   .append(whereContent)
			   .append("\r\n\t</delete>");
		return sqlData.toString();
	}


	private String getJDBCType(String javaType) throws SQLException {
		if(javaType.equals("Date")){
			return "TIMESTAMP";
		} else if(javaType.equals("Timestamp")){
			return "TIMESTAMP";
		} else if(javaType.equals("String")){
			return "VARCHAR";
		} else if(javaType.equals("Date")){
			return "TIMESTAMP";
		} else if(javaType.equals("int") || javaType.equals("Integer") || javaType.equals("Long")){
			return "INTEGER";
		} else if(javaType.equals("BigDecimal")){
			return "DECIMAL";
		} else {
			throw new SQLException("缺少jdbcType的定义,无法转换");
		}
	}
}
