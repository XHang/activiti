# 工作流

# 一：什么是工作流

所谓工作流，顾名思义，就是把整个工作的整个流程串联起来

工作流只是提供了一种思路，具体的实现还是要依赖广大框架的。

目前Java最流行的工作流框架有Activity和jbpm。

该文档主要讲解Activity 6.0的使用

> Activity 其实已经发展到7.0了

#二：Activity的先决条件

1. JDK1.7以上

2. IDE插件，如Eclipse的Activiti Designer 插件，或者IDEA上面的`actiBPM `插件

# 三：快速入门

## 3.1基础环境搭建

官网上下载的zip包有几个可以用于快速入门的war包，直接部署上tomcat上，就可以看到效果了。

用web UI直接进行流程的设计。这里就不讲了

**快速入门步骤**：

1. 首先要新建一个maven项目，然后添加Activity的依赖。

   依赖项可以在该网站上找到`https://www.activiti.org/quick-start`

2. 为你的项目配置下日志配置文件

   ```
   log4j.rootLogger=DEBUG, ACT
   log4j.appender.ACT=org.apache.log4j.ConsoleAppender
   log4j.appender.ACT.layout=org.apache.log4j.PatternLayout
   log4j.appender.ACT.layout.ConversionPattern= %d{hh:mm:ss,SSS} [%t] %-5p %c %x - %m%n
   ```

   放在`/src/main/resources/log4j.properties `

3. 开始写程序-main函数

   > 代码就懒的搬上来了，请参阅项目的`com.example.Example`类的`example1`方法

**本节的几个小知识点**

1. Activity支持依赖注入，将在以下的章节讲到

2. Activity有一个数据库脚本，里面记录了有关工作流相关的表的建表语句

   在官网下载的zip包里面的`database/create `里面

   或者找`activiti-engine-5.22.0.jar`里面的`org/activiti/db/create`

3. 有一个配置可以将项目打成一个jar包，然后直接运行jar包就可以看到效果了。

   > 在项目的pom文件就可以看到这段配置。以上

## 3.2：部署一个流程

这一节我们将 部署一个入职流程，这个流程是酱紫的：

如果就业经验大于3年，那么就会发布一个个性化的入职信息，需要录入欢迎入职时间

如果就业经验小于3年，那么会自动将数据和后端系统进行集成。通过脚本或者Java程序

当然，这个就业经验需要我们输入年限

流程我们定义好了，怎么嵌入到工作流呢？

1. 这个时候，我们就需要编写`bpmn`的XML了，这将流程以XML文件的格式展现出来

> xml文件请参阅Onboarding.bpmn20.xml 文件

2. 编写程序执行这个流程，请参阅项目的`com.example.Example`类的方法`test3`方法

# 四：工作流配置

## 4.1：配置文件

在之前的程序中，工作流引擎的配置都是写在Java文件中，这样不利于降低耦合性。本节将介绍一个配置文件。

通过这个配置文件，可以直接创建一个工作流引擎。

这个配置文件的名称叫做：`activiti.cfg.xml `,格式其实跟Spring的bean配置文件一样。

具体可以参照本项目的`activiti.cfg.xml `文件

需要注意的地方1：

该配置文件必须包含一个`processEngineConfiguration `bean，而这个bean可以有由很多的class创建出来。

选择哪一种class，要根据不同的项目而定，具体来说，有下面的class

`org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration `
单独运行的流程引擎。Activiti会自己处理事务。 默认，数据库只在引擎启动时检测 （如果没有Activiti的表或者表结构不正确就会抛出异常）

`org.activiti.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration `
单元测试时的辅助类。Activiti会自己控制事务。 默认使用H2内存数据库。数据库表会在引擎启动时创建，关闭时删除。 使用它时，不需要其他配置（除非使用job执行器或邮件功能）

`org.activiti.spring.SpringProcessEngineConfiguration`
在Spring环境下使用流程引擎。 参考Spring集成章节

`org.activiti.engine.impl.cfg.JtaProcessEngineConfiguration`
单独运行流程引擎，并使用JTA事务

需要注意的地方2：

上面说的那个`processEngineConfiguration `bean，有几个属性可以指定

1. `databaseTyp`e   ：一般不用设置，工作流获取数据库连接时会自动判断
   可能的值有`h2, mysql, oracle, postgres, mssql, db2`
   如果自动检测失败，可能需要手动设定
