package com.gen.dao.detail;

import com.gen.conf.Config;
import com.gen.dao.DatabaseTableInfo;

import java.sql.SQLException;


/**
 * 生成Service.java
 *
 * Created by caowei on 2017/8/31.
 */
public class ServiceImplGenerator {

	
	public String generate(DatabaseTableInfo dbTableInfo) throws SQLException {

		String poClassName=dbTableInfo.getPo().getClassName();
		String conditionClassName=dbTableInfo.getCondition().getClassName();

		StringBuffer data = new StringBuffer();

		data.append("package " + Config.serviceImplPackage + ";\r\n\r\n")
				.append("import com.bm.center.base.service.BaseServiceImpl;\r\n")
				.append("import " + dbTableInfo.getPo().getPackageName() + "." + poClassName + ";\r\n")
				.append("import " + dbTableInfo.getCondition().getPackageName() + "." + conditionClassName + ";\r\n")
				.append("import " + Config.mapperJavaPackage + "." + dbTableInfo.getMapperClassName() + ";\r\n");
		if (!Config.serviceImplPackage.equals(Config.servicePackage)) {
			data.append("import " + Config.servicePackage + "." + dbTableInfo.getServiceClassName() + ";\r\n");
		}

		data.append("import org.springframework.stereotype.Service;\r\n\r\n");
		data.append("@Service\r\n")
				.append("public class " + dbTableInfo.getServiceImplClassName() + " extends BaseServiceImpl<" + poClassName + ", " + conditionClassName + ", " + dbTableInfo.getMapperClassName() + "> implements " + dbTableInfo.getServiceClassName() + " {\r\n\r\n")

		.append("}");

		return data.toString();
	}



}
