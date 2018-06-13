package com.example;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

import java.util.Date;

/**
 * 当入职经验小于3年时，执行这个Java任务，之前这个是脚本任务
 */
public class AutomatedDataDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Date now = new Date();
        //设置流程变量
        execution.setVariable("autoWelcomeTime", now);
        //检索流程变量
        System.out.println("假设已经为 ["
                + execution.getVariable("fullName") + "]执行了后端任务了");
    }
}
