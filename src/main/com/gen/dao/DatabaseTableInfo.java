package com.gen.dao;

import com.bm.center.base.util.CopyUtil;
import com.gen.conf.Config;
import com.gen.conf.JDBC;
import com.gen.domain.BeanFile;
import com.gen.domain.ColumnProperties;
import com.gen.util.Utils;
import lombok.Getter;

import java.sql.*;
import java.util.*;


/**
 * Created by caowei on 2017/8/31.
 */
public class DatabaseTableInfo {


	private Connection conn = JDBC.getConnection();
	private DatabaseMetaData dmd = conn.getMetaData();

	private ResultSetMetaData rmd;

	private Map<String, String> commentMap;

	private boolean autoIncrement;
	
	private String className;// 数据库表对应的PO名称

	private String tableName; // 表名

	private String tableComment;// 表注释

	private BeanFile po;

	private BeanFile condition;

	private BeanFile queryForm;

	private BeanFile createForm;

	@Getter
	private BeanFile updateForm;

	private BeanFile deleteForm;

	private BeanFile vo;


	private static Map<String, DatabaseTableInfo> databaseTableInfoMap = new HashMap<>();

	public static DatabaseTableInfo getInstance(String  tableName) throws SQLException {
		DatabaseTableInfo databaseTableInfo = databaseTableInfoMap.get(tableName);
		if (databaseTableInfo == null) {
			databaseTableInfo = new DatabaseTableInfo(tableName);
			databaseTableInfoMap.put(tableName, databaseTableInfo);
		}

		return databaseTableInfo;
	}