2. `databaseSchemaUpdate `：可以指定流程流程启动和关闭时的数据库模式设定，有以下值
   1. false(默认值):检查数据库表的版本和依赖库的版本， 如果版本不匹配就抛出异常。
   2. true: 构建流程引擎时，执行检查，如果需要就执行更新。 如果表不存在，就创建。
   3. create-drop: 构建流程引擎时创建数据库表， 关闭流程引擎时删除这些表。

需要注意的地方3：

其实还可以配置一个JNDI数据源，但是暂时忽略之

## 4.2 ：Job Executor(从版本6开始)

Job Executor是一个管理线程池以激活定时器和其他异步任务的组件。

> 在本教程中，Job Executor就相当于AsyncExecutor ，所以可以这么说。
>
> 另外，不想借助Job Executo来实现也是行的，你可以采取其他实现，比如说消息队列

Activiti6唯一可用的Job Executor(作业执行器)是 AsyncExecutor (异步执行器).

与以往的Job Executor相比，新来的 async executor有更高的性能和更便于执行数据库的机能。

另外，如果应用是在JavaEE7下面运行的，可以使用`JSR-236`兼容的`ManagedAsyncJobExecutor`  让容器来管理线程，要启动这个，你需要在activiti.cfg.xml 的配置文件加上这个东西

```
<bean id="threadFactory" class="org.springframework.jndi.JndiObjectFactoryBean">
   <property name="jndiName" value="java:jboss/ee/concurrency/factory/default" />
</bean>
<bean id="customJobExecutor" class="org.activiti.engine.impl.jobexecutor.ManagedAsyncJobExecutor">
   <!-- ... -->
   <property name="threadFactory" ref="threadFactory" />
   <!-- ... -->
</bean>
```

### 4.2.1 激活Job Executor

在默认情况下，工作流引擎启动时AsyncExecutor 并没有启动，所以你需要激活它

在activiti.cfg.xml 配置文件里，如下图所示

```
 <bean id="processEngineConfiguration" class="org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration">
   .....
    <property name="asyncExecutorActivate" value="true" />
 </bean>
```

## 4.3 邮件配置

邮件服务是可选的，Activiti支持在流程中发送邮件，但是要实际的发送邮件，需要配置下SMTP的邮件服务器。

//TODO 以后接触到再写下去

## 4.4 ：历史流程服务配置

历史流程服务是可选的。

通过这个配置可以让你调整历史的配置

```
<property name="history" value="audit" />
```

配置位置当然在`processEngineConfiguration`的bean里面

还有其他知识，用到了再学。

## 4.5：设置配置中bean的可见性

在默认情况下，你在activiti.cfg.xml里面或者Spring中配置的任何bean，都可以在脚本和表达式中使用。

如果要限制这些bean的可见性，让它们在脚本和表达式中不可见，也不可用。

那么就要在`ProcessEngineConfiguration`  里面配置一个叫``beans`  `属性。

这个属性的数据类型是一个Map，只有在这个map中存在的bean，才能对表达式和脚本中可见。

## 4.6 部署的缓存配置

缓存配置的好处就是所有流程定义都被缓存(当然是解析后)，以避免每次流程定义都需要用到数据库。

默认情况下，这个缓存没有限制。添加配置可以限制

```
<property name="processDefinitionCacheLimit" value="10" />
```

当然还是要配置在`ProcessEngineConfiguration`  里面的。

这个配置的值要根据流程定义的总数，以及运行时所有流程实例使用的流程定义的数量





# 五：创建工作流相关表

创建工作流相关的表很简单

步骤如下

1. 添加`activiti-engine.jar`到classpath下,其实就是加依赖啦
2. 添加数据库驱动jar包
3. 配置数据库，参阅上面我写的配置教程，就是第四章的`工作流配置文件`
4. 执行DbSchemaCreate的main方法。

这其中建表的sql语句可以从`activiti-engine.jar`包找到，更多详情可以找第三章`快速入门`看下

这里介绍一下里面sql文件名的格式

`activiti.{db}.{create|drop}.{type}.sql`

其中db是数据库类型，type有以下几种

`engine`:引擎执行所需要的表

`identity`:包含用户，组和用户对组的成员资格相关的表

`history`：历史记录和审计（audit  ）的表，这些表是可选的，当历史等级设置为none时不需要。

> 注意：当历史等级设置为none时也会禁用一些其他功能，比如说任务的注释

