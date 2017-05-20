package com.example;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;

/**
 * 指南配套示例程序
 * 1：使用classpath下面的activiti.cfg.xml来配置工作流引擎
 * （之前的快速入门程序，使用的是通过编程写配置来配置工作流引擎的）
 * @author DELL
 *
 */
public class Example {
	public static void main(String[] args) {
		ProcessEngine processEngine =ProcessEngines.getDefaultProcessEngine();
		System.out.println("启动引擎");
		ProcessEngines.destroy();
		System.out.println("关闭引擎");
	}
}
