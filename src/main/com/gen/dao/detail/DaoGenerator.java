package com.gen.dao.detail;

import com.gen.conf.Config;
import com.gen.dao.DatabaseTableInfo;

import java.sql.SQLException;


/**
 * 生成Mapper.java
 *
 * Created by caowei on 2017/8/31.
 */
public class DaoGenerator {



	
	public String generate(DatabaseTableInfo dbTableInfo) throws SQLException {

		String poClassName=dbTableInfo.getPo().getClassName();
		String conditionClassName=dbTableInfo.getCondition().getClassName();


		StringBuffer data = new StringBuffer();

		data.append("package " + Config.mapperJavaPackage + ";\r\n\r\n")
				.append("import com.bm.center.base.mapper.BaseMapper;\r\n")
				.append("import " + dbTableInfo.getPo().getPackageName() + "." + poClassName + ";\r\n")
				.append("import " + dbTableInfo.getCondition().getPackageName() + "." + conditionClassName + ";\r\n\n")
				.append("public interface " + dbTableInfo.getMapperClassName() + " extends BaseMapper<" + poClassName + ", " + conditionClassName + "> {\r\n\r\n")

		.append("}");

		return data.toString();
	}


}
