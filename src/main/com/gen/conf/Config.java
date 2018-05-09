package com.gen.conf;

/**
 * @Author: LiYuan
 * @Description:配置项目常量
 * @Date: 17:58 2018/5/9
 */
public interface Config {

    /**
     * 生成的接口根uri
     */
    //String parentUri = "/ka";
    String parentUri = "";




    /**
     * 项目基本包名
     */
    //String basePackage = "com.qianmi.ehome.gateway.ka";
    //String basePackage = "com.qianmi.ehome.train.api";
    String basePackage = "com.liyuan.demo";



    /**
     * 异常名称
     */
    //String exceptionName = "GateWayKAException";
    String exceptionName = "DemoException";

    /**
     * 项目路径
     */
    //String projectPath = "/Users/caowei/workspace/ehome-train-api/";
    String projectPath = "D:/liyuan/generation/";

    /**
     * 项目名称
     */
    //String projectName = "ehome-train-api";
    //String projectName = "module";
    //String projectName = "ehome-gateway-ka";
    String projectName = "demo";


    /**
     * dao模块名称
     */
    String daoModuleName = projectName + "-dao";

    /**
     * domain模块名称
     */
    String domainModuleName = projectName + "-domain";

    /**
     * service模块名称
     */
    String serviceModuleName = projectName + "-service";

    /**
     * web模块名称
     */
    String webModuleName = projectName + "-web";


    /**
     * 异常类所在包
     */
    String exceptionPackage = basePackage + ".domain.exception";

    /**
     * 代码路径
     */
    String javaPath = "/src/main/java/";

    /**
     * 资源路径
     */
    String resourcePath = "/src/main/resources/";

    /**
     * Po包名
     */
    String poPackage = basePackage + ".domain.po";

    /**
     * Condition包名
     */
    String conditionPackage = basePackage + ".domain.condition";

    /**
     * Form包名
     */
    String formPackage = basePackage + ".form";


    /**
     * Mapper.java包名
     */
    String mapperJavaPackage = basePackage + ".mapper";

    /**
     * Service.java包名
     */
    String servicePackage = basePackage + ".service";

    /**
     * ServiceImp.java包名
     */
    String serviceImplPackage = servicePackage + ".impl";

    /**
     * Vo包名
     */
    String voPackage = basePackage + ".vo";

    /**
     * Controller包名
     */
    String controllerPackage = basePackage + ".controller";


    /**
     * Mapper.xml路径
     */
    String mapperXMLPath = projectPath + daoModuleName + resourcePath + mapperJavaPackage.replaceAll("\\.", "/") + "/";

    /**
     * Mapper.java文件路径
     */
    String mapperJavaPath = projectPath + daoModuleName + javaPath + mapperJavaPackage.replaceAll("\\.", "/") + "/";

    /**
     * Service.java文件路径
     */
    String servicePath = projectPath + serviceModuleName + javaPath + servicePackage.replaceAll("\\.", "/") + "/";

    /**
     * ServiceImp.java文件路径
     */
    String serviceImplPath = servicePath + "impl/";

    /**
     * Controller.java文件路径
     */
    String controllerPath = projectPath + webModuleName + javaPath + controllerPackage.replaceAll("\\.", "/") + "/";



}
