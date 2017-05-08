  <bean id="processEngineConfiguration" class="org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration">
  以上是用来构建ProcessEngine的bena。有多个class可以采用
 org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration: 单独运行的流程引擎。Activiti会自己处理事务。 默认，数据库只在引擎启动时检测 （如果没有Activiti的表或者表结构不正确就会抛出异常）。
org.activiti.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration: 单元测试时的辅助类。Activiti会自己控制事务。 默认使用H2内存数据库。数据库表会在引擎启动时创建，关闭时删除。 使用它时，不需要其他配置（除非使用job执行器或邮件功能）。
org.activiti.spring.SpringProcessEngineConfiguration: 在Spring环境下使用流程引擎。 参考Spring集成章节。
org.activiti.engine.impl.cfg.JtaProcessEngineConfiguration: 单独运行流程引擎，并使用JTA事务。
注：没有真正尝试过，事实上，第一个class有尝试，但是结果却没有预料中的报错。
使用的是h2数据库，即就是刚启动时里面没有表，更没有数据。但是不报错。。。解释:数据库只在引擎启动时检测 （如果没有Activiti的表或者表结构不正确就会抛出异常）。
原因已解决，请继续查看下去


配置文件结构
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"        
							xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"       
							 xsi:schemaLocation="http://www.springframework.org/schema/beans   
							 									http://www.springframework.org/schema/beans/spring-beans.xsd">
  <bean id="processEngineConfiguration" class=" org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration">
    <property name="jdbcUrl"    value="jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000"/>     
  </bean>
</beans>
这里的property的name可以有多个选择
databaseType   一般不用设置，工作流获取数据库连接时会自动判断，可能的值有h2, mysql, oracle, postgres, mssql, db2
databaseSchemaUpdate  设置流程启动和关闭时如何处理数据库表
	false（默认）：检查数据库表的版本和依赖库的版本， 如果版本不匹配就抛出异常。
	true: 构建流程引擎时，执行检查，如果需要就执行更新。 如果表不存在，就创建。
	create-drop: 构建流程引擎时创建数据库表， 关闭流程引擎时删除这些表。
	PS:设置true的话，并且工作流引擎的class为 org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration不就可以在数据库表不存在的时候不报错吗。。。正解！
	
	
JNDI数据库的配置
	要改动数据库的配置每次都要重新打war包发布，太麻烦，jndi帮助你！
	
	