	private DatabaseTableInfo(String tableName) throws SQLException {
		this.tableName = tableName;
		className = tableName;
		// 去掉前缀tb_jz_
		if (className.startsWith("tb_")) {
			className = className.substring(3);
		}
		if (className.startsWith("jz_")) {
			className = className.substring(3);
		}
		className = Utils.sql2JavaName(className);
		className = Utils.upperFirstChar(className);

		// 初始化字段注释
		initColumnComment();

		// 初始化表注释
		initTableComment();

		// 初始化主键
		// 	如果同一用户可访问多个schema，则要在getPrimaryKeys()要指定好schema的参数
		ResultSet primaryKeySet = this.dmd.getPrimaryKeys(null, null, this.tableName.toUpperCase());
		boolean hasPrimary = primaryKeySet.next();
		if (!hasPrimary) {
			throw new SQLException("表里不存在主键");
		}
		String primaryFiled = primaryKeySet.getString(4);


		List<ColumnProperties> columnPropertiesList = new ArrayList<>(); // 所有字段
		// 初始化主键类型
		String sql = "select * from " + this.tableName + " where 1 = 2";
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery(sql);
		this.rmd = rs.getMetaData();
		for (int i = 1; i <= rmd.getColumnCount(); i++) {

			String columnClassName = rmd.getColumnClassName(i);
			String clazz = columnClassName.substring(columnClassName.lastIndexOf(".") + 1);
			if ("Timestamp".equals(clazz)) {
				clazz = "Date";
			}
			if ("Short".equals(clazz)) {
				clazz = "Integer";
			}

			String fieldId = rmd.getColumnName(i);
			String comment = commentMap.get(fieldId.toUpperCase());
			ColumnProperties columnProperties = new ColumnProperties();
			columnProperties.setFieldId(fieldId);
			columnProperties.setName(Utils.sql2JavaName(fieldId));
			columnProperties.setClazz(clazz);
			columnProperties.setComment(comment);
			columnPropertiesList.add(columnProperties);

			// 主键
			if (primaryFiled.equals(fieldId)) {
				columnProperties.setPrimary(true);
			}
		}

		// 初始化PO类
		po = this.createBeanFile(className + "Po", Config.poPackage + "." + className.toLowerCase(), Config.domainModuleName, columnPropertiesList, null, 0);

		// 初始化condition类
		condition = this.createQueryBean(className + "Condition", Config.conditionPackage + "." + className.toLowerCase(), Config.domainModuleName, false);

		// 初始化queryForm类
		queryForm = this.createQueryBean(className + "QueryForm", Config.formPackage + "." + className.toLowerCase(), Config.webModuleName, true);

		// 初始化createForm
		createForm = this.createInsertFrom(className + "CreateForm", Config.formPackage + "." + className.toLowerCase(), Config.webModuleName);

		// 初始化updateForm
		updateForm = this.createUpdateFrom(className + "UpdateForm", Config.formPackage + "." + className.toLowerCase(), Config.webModuleName);

		// 初始化deleteForm
		deleteForm = this.createDeleteFrom(className + "DeleteForm", Config.formPackage + "." + className.toLowerCase(), Config.webModuleName);

		// 初始化vo
		vo = this.createBeanFile(className + "Vo", Config.voPackage + "." + className.toLowerCase(), Config.webModuleName, columnPropertiesList, null, 3);

	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public String getClassName() {
		return className;
	}

	public String getTableName() {
		return this.tableName;
	}

	public String getTableComment() {
		return tableComment;
	}

	public ColumnProperties getPrimaryKey() {
		return po.getPropertiesList().stream().filter(column -> column.isPrimary()).findFirst().get();
	}


	public ColumnProperties filterColumn(String fieldId) {
		if (po.getPropertiesList().stream().filter(column -> column.getFieldId().equals(fieldId)).count() == 0) {
			return null;
		}
		return po.getPropertiesList().stream().filter(column -> column.getFieldId().equals(fieldId)).findFirst().get();
	}

	public BeanFile getPo() {
		return this.po;
	}

	public BeanFile getCondition() {
		return this.condition;
	}

	public BeanFile getQueryForm() {
		return queryForm;
	}

	public BeanFile getCreateForm() {
		return createForm;
	}

	public BeanFile getUpdateForm() {
		return updateForm;
	}


	public BeanFile getDeleteForm() {
		return deleteForm;
	}



	public BeanFile getVo() {
		return vo;
	}

	public String getMapperClassName() {
		return this.className + "Mapper";
	}

	public String getServiceClassName() {
		return this.className + "Service";
	}

	public String getServiceImplClassName() {
		return this.className + "ServiceImpl";
	}

	public String getControllerClassName() {
		return this.className + "Controller";
	}


	/**
	 * 初始化注释
	 *
	 * @throws SQLException
     */
	private void initColumnComment() throws SQLException {
		// 查询注释
		commentMap = new HashMap<>();
		String commentSql;
		if (JDBC.dbType == 1) {
			commentSql = "show full FIELDS from " + tableName;
		} else if (JDBC.dbType == 2) {
			commentSql = "SELECT t.column_name as Field, t.data_type as Type,c.comments  FROM USER_TAB_COLUMNS t join user_col_comments c \n" +
					"on c.table_name = t.table_name and c.column_name = t.column_name\n" +
					" where t.table_name ='" + tableName.toUpperCase() + "'";
		} else {
			throw new SQLException("没有对应的db方式");
		}
		Statement commentSt = conn.createStatement();
		ResultSet commentRs = commentSt.executeQuery(commentSql);
		while (commentRs.next()) {
			if (JDBC.dbType == 1) {
				commentMap.put(commentRs.getString("field").toUpperCase(), commentRs.getString("comment"));
				if ("pri".equals(commentRs.getString("key").toLowerCase()) && "auto_increment".equals(commentRs.getString("extra").toLowerCase())) {
					autoIncrement = true;
				}
			} else if (JDBC.dbType == 2) {
				commentMap.put(commentRs.getString("field").toUpperCase(), commentRs.getString("comments"));
			}
		}
	}


	/**
	 * 初始化注释
	 *
	 * @throws SQLException
	 */
	private void initTableComment() throws SQLException {
		String commentSql;
		if (JDBC.dbType == 1) {
			commentSql = "select table_comment from information_schema.tables  where  table_name ='" + tableName + "'";
		} else if (JDBC.dbType == 2) {
			commentSql = "select comments from all_tab_comments where table_name = '" + tableName + "'";
		} else {
			throw new SQLException("没有对应的db方式");
		}
		Statement commentSt = conn.createStatement();
		ResultSet commentRs = commentSt.executeQuery(commentSql);
		if (commentRs.next()) {
			tableComment = commentRs.getString(1);
		}
	}

	/**
	 * 创建bean文件
	 *
	 * @param className
	 * @param packageName
	 * @param moduleName
	 * @param propertiesList
     * @return
     */
	private BeanFile createBeanFile(String className, String packageName, String moduleName, List<ColumnProperties> propertiesList, List<String> defaultImportList, int beanType) {
		BeanFile beanFile = new BeanFile();
		beanFile.setBeanType(beanType);
		beanFile.setClassName(className);
		beanFile.setPackageName(packageName);
		beanFile.setModuleName(moduleName);
		beanFile.setComment(tableComment);
		beanFile.setPropertiesList(propertiesList);
		List<String> importList = new ArrayList<>();
		beanFile.setImportList(importList);

		// 添加默认导入包
		if (defaultImportList != null) {
			importList.addAll(defaultImportList);
		}

		return beanFile;
	}

	/**
	 * 创建condition,queryForm
	 *
	 * @param className
	 * @param packageName
	 * @param moduleName
     * @return
     */
	private BeanFile createQueryBean(String className, String packageName, String moduleName, boolean isForm) {
		ColumnProperties primaryKey = this.getPrimaryKey();
		List<String> betweenField = Arrays.asList("add_time");
		List<String> listField = Arrays.asList(primaryKey.getFieldId());
		List<String> defaultImportList = new ArrayList<>();

		List<ColumnProperties> conditionPropertiesList = new ArrayList<>();
		for (ColumnProperties columnProperties : po.getPropertiesList()) {
			if (columnProperties.getClazz().equals("Date")) {
				if (betweenField.contains(columnProperties.getFieldId())) {
					ColumnProperties minProperties = CopyUtil.transfer(columnProperties, ColumnProperties.class);
					minProperties.setName("min" + Utils.upperFirstChar(columnProperties.getName()));
					minProperties.setComment("最小" + columnProperties.getComment());
					conditionPropertiesList.add(minProperties);

					ColumnProperties maxProperties = CopyUtil.transfer(columnProperties, ColumnProperties.class);
					maxProperties.setName("max" + Utils.upperFirstChar(columnProperties.getName()));
					maxProperties.setComment("最大" + columnProperties.getComment());
					conditionPropertiesList.add(maxProperties);
				}
			} else {
				conditionPropertiesList.add(columnProperties);

				if (listField.contains(columnProperties.getFieldId())) {
					ColumnProperties listProperties = CopyUtil.transfer(columnProperties, ColumnProperties.class);
					listProperties.setName(columnProperties.getName() + "List");
					listProperties.setClazz("List<" + columnProperties.getClazz() + ">");
					listProperties.setComment(columnProperties.getComment() + "列表");
					conditionPropertiesList.add(listProperties);

					String importStr = "java.util.List";
					if (!defaultImportList.contains(importStr)) {
						defaultImportList.add(importStr);
					}
				}
			}
		}

		if (isForm) {
			defaultImportList.add("com.bm.center.base.form.BaseQueryForm");

		} else {
			defaultImportList.add("com.bm.center.base.condition.BaseCondition");
		}

		BeanFile beanFile = this.createBeanFile(className, packageName, moduleName, conditionPropertiesList, defaultImportList, isForm ? 1 : 0);
		if (isForm) {
			beanFile.setExtendsClassName("BaseQueryForm");

		} else {
			beanFile.setExtendsClassName("BaseCondition");
		}
		return beanFile;
	}

	/**
	 * 创建createForm
	 *
	 * @param className
	 * @param packageName
	 * @param moduleName
	 * @return
	 */
	private BeanFile createInsertFrom(String className, String packageName, String moduleName) {
		ColumnProperties primaryKey = this.getPrimaryKey();
		List<String> excludeFields = Arrays.asList(primaryKey.getFieldId(),
				"add_code", "add_name", "add_time", "opt_code", "opt_name", "opt_time");

		List<ColumnProperties> formPropertiesList = new ArrayList<>();
		for (ColumnProperties columnProperties : po.getPropertiesList()) {
			if (excludeFields.contains(columnProperties.getFieldId())) {
				continue;
			}

			formPropertiesList.add(columnProperties);
		}

		BeanFile beanFile = this.createBeanFile(className, packageName, moduleName, formPropertiesList, null, 1);
		return beanFile;
	}

	/**
	 * 创建updateForm
	 *
	 * @param className
	 * @param packageName
	 * @param moduleName
	 * @return
	 */
	private BeanFile createUpdateFrom(String className, String packageName, String moduleName) {
		List<String> excludeFields = Arrays.asList(
				"add_code", "add_name", "add_time", "opt_code", "opt_name", "opt_time");
		List<ColumnProperties> formPropertiesList = new ArrayList<>();
		for (ColumnProperties columnProperties : po.getPropertiesList()) {
			if (excludeFields.contains(columnProperties.getFieldId())) {
				continue;
			}

			formPropertiesList.add(columnProperties);
		}

		BeanFile beanFile = this.createBeanFile(className, packageName, moduleName, formPropertiesList, null, 2);
		return beanFile;
	}

	/**
	 * 创建deleteForm
	 *
	 * @param className
	 * @param packageName
	 * @param moduleName
	 * @return
	 */
	private BeanFile createDeleteFrom(String className, String packageName, String moduleName) {
		List<ColumnProperties> formPropertiesList = new ArrayList<>();
		for (ColumnProperties columnProperties : po.getPropertiesList()) {
			if (columnProperties.isPrimary())
				formPropertiesList.add(columnProperties);
		}

		BeanFile beanFile = this.createBeanFile(className, packageName, moduleName, formPropertiesList, null, 2);
		return beanFile;
	}



}
