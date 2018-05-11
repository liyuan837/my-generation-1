package com.gen.dao.detail;

import com.gen.conf.Config;
import com.gen.dao.DatabaseTableInfo;

import java.sql.SQLException;


/**
 * @Author: LiYuan
 * @Description:生成Service.java
 * @Date: 18:51 2018/5/9
 */
public class ServiceGenerator {

	public String generate(DatabaseTableInfo dbTableInfo) throws SQLException {

		String poClassName=dbTableInfo.getPo().getClassName();
		String conditionClassName=dbTableInfo.getCondition().getClassName();

		StringBuffer data = new StringBuffer();

		data.append("package " + Config.servicePackage + ";\r\n\r\n")
				.append("import " + dbTableInfo.getPo().getPackageName() + "." + poClassName + ";\r\n")
				.append("import " + dbTableInfo.getCondition().getPackageName() + "." + conditionClassName + ";\r\n")
				.append("import " + Config.servicePackage +".base.BaseService;\r\n\n")
				.append("public interface " + dbTableInfo.getServiceClassName() + " extends BaseService<" + poClassName + ", " + conditionClassName + "> {\r\n\r\n")

		.append("}");

		return data.toString();
	}



}
