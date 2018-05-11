package com.gen.dao.detail;

import com.gen.conf.Config;
import com.gen.dao.DatabaseTableInfo;

import java.sql.SQLException;


/**
 * @Author: LiYuan
 * @Description:生成Mapper.java
 * @Date: 11:39 2018/5/11
 */
public class DaoGenerator {



	
	public String generate(DatabaseTableInfo dbTableInfo) throws SQLException {

		String poClassName=dbTableInfo.getPo().getClassName();
		String conditionClassName=dbTableInfo.getCondition().getClassName();


		StringBuffer data = new StringBuffer();
		data.append("package " + Config.mapperJavaPackage + ";\r\n\r\n")

				.append("import " + dbTableInfo.getPo().getPackageName() + "." + poClassName + ";\r\n")
				.append("import " + dbTableInfo.getCondition().getPackageName() + "." + conditionClassName + ";\r\n")
				.append("import "+Config.mapperJavaPackage+".base.BaseMapper;\r\n\n")
				.append("public interface " + dbTableInfo.getMapperClassName() + " extends BaseMapper<" + poClassName + ", " + conditionClassName + "> {\r\n\r\n")

		.append("}");

		return data.toString();
	}


}
