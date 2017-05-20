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
			7：activiti创建的数据库表表名解释
					activiti创建的数据库表名全都以ACT_开头，第二部分根据表的功能不同而不同。比如说
				    ACT_RE_ *这种表名：RE代表存储库。 具有此前缀的表包含静态信息，例如流程定义和流程资源（图像，规则等）。
				    ACT_RU_ *这种表名：RU代表运行时间。 这些是包含流程实例，用户任务，变量，作业等的运行时数据的运行时表。
				    										 Activiti仅在流程实例执行期间存储运行时的数据，并在进程实例结束时删除记录。 这样可以使运行时间表小型化。
				    ACT_ID_ *这种表名：ID代表身份。 这些表包含身份信息，如用户，组等。
				    ACT_HI_ *：HI代表历史。 这些是包含历史数据的表，例如过去的流程实例，变量，任务等
				    ACT_GE_ *：一般数据，用于各种用例。（用例是什么鬼？）
			8：数据库升级！
					默认情况下，在创建流程引擎时，即ProcessEngine对象时会默认检查一遍数据库表版本检查。
					如果版本不一致，抛异常。这时候就需要进行数据库表升级了
					请注意：在升级前，你必须备份你的数据库表、
					怎么升级？
					1：将以下配置属性放在activiti.cfg.xml 配置文件里面
						<beans >
						  		<bean id="processEngineConfiguration" class="org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration">
 						   				<!-- ... -->
  							 			<property name="databaseSchemaUpdate" value="true" />
    									<!-- ... -->
  								</bean>
					</beans>
					2：为你的数据库选择合适的驱动程序添加到classpath路径下，升级你项目引用的Activiti类库。并将数据库配置为，指向拥有老版本表的数据库
					3：运行你的Activiti，大概就是先构造一个引擎实例吧，这样就可以升级了。具体的文档说的不是很清楚
					4：作为替代，你也可以在官网下载升级的数据库脚本，然后运行it
				9：Job Executor和Async Executor
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
				3：ProcessEngines
				
						
				
					
				    
				
					
					