> 再次注意：如果你使用的是Mysql5.6.4的版本将不支持时间戳或毫秒级精度的日期。 
>
> 更糟糕的是，创建这些数据类型的列时，某个版本的MySQl数据库会抛异常，某些则不会。
>
> 一旦要是抛异常了，可以使用其他的建表文件，如常规版本或者带有mysql55的特殊sql文件。
>
> mysql55这个文件建出来的表不包含毫秒精度的列类型

# 六：工作流相关的表名解释

activiti工作流创建的数据库表名全都以ACT_开头，第二部分根据表的功能不同而不同。

比如说：

1. ACT_RE_ *这种表名：RE代表存储库。 具有此前缀的表包含静态信息，例如流程定义和流程资源（图像，规则等）。
2. ACT_RU_ *这种表名：RU代表运行时间。 这些是包含流程实例，用户任务，变量，作业等的运行时数据的运行时表， Activiti仅在流程实例执行期间存储运行时的数据，并在进程实例结束时删除记录。 这样可以使运行时间表小型化。
3. ACT_ID_ *这种表名：ID代表身份。 这些表包含身份信息，如用户，组等。
4. ACT_HI_ *：HI代表历史。 这些是包含历史数据的表，例如过去的流程实例，变量，任务等
5.  ACT_GE_ *：一般数据，用于各种用例。

# 七：数据库升级

默认情况下，在创建流程引擎时，即ProcessEngine对象时会默认检查一遍数据库表版本检查。
如果版本不一致，抛异常。这时候就需要进行数据库表升级了

> 请注意：在升级前，你必须备份你的数据库表、

怎么升级？

1：将以下配置属性放在activiti.cfg.xml 配置文件里面

```
<beans >
	<bean id="processEngineConfiguration" 		class="org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration">
	<!-- ... -->
	<property name="databaseSchemaUpdate" value="true" />
	<!-- ... -->
	</bean>
</beans>
```

2：为你的数据库选择合适的驱动程序添加到classpath路径下，升级你项目引用的Activiti类库。
     并将数据库配置为，指向拥有老版本表的数据库
3：运行你的Activiti，大概就是先构造一个引擎实例吧，这样就可以升级了。具体的文档说的不是很清楚
4：作为替代，你也可以在官网下载升级的数据库脚本，然后运行它

# 八：日志配置

在默认情况下，`activiti-engine  `依赖没有` SFL4J-binding` 的jar。
你需要添加这些jar，以便选择你需要的日志实现。如果没有添加这些实现的日志类库依赖，slf4j将使用默认的`NOP-logge `来记录信息。

在这里，我们假设你使用的是log4j的日志类库来记录日志，那么

```
<dependency>
  <groupId>org.slf4j</groupId>
  <artifactId>slf4j-log4j12</artifactId>
</dependency>
```

OK,这样你就使用了log4j来记录日志了。



# 九：事件处理

Activiti事件处理机制允许你在工作流引擎中发生事件时得到通知。

可以为特定的事件注册监听器，而不是任何事件发生时都收到通知。
注册监听器有两种方式

1. 使用APi在运行时添加引擎范围的事件监听器。
2. 将事件监听器添加到BPMN的特定流程的定义中。









​	





   

   













这里的property的name可以有多个选择
databaseType   一般不用设置，工作流获取数据库连接时会自动判断，可能的值有h2, mysql, oracle, postgres, mssql, db2
databaseSchemaUpdate  设置流程启动和关闭时如何处理数据库表
	false（默认）：
	true: 构建流程引擎时，执行检查，如果需要就执行更新。 如果表不存在，就创建。
	create-drop: 构建流程引擎时创建数据库表， 关闭流程引擎时删除这些表。
	PS:设置true的话，并且工作流引擎的class为 org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration不就可以在数据库表不存在的时候不报错吗。。。正解！
	
​	
JNDI数据库的配置
	要改动数据库的配置每次都要重新打war包发布，太麻烦，jndi帮助你！
	
