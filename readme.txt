本项目是针对github上https://github.com/liyuan837/base_springmvc_mybatis_springboot_modules项目框架的代码自动生成器

使用注意：
1、代码生成器只适用于base_springmvc_mybatis_springboot_modules，不能用以生成其他框架的代码

使用方法：
1、首先要在Config.java类中设置项目相关的常量；
2、在JDBC.java类中进行数据库配置；
3、在Main.java类中的maps集合中定义数据库中对应的表名；
4、运行Main.java的main方法，就能生成相应的代码；
5、将对应的代码copy到项目框架中即可运行。

生成代码说明：会根据数据表名生成对应的
1、po：与数据表对应的实体
2、condition：查询条件封装类（自动剔除了date、datetime类型的字段）
3、form：提交数据的封装类
4、vo：返回给前端的数据封装类
5、mapper、service、controller：整个三层业务逻辑增删改查的模板代码
