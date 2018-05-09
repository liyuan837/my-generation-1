package com.gen.dao;

import com.gen.conf.Config;
import com.gen.dao.detail.*;

import java.io.File;
import java.io.FileOutputStream;


/**
 * 代码生成类
 *
 * Created by caowei on 2017/8/31.
 */
public class MybatisGenerator {
	public void generate(String tableName) throws Exception{
		// 获取数据表信息
		System.out.println(Config.mapperXMLPath);
		System.out.println(Config.mapperJavaPath);
		System.out.println(Config.servicePath);
		System.out.println(Config.serviceImplPath);
		System.out.println(Config.controllerPath);

		DatabaseTableInfo dbTableInfo = DatabaseTableInfo.getInstance(tableName);
		writeFile(Config.mapperXMLPath, dbTableInfo.getMapperClassName()+ ".xml", new SqlMapXMLGenerator().generate(dbTableInfo));
		writeFile(Config.mapperJavaPath, dbTableInfo.getMapperClassName()+ ".java", new DaoGenerator().generate(dbTableInfo));
		writeFile(Config.servicePath, dbTableInfo.getServiceClassName() + ".java", new ServiceGenerator().generate(dbTableInfo));
		writeFile(Config.serviceImplPath, dbTableInfo.getServiceImplClassName() + ".java", new ServiceImplGenerator().generate(dbTableInfo));
		writeFile(Config.controllerPath, dbTableInfo.getControllerClassName() + ".java", new ControllerGenerator().generate(dbTableInfo));

		writeFile(dbTableInfo.getPo().getFilePath(), dbTableInfo.getPo().getClassName()+ ".java", dbTableInfo.getPo().toString());
		writeFile(dbTableInfo.getCondition().getFilePath(), dbTableInfo.getCondition().getClassName()+ ".java", dbTableInfo.getCondition().toString());
		writeFile(dbTableInfo.getCreateForm().getFilePath(), dbTableInfo.getCreateForm().getClassName()+ ".java", dbTableInfo.getCreateForm().toString());
		writeFile(dbTableInfo.getQueryForm().getFilePath(), dbTableInfo.getQueryForm().getClassName()+ ".java", dbTableInfo.getQueryForm().toString());
		writeFile(dbTableInfo.getUpdateForm().getFilePath(), dbTableInfo.getUpdateForm().getClassName()+ ".java", dbTableInfo.getUpdateForm().toString());
		writeFile(dbTableInfo.getUpdateForm().getFilePath(), dbTableInfo.getDeleteForm().getClassName()+ ".java", dbTableInfo.getDeleteForm().toString());
		writeFile(dbTableInfo.getVo().getFilePath(), dbTableInfo.getVo().getClassName()+ ".java", dbTableInfo.getVo().toString());

	}

	/**
	 * 生成类文件
	 *
	 * @param dirPath
	 * @param fileName
	 * @param data
	 * @throws Exception
     */
	private void writeFile(String dirPath, String fileName, String data) throws Exception {
		createNecessaryFile(dirPath);
		FileOutputStream fos = new FileOutputStream(dirPath + fileName);
		fos.write(data.getBytes("UTF-8"));
		fos.close();
	}

	private void createNecessaryFile(String file){
		File f=new File(file);
		if(!f.exists()){
			f.mkdirs();
		}
	}

}