----------------------------开始工作流引擎学习-----------------------------------------
1：十分钟入门程序，略
2：入门项目。
	第一章：添加依赖
	第二章：设置Activiti以及如何获取ProcessEngine类的一个实例
					注：ProcessEngine类的实例是Activiti所有引擎功能的核心接入点
					注：Activiti流程引擎通过名为activiti.cfg.xml的XML文件进行配置
					注：org.activiti.engine.ProcessEngines.getDefaultProcessEngine()即可获取一个ProcessEngine类的示例
					注：以上部分将在classpath路径寻找一个activiti.cfg.xml文件，并通过该文件创建一个ProcessEngine类的实例，即引擎。
					这个activiti.cfg.xml文件大概是酱紫的
					<beans xmlns="http://www.springframework.org/schema/beans"
       							xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       							xsi:schemaLocation="http://www.springframework.org/schema/beans   http://www.springframework.org/schema/beans/spring-beans.xsd">
							  <bean id="processEngineConfiguration" class="org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration">
									    <property name="jdbcUrl" value="jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000" />
									    <property name="jdbcDriver" value="org.h2.Driver" />
									    <property name="jdbcUsername" value="sa" />
									    <property name="jdbcPassword" value="" />
									    <property name="databaseSchemaUpdate" value="true" />
									    <property name="jobExecutorActivate" value="false" />
									    <property name="asyncExecutorEnabled" value="true" />
									    <property name="asyncExecutorActivate" value="false" />
									    <property name="mailServerHost" value="mail.my-corp.com" />
									    <property name="mailServerPort" value="5025" />
  							</bean>
					</beans>
					发现没，这个文件类似于Spring的配置文件，但是，并不是说Activiti只能在Spring运行，这叫借鉴。。。
					另外：Activiti也可以使用配置文件通过编程的方式创建，其实就是手动指定配置文件路径罢了
					ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault();
					ProcessEngineConfiguration.createProcessEngineConfigurationFromResource(String resource);
					ProcessEngineConfiguration.createProcessEngineConfigurationFromResource(String resource, String beanName);
					ProcessEngineConfiguration.createProcessEngineConfigurationFromInputStream(InputStream inputStream);
					ProcessEngineConfiguration.createProcessEngineConfigurationFromInputStream(InputStream inputStream, String beanName);
					甚至为了方便，你也可以不写配置，使用默认配置构建引擎
					ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
					ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
					注意：以上的ProcessEngineConfiguration.createXXX()方法都会创建一个ProcessEngineConfiguration
					该对象调用buildProcessEngine()方法house，就可以得到一个ProcessEngine对象
					再附上一个小小的例子：
					ProcessEngine processEngine = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration()
 																			 .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE)
  																			.setJdbcUrl("jdbc:h2:mem:my-own-db;DB_CLOSE_DELAY=1000")
																			  .setAsyncExecutorEnabled(true)
																			  .setAsyncExecutorActivate(false)
																			  .buildProcessEngine();
				应该说，这才是用编程手段写配置来获取ProcessEngine对象，上面的都需要一个xml配置文件（除了默认配置）
				接下来讲解activiti.cfg.xml文件内容
				1：必须：activiti.cfg.xml必须包含一个具有id'processEngineConfiguration'的bean。eg：
				 <bean id="processEngineConfiguration" class="org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration">
				 为什么必须呢？因为这个bean就是为了构造ProcessEngine对象的。
				 然后。。class属性可以选择多个，有下列几种
				 org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration：
				 	进程引擎以独立的方式使用。 Activiti将负责处理事务。默认情况下，只有当引擎引导时才会检查数据库（如果没有Activiti表或相关的表版本不正确，则抛出异常）。
    			org.activiti.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration：
    				这是用于单元测试的便利类。 Activiti将负责处理事务。缺省情况下，使用H2内存数据库。当引擎启动并关闭时，数据库将被创建和删除。
    				使用这个时，可能不需要额外的配置（除了使用例如作业执行器或邮件功能之外）。
			    org.activiti.spring.SpringProcessEngineConfiguration：
			    	在Spring环境中使用流程引擎时使用。有关更多信息，请参阅Spring集成部分。
			    org.activiti.engine.impl.cfg.JtaProcessEngineConfiguration： 
			    	当引擎以独立模式运行时使用JTA事务。  
			    2：配置数据库
			    	定义activiti使用的数据库有两种方法：
			    	第一种：使用jdbc
			    		    jdbcUrl：数据库的JDBC URL。
   							 jdbcDriver：实现驱动程序的特定数据库类型。
    						jdbcUsername：用于连接数据库的用户名。
   							 jdbcPassword：连接到数据库的密码。
   							 具体使用方式请参阅以上的xml部分。
   							 值得一提的是：这样以jdbc属性构建的数据源具有默认Mybatis连接池设置。可以通过以下属性调整这个连接池属性
   							   jdbcMaxActiveConnections：连接池包含的最大活动连接数，默认为10
   								 jdbcMaxIdleConnections：连接池包含的最大空闲连接数
							    jdbcMaxCheckoutTime：在强制返回连接之前，可以从连接池中检出连接的时间（以毫秒为单位）。默认值为20000（20秒）。（不懂。。）
							    jdbcMaxWaitTime：这是一个低级别的设置，使池有机会打印日志状态，并重新尝试获取连接，如果它花费非常长的时间（以避免如果池配置错误，则会永远失败）默认是20000（20秒）。（不懂。。）
					第二种：使用javax.sql.DataSource实现（比如Apache Commons的DBCP）
					配置大致是酱紫的、
					<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" >
			  					<property name="driverClassName" value="com.mysql.jdbc.Driver" />
			  					<property name="url" value="jdbc:mysql://localhost:3306/activiti" />
								  <property name="username" value="activiti" />
								  <property name="password" value="activiti" />
								  <property name="defaultAutoCommit" value="false" />
				</bean>
				<bean id="processEngineConfiguration" class="org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration">
    					<property name="dataSource" ref="dataSource" />
						  .....
				</bean>
				当然。这种的话，你得添加一个此类数据源的库，比如说（DBCP)
			3:设置数据库的通用属性
				以下属性是无论数据源是jdbc还是dbcp都可以设置的属性。
				 databaseType：通常不需要指定此属性，因为它将从数据库连接元数据中自动分析。只有在自动检测失败的情况下才应指定。
				 								可能的值：{h2，mysql，oracle，postgres，mssql，db2}。
				 								不使用默认H2数据库时，此属性是必需的。
				 								此设置将确定将使用哪些创建/删除脚本和查询（有待确定）。有关支持的类型的概述，请参阅支持的数据库部分。
    			databaseSchemaUpdate：允许在进程引擎启动和关闭时设置策略来处理数据库模式。
       					 false（默认）：当创建流程引擎时，检查DB模式对库的版本，如果版本不匹配则抛出异常。
        				true：构建流程引擎时，执行检查，如果需要，执行表的更新。如果表不存在，则创建它。
        				create-drop：创建流程引擎时创建表，并在流程引擎关闭时删除表。
        	4：使用jndi来配置数据库
        			jndi的好处：将数据库的连接交于servlct管理，可以实行数据库的热部署。
        			暂时忽略，该配置需要和Spring结合
        	5：列一下activiti引用的数据库
        		数据库名         jdbc的url示例       																																					备注
        		h2                     jdbc:h2:tcp://localhost/activiti																															默认的数据库配置
        								(实际示例程序用这个url，会爆连接重置，还是jdbc:h2:mem:activiti不会有问题)			
        	   mysql               jdbc:mysql://localhost:3306/activiti?autoReconnect=true                 															 
        	   oracle				jdbc:oracle:thin:@localhost:1521:xe						
        	  postgres           jdbc:postgresql://localhost:5432/activiti
        	  db2                   jdbc:db2://localhost:50000/activiti
        	  mssql                 自己查文档去。
        	  
        	  血与泪的教训：JDBC连接符千万不要有空格，不然爆的错误让你怀疑人生
        	 6：创建activiti的数据库表
    				其实最简单的方法难道不是把processEngineConfiguration的class属性设置为	org.activiti.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration
    				或者设置databaseSchemaUpdate属性为true，这样程序一启动就帮你检查表存不存在，存在无视，不存在自动创建。
    				文档提供了别的方法
    				1：添加activiti-engine jar到classpath下
    				2：添加数据库驱动jar包
    				3：配置数据库，参阅上面我写的配置教程
    				4：执行DbSchemaCreate的main方法。
    				你知道这是根据什么sql创建的吗？
    				在你下载activiti的zip包时，里面有一个database文件夹对吧，里面的create文件夹就是创建表的脚本啦，它可是分为好几个数据库的建表脚本
    				另外，你在activiti-engine-x.jar包的org / activiti / db / create也可以找到他
    				经测试，你如果改了jar包里面的sql语句，运行时建表语句也会随之更改
    				讲一下activiti里面sql脚本文件的命名特点吧
    				activiti.mssql.create.engine.sql
    				activiti.mssql.create.history.sql
    				activiti.mssql.create.identity.sql
    				就mssql数据库脚本文件为例
    				identity是包含用户，组和用户组的成员资格的表。这些表是可选的，并且在使用引擎附带的默认身份管理时应使用它们。
    				history   历史：包含历史记录和审计信息的表。可选：当历史级别设置为无时，不需要。请注意，这也将禁用在历史数据库中存储数据的某些功能（例如评论任务）。
    				engine    activiti执行所需的表。必须的
    				敲黑板，划重点！！！使用mysql5.6.4以下版本的同学注意了。mysql5.6.4以下版本不支持以毫秒为单位的时间戳或日期
    				某版本的activiti执行创建列时会爆出异常，有些则不会。。。具体请看官方文档。。。
    		7：
    		8：数据库升级！
    				默认情况下，在创建流程引擎时，即ProcessEngine对象时会默认检查一遍数据库表版本检查。
    				如果版本不一致，抛异常。这时候就需要进行数据库表升级了
    				请注意：在升级前，你必须备份你的数据库表、
    				怎么升级？
    				1：将以下配置属性放在activiti.cfg.xml 配置文件里面
    					
 					
