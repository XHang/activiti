  <bean id="processEngineConfiguration" class="org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration">
  ��������������ProcessEngine��bena���ж��class���Բ���
 org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration: �������е��������档Activiti���Լ��������� Ĭ�ϣ����ݿ�ֻ����������ʱ��� �����û��Activiti�ı���߱�ṹ����ȷ�ͻ��׳��쳣����
org.activiti.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration: ��Ԫ����ʱ�ĸ����ࡣActiviti���Լ��������� Ĭ��ʹ��H2�ڴ����ݿ⡣���ݿ�������������ʱ�������ر�ʱɾ���� ʹ����ʱ������Ҫ�������ã�����ʹ��jobִ�������ʼ����ܣ���
org.activiti.spring.SpringProcessEngineConfiguration: ��Spring������ʹ���������档 �ο�Spring�����½ڡ�
org.activiti.engine.impl.cfg.JtaProcessEngineConfiguration: ���������������棬��ʹ��JTA����
ע��û���������Թ�����ʵ�ϣ���һ��class�г��ԣ����ǽ��ȴû��Ԥ���еı���
ʹ�õ���h2���ݿ⣬�����Ǹ�����ʱ����û�б���û�����ݡ����ǲ�������������:���ݿ�ֻ����������ʱ��� �����û��Activiti�ı���߱�ṹ����ȷ�ͻ��׳��쳣����
ԭ���ѽ����������鿴��ȥ


�����ļ��ṹ
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"        
							xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"       
							 xsi:schemaLocation="http://www.springframework.org/schema/beans   
							 									http://www.springframework.org/schema/beans/spring-beans.xsd">
  <bean id="processEngineConfiguration" class=" org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration">
    <property name="jdbcUrl"    value="jdbc:h2:mem:activiti;DB_CLOSE_DELAY=1000"/>     
  </bean>
</beans>
�����property��name�����ж��ѡ��
databaseType   һ�㲻�����ã���������ȡ���ݿ�����ʱ���Զ��жϣ����ܵ�ֵ��h2, mysql, oracle, postgres, mssql, db2
databaseSchemaUpdate  �������������͹ر�ʱ��δ������ݿ��
	false��Ĭ�ϣ���������ݿ��İ汾��������İ汾�� ����汾��ƥ����׳��쳣��
	true: ������������ʱ��ִ�м�飬�����Ҫ��ִ�и��¡� ��������ڣ��ʹ�����
	create-drop: ������������ʱ�������ݿ�� �ر���������ʱɾ����Щ��
	PS:����true�Ļ������ҹ����������classΪ org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration���Ϳ��������ݿ�����ڵ�ʱ�򲻱����𡣡������⣡
	
	
JNDI���ݿ������
	Ҫ�Ķ����ݿ������ÿ�ζ�Ҫ���´�war��������̫�鷳��jndi�����㣡
	
	
