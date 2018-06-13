package com.example;

import org.activiti.engine.*;
import org.activiti.engine.form.FormData;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.engine.impl.form.LongFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 指南配套示例程序
 * 1：使用classpath下面的activiti.cfg.xml来配置工作流引擎
 * （之前的快速入门程序，使用的是通过编程写配置来配置工作流引擎的）
 * @author DELL
 *
 */
public class Example {
	public static void main(String[] args) throws ParseException {
		new Example().test3();
	}

	/**
	 * 入门程序1
	 * 简单的部署，创建工作流引擎
	 */
	@Test
	public void example1(){
		//工作流引擎配置
		ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
				.setJdbcUrl("jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000")
				.setJdbcUsername("sa")
				.setJdbcPassword("")
				.setJdbcDriver("org.h2.Driver")
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		//根据配置构建一个工作流引擎
		ProcessEngine processEngine = cfg.buildProcessEngine();
		String name = processEngine.getName();
		String ver = ProcessEngine.VERSION;
		//打印工作流引擎名称和版本
		System.out.println("ProcessEngine [" + name + "] Version: [" + ver + "]");
	}

	/**
	 * 入门程序2：部署一套流程，并查看该流程的流程名称和ID
	 */
	@Test
	public void example2(){
		ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
				.setJdbcUrl("jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000")
				.setJdbcUsername("sa")
				.setJdbcPassword("")
				.setJdbcDriver("org.h2.Driver")
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		ProcessEngine processEngine = cfg.buildProcessEngine();
		String pName = processEngine.getName();
		String ver = ProcessEngine.VERSION;
		System.out.println("ProcessEngine [" + pName + "] Version: [" + ver + "]");
		//加载BPMN模型并加载到工作流引擎中
		RepositoryService repositoryService = processEngine.getRepositoryService();
		Deployment deployment = repositoryService.createDeployment()
				.addClasspathResource("onboarding.bpmn20.xml").deploy();
		//检索已部署的流程模型，确保它位于工作流的存储库中
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.deploymentId(deployment.getId()).singleResult();
		System.out.println(
				"发现一个流程定义! ["
						//流程名称
						+ processDefinition.getName() + "] with id ["
						//流程的唯一ID
						+ processDefinition.getId() + "]");
	}

	/**
	 * 入门程序3
	 * 需要在main函数中调用，Junit单元测试不能用，因为该程序需要键盘输入
	 * 该程序主要实现一个流程
	 * @throws ParseException
	 */
	public void test3() throws ParseException {
		ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
				.setJdbcUrl("jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000")
				.setJdbcUsername("sa")
				.setJdbcPassword("")
				.setJdbcDriver("org.h2.Driver")
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		ProcessEngine processEngine = cfg.buildProcessEngine();
		String pName = processEngine.getName();
		String ver = ProcessEngine.VERSION;
		System.out.println("引擎名： [" + pName + "] 版本: [" + ver + "]");
		//检索已部署的流程模型，确保它位于工作流存储库中
		RepositoryService repositoryService = processEngine.getRepositoryService();
		Deployment deployment = repositoryService.createDeployment()
				.addClasspathResource("onboarding.bpmn20.xml").deploy();
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.deploymentId(deployment.getId()).singleResult();
		System.out.println(
				"发现流程定义 ["
						+ processDefinition.getName() + "] with id ["
						+ processDefinition.getId() + "]");
		//-----------启动流程示例------------
		RuntimeService runtimeService = processEngine.getRuntimeService();
		ProcessInstance processInstance = runtimeService
				.startProcessInstanceByKey("onboarding");
		System.out.println("入职流程已经启动： id ["
				+ processInstance.getProcessInstanceId()
				+ "] key [" + processInstance.getProcessDefinitionKey() + "]");
		//-----------启动流程示例------------
		TaskService taskService = processEngine.getTaskService();
		FormService formService = processEngine.getFormService();
		//历史流程相关
		HistoryService historyService = processEngine.getHistoryService();

		Scanner scanner = new Scanner(System.in);
		while (processInstance != null && !processInstance.isEnded()) {
			//收集管理员角色的任务列表
			List<Task> tasks = taskService.createTaskQuery()
					.taskCandidateGroup("managers").list();
			System.out.println("活动的未交付任务数: [" + tasks.size() + "]");
			for (int i = 0; i < tasks.size(); i++) {
				Task task = tasks.get(i);
				System.out.println("流程任务 [" + task.getName() + "]");
				Map<String, Object> variables = new HashMap<String, Object>();
				FormData formData = formService.getTaskFormData(task.getId());
				for (FormProperty formProperty : formData.getFormProperties()) {
					//根据流程模型定义的表单属性类型，提醒用户不同的输入
					if (StringFormType.class.isInstance(formProperty.getType())) {
						System.out.println(formProperty.getName() + "?");
						String value = scanner.nextLine();
						variables.put(formProperty.getId(), value);
					} else if (LongFormType.class.isInstance(formProperty.getType())) {
						System.out.println(formProperty.getName() + "? (必须是整数)");
						Long value = Long.valueOf(scanner.nextLine());
						variables.put(formProperty.getId(), value);
					} else if (DateFormType.class.isInstance(formProperty.getType())) {
						System.out.println(formProperty.getName() + "? (必须是日期类型，如 2018/06/06)");
						DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
						Date value = dateFormat.parse(scanner.nextLine());
						variables.put(formProperty.getId(), value);
					} else {
						System.out.println("<form type not supported>");
					}
				}
				taskService.complete(task.getId(), variables);
				//流程的历史相关
				HistoricActivityInstance endActivity = null;
				List<HistoricActivityInstance> activities =
						historyService.createHistoricActivityInstanceQuery()
								.processInstanceId(processInstance.getId()).finished()
								.orderByHistoricActivityInstanceEndTime().asc()
								.list();
				for (HistoricActivityInstance activity : activities) {
					if (activity.getActivityType() == "startEvent") {
						System.out.println("BEGIN " + processDefinition.getName()
								+ " [" + processInstance.getProcessDefinitionKey()
								+ "] " + activity.getStartTime());
					}
					if (activity.getActivityType() == "endEvent") {
						// Handle edge case where end step happens so fast that the end step
						// and previous step(s) are sorted the same. So, cache the end step
						//and display it last to represent the logical sequence.
						endActivity = activity;
					} else {
						System.out.println("-- " + activity.getActivityName()
								+ " [" + activity.getActivityId() + "] "
								+ activity.getDurationInMillis() + " ms");
					}
				}
				if (endActivity != null) {
					System.out.println("-- " + endActivity.getActivityName()
							+ " [" + endActivity.getActivityId() + "] "
							+ endActivity.getDurationInMillis() + " ms");
					System.out.println("COMPLETE " + processDefinition.getName() + " ["
							+ processInstance.getProcessDefinitionKey() + "] "
							+ endActivity.getEndTime());
				}
				//流程的历史相关
			}
			// Re-query the process instance, making sure the latest state is available
			processInstance = runtimeService.createProcessInstanceQuery()
					.processInstanceId(processInstance.getId()).singleResult();
		}
		scanner.close();
	}

	public void test4(){
		//在classpath里面寻找activiti.cfg.xml配置文件，并以此创建一个工作流引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	}
}