​				9：Job Executor和Async Executor
					  从 5.17.0版本开始，Activiti添加了一个Async Executor，Async Executor是在Activiti Engine中执行异步作业的一种性能更好的数据库友好方式。
					   因此建议切换到Async Executor。 默认情况下，旧的Job Executor仍然被使用。 
									ps：如果你的应用程序在javaee 7下运行，则ManagedJobExecutor和ManagedAsyncJobExecutor可用于让容器管理线程。
									要启用它们，请在线程工厂作如下配置：
									用到jndi，忽略。。。
						Job Executor是用来管理几个线程用来激发定时器（以及异步消息）的组件。
						在单元测试场景中，使用多个线程很麻烦，因此，api允许通过api查询：ManagementService.createJobQuery
						并执行作业：ManagementService.executeJob  这样就可以在单元测试中控制作业执行。
						如果为了避免Job Executor干扰，可以关闭它。
						<property name="jobExecutorActivate" value="false" />
						默认情况下，当流程引擎启动时，Job Executor会被激活。
						AsyncExecutor是一个管理线程池以激发定时器和其他异步任务的组件。
						默认不启用，建议启用，启用方式如下
						<property name =“asyncExecutorEnabled”value =“true”/>
						<property name =“asyncExecutorActivate”value =“true”/>
						第一个配置是弃用旧的Job Executor，启用新的Async Executor
						第二个配置是引擎启动时启动Async executor thread pool（不懂。。。）
				10：邮件配置
						activiti支持在流程运行过程中间发送电子邮件，这需要一个有效的SMTP邮件服务器配置
				11：历史配置
							可选
							<property name =“history”value =“audit”/>
				12：在表达式和脚本中公开配置的bean
						默认情况下，你配置的bena都可以在表达式或者脚本中获取到，不想让他们获取？，配置一下配置文件中beans的一个属性，该属性是一个map
						可以选择哪些bena可以被公开。公开的bean将以属性指定的名称显示
						（我倒想知道怎么在表达式或者脚本获取这些bena。。。）
				13：部署缓冲配置
						一般情况下，进程定义后都会被缓冲，避免每次进程定义后都去数据库连一遍（干嘛？），而且也是因为进程定义后数据不会改变。
						默认情况下，这个缓冲没有限制，想限制？补充下面属性
						<property name="processDefinitionCacheLimit" value="10" />
						其他的，自己查文档，一般玩不到这里吧。
				14：Activiti采用slf日志框架
						默认需要你自己添加日志的实现jar包到classpath.如log4j.
						请注意：除了添加日志jar包外，还要添加日志和slf的接口jar包
						如：slf4j-log4j12
						另：如果不添加任何日志实现jar包，Activiti默认使用NOP记录器，只会记录警告信息！
				15：映射的诊断上下文：关于日志的，不懂。。。
				16：Activiti 5.15中引入了一个事件机制。 它允许您在引擎内发生各种事件时收到通知。事件类型有几种：自己查文档去
						1：可以通过配置添加引擎范围的事件监听器
								<bean id="processEngineConfiguration" class="org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration">
   											 ...
    									<property name="eventListeners">
		      									<list>
		         										<bean class="org.activiti.engine.example.MyEventListener" />
		      									</list>
    									</property>
								</bean>
								
						2：可以通过api在运行时添加引擎范围内的事件监听器
						3：可以在特定的进程添加自定义的事件监听器
								<process id="testEventListeners">
  									<extensionElements>
								    <activiti:eventListener class="org.activiti.engine.test.MyEventListener" />
								    <activiti:eventListener delegateExpression="${testEventListener}" events="JOB_EXECUTION_SUCCESS,JOB_EXECUTION_FAILURE" />
								  </extensionElements>
 									 ...
								</process>
								这个是添加两个监听器到流程中，第一个监听器接受任何类型的事件，且只有当作业成功执行或失败时，才会通知 process engine configuration.里面
								beans属性定义的侦听器的第二个侦听器。
								其他还有事件的实现类，自己去看
				呼，配置暂时告一段落，接下里搞代码了。也就是api的使用！
				1：ProcessEngine可以获得包含工作流/ BPM方法的各种服务。 ProcessEngine和服务对象是线程安全的。
				2：ProcessEngines.getDefaultProcessEngine（）将首次初始化并构建一个进程引擎，之后总是返回相同的进程引擎。
						 所有流程引擎的正确创建和关闭可以通过ProcessEngines.init（）和ProcessEngines.destroy（）完成。
						 ps:getDefaultProcessEngine（）时已经帮你init过了。
				3：ProcessEngines会自动在classpath里面找activiti.cfg.xml和activiti-context.xml文件。
					  如果找到activiti.cfg.xml文件,要创建流程引擎需要ProcessEngineConfiguration.createProcessEngineConfigurationFromInputStream(inputStream).buildProcessEngine()
					  如果找到activiti-context.xml，想获取流程引擎将用Spring的方式获取，怎么获取请参阅Spring教程。
					  
					  -----------------各种服务的介绍--------------------
				4：RepositoryService：这可能是你使用activiti引擎的第一个服务，此服务提供了管理和操作部署和流程定义的操作。
						流程定义：他是一个流程每个步骤结构和行为的表示。
						部署：部署可以包含多个流程定义的xml文件（BPMN 2.0 xml）和任何其他资源，一个部署就是Activiti引擎内的打包单元
								选择部署中包含的内容随你选择？
						此外，这项服务还允许，
							1：查询引擎已知的部署和流程定义。   
							2：暂停和激活部署流程
							（可以发现，这个包含的基本都是静态资源，什么定义，什么资源的）
							
				5:RuntimeService :它处理《开始流程》，定义的新流程实例，要知道，每一个流程定义通常有很多实例运行（什么，同时多个人请假流程不行吗？）
												 RuntimeService也是用于获取和设置流程实例变量的服务（流程运行时肯定会有许多变量对吧）
												  Runtimeservice还允许查询流程实例和执行。
												   Runtimeservice还负责当流程实例等待外部触发器继续执行的操作。
												   流程实例可以具有多种等待状态，该服务的某些操作可以发给流程示例，以触发流程实例的外部触发器，使之能继续进行流程。


