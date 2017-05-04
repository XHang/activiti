package com.example;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;

/**
 * 工作流示例：一个入职请求
 * 过程：1利用jdbc连接数据库创建一个默认的工作流引擎，打印引擎和名字
 * 			加载流程描述文件，创建流程实体，打印流程的名字和ID
 * 由于该网络问题，以下代码完成不可。只能待有机会再补。该示例程序暂停
 * @author DELL
 *
 */
public class OnboardingRequest {
	public static void main(String[] args) {
		//配置工作流引擎
		 ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
	      .setJdbcUrl("jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000")
	      .setJdbcUsername("sa")
	      .setJdbcPassword("")
	      .setJdbcDriver("org.h2.Driver")
	      .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		 //利用配置构建一个工作流引擎
	    ProcessEngine processEngine = cfg.buildProcessEngine();
	    String pName = processEngine.getName();
	    String ver = ProcessEngine.VERSION;
	    //打印Process Engine名字和Activiti版本。
	    System.out.println("ProcessEngine [" + pName + "] Version: [" + ver + "]");
	    //利用该xml创建一个工作流进程。打印该任务
	    RepositoryService repositoryService = processEngine.getRepositoryService();
	    Deployment deployment = repositoryService.createDeployment()
	        .addClasspathResource("onboarding.bpmn20.xml").deploy();
	    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
	        .deploymentId(deployment.getId()).singleResult();
	    System.out.println(
	        "Found process definition [" 
	            + processDefinition.getName() + "] with id [" 
	            + processDefinition.getId() + "]");
	  }
	} 

