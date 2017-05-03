package com.example;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;

/**
 * 工作流示例：一个登机请求
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
	  }
	} 