​				
​				
				6:TaskService:在Activiti中业务流程定义中的每一个执行节点被称为一个Task，Task都分组在TaskService中，包含
				查询分配给用户或组的Task
				创建新的独立Task。这些是与流程实例无关的Task（纳尼？）
   				 操作Task分配给哪个用户或哪个用户以某种方式涉及Task
   				 ?声明并完成Task，声明意味着有人愿意处理该Task，也就意味着该用户将完成该Task
   				 
   				7： IdentityService很简单。它允许组和用户的管理（创建，更新，删除，查询，...）
   				
   				8：FormService:包含起始表单和任务表单。
   						起始表单是流程实例启动前向用户显示的表单，任务表单是用户想要填写表单显示的表单，这是可选的，表单不需要嵌入到流程定义中（不懂）
				
				9：HistoryService:可以查看Activiti引擎收集的所有历史数据,执行进程时，该服务可以保留很多有关进程数据（这是可配置的）
					什么数据呢？比如说，流程实例的启动时间，谁做了哪些任务，完成任务需要多久时间，每个流程实例的执行过程等待。。
					
				10：ManagementService :一般用不上，这是用于Activiti系统的日常维护。
				
				异常部分：一般的异常，没声明却抛出的异常时这个，org.activiti.engine.ActivitiException
									其他的javadoc都有说明
									此外还有些异常请参阅官方文档。


