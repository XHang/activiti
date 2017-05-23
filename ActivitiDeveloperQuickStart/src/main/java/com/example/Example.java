package com.example;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;

/**
 * 指南配套示例程序
 * 1：使用classpath下面的activiti.cfg.xml来配置工作流引擎
 * （之前的快速入门程序，使用的是通过编程写配置来配置工作流引擎的）
 * @author DELL
 *
 */
public class Example {
	public static void main(String[] args) {
		System.out.println("即将启动引擎");
		ProcessEngine processEngine =ProcessEngines.getDefaultProcessEngine();
		System.out.println("即将获取repositoryService服务！");
		RepositoryService repositoryService =  processEngine.getRepositoryService();
		System.out.println("即将部署请假流程！");
		repositoryService.createDeployment().addClasspathResource("VacationRequest.bpmn20.xml").deploy();
		System.out.println("当前部署的流程总数为："+repositoryService.createProcessDefinitionQuery().count());
		 RuntimeService runtimeSerivce=processEngine.getRuntimeService();
		 System.out.println("开始执行流程。。。我猜会报错");
		 try{
			 runtimeSerivce.startProcessInstanceByKey("vacationRequest");
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		System.out.println("即将关闭引擎");
		ProcessEngines.destroy();
		
	}
}