​									
				13:流程定义的xml文档解释：
				1：根元素是definitions元素，在这个元素中，可以定义多个流程，不建议这么做，会增加维护难度。
						一个空流程定义如下所示。注意：根元素至少有xmlns和targetNamespace属性。其中targetNamespace用于分辨各类的流程。可以是任何值
						xmlns也可以是：xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
														xsi:schemaLocation="http://www.omg.org/spec/BPMN/20100524/MODEL
	             									   http://www.omg.org/spec/BPMN/2.0/20100501/BPMN20.xsd
						<definitions
 								 xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
 								 xmlns:activiti="http://activiti.org/bpmn"
  								targetNamespace="Examples">
  								<process id="myProcess" name="My First Process">
							     ..
							    </process>
						</definitions>
				2：process元素有两个属性
						id：必须的，该属性会映射到ProcessDefinition对象的key属性。然后可以通过RuntimeService上的startProcessInstanceByKey来将id所在的流程实例化出来一个。
						eg:ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myProcess");
							 要注意，这个方法和startProcessInstanceById方法不同。byid的id是Activiti引擎在部署流程时生成的id。格式为：key：version(version是什么鬼。。。)
							 并且只能是64个字符。注意不要溢出了
						 name此属性可选，并映射到ProcessDefinition的name属性，引擎不使用这个属性，但是可以在用户界面上显示更人性化的名称。
						 （我觉得学任何框架，首先找他的十分钟入门版本，练习一遍，对其中不明白的技术看用户指南，去学比较好，而不是一头扎进用户指南。。。。我败了）
						 接着来演示一个小流程，看xml应该这么写
						 这个流程是这样的：有一个公司的财务报告人员，每月都要写月度财务报告，然后写完后公司高层的一个人员获取这份报告，并传阅给公司的所有股东。让他们批准
						 （假装这里有流程图）无启动事件-写入每月财务报告-验证每月财务报告-无终止事件。
						 xml流程代码：
						 <definitions id="definitions"
												  targetNamespace="http://activiti.org/bpmn20"
												  xmlns:activiti="http://activiti.org/bpmn"
												  xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL">
												<process id="financialReport" name="Monthly financial report reminder process">
														  <startEvent id="theStart" />
														  <sequenceFlow id='flow1' sourceRef='theStart' targetRef='writeReportTask' />
														  <userTask id="writeReportTask" name="Write monthly financial report" >
														    <documentation>
														      Write monthly financial report for publication to shareholders.
														    </documentation>
														    <potentialOwner>
														      <resourceAssignmentExpression>
														        <formalExpression>accountancy</formalExpression>
														      </resourceAssignmentExpression>
														    </potentialOwner>
														  </userTask>
														  <sequenceFlow id='flow2' sourceRef='writeReportTask' targetRef='verifyReportTask' />
														  <userTask id="verifyReportTask" name="Verify monthly financial report" >
														    <documentation>
														      Verify monthly financial report composed by the accountancy department.
														      This financial report is going to be sent to all the company shareholders.
														    </documentation>
														    <potentialOwner>
														      <resourceAssignmentExpression>
														        <formalExpression>management</formalExpression>
														      </resourceAssignmentExpression>
														    </potentialOwner>
														  </userTask>
														  <sequenceFlow id='flow3' sourceRef='verifyReportTask' targetRef='theEnd' />
														  <endEvent id="theEnd" />
												</process>
						</definitions>
						startEvent标签告诉我们这个流程没有启动事件，同时也告诉我们流程的入口点是这里。
						这个xml有两个userTask，也就是有两个任务，流程有两个节点。
						第一个userTask是说写月度财务报告，id属性是标识符，name属性随便写
						documentation是该任务的描述，可以通过任务对象task.getDescription()获取
						potentialOwner标签是把任务负责人开给候选人 列表。要注意必须指定候选人列表是用户还是组。
						像xml文档，就把第一个任务分配给会计部。
						流程的每一个节点通过sequenceFlow标签来指定
						
​						
​						
​						 
​						
​				
​				
				教程告一段落，写个示例程序来巩固一下
				需求：模拟公司的一个假期请求流程
				首先，建一个流程定义的xml文档：VacationRequest.bpmn20.xml
							内容是酱紫（查阅本项目自带的VacationRequest.bpmn20.xml文件）。
					2：部署它，部署就是让Activiti引擎知道它，并把这个流程定义xml解析为可执行的文件，并把部署中添加到数据库中
							这样在引擎重新启动时，仍能记住所有部署的进程。
							代码是酱紫的（请参阅Example.java文件）


​					
​				    
​				
​					
​					