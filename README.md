[![QQ](http://pub.idqqimg.com/wpa/images/group.png)](https://jq.qq.com/?_wv=1027&k=5HWgxBZ)

获取数据集通用API
===================

欢迎加入QQ群讨论（群号：604844003）

在我们开发某些业务应用时，只需要获取数据集即可，比如报表系统，大屏展现等，那么我们可以设计一个简单的接口，用来获取数据，通过一个配置文件配置好我们的接口，这样在调试前端程序时，我们可以无需关注后台代码，也无需重启的情况下调试各个Rest接口：

【2019-10-16】 v1.0.1
- 新增多个xml配置
- 增加apis节点，支持通过js编写api；
- 支持接口登录认证，report和api都只支持token方式认证；
- 扩展js函数库：常用函数封装成js接口；

v1.0.0
- 通过在XML中新增report节点来展现图表（基于React），支持多条数据；
- 数据集支持通过`SQL`或`存储过程`或`Java类`或`JS`编写；；
- 支持不同的业务数据通过不同的API接口提供；
- 支持groupby操作；
- 修改配置，增加API接口无需重启服务，实时生效；
- js函数库支持数据库CRUD操作；
- 内含Excel通用导入程序，导入的Excel可编写API对外提供接口获取数据；

<img src='http://h5.sumslack.com/1212.png'  alt='preview' />

一个demo的xml文件：

```
<?xml version="1.0" encoding="UTF-8"?>
<reports ds="default">
	<report id="avgTemp" title="计算城市平均温度" java-align-data="true" ds="default" startDate="${date}" endDate="2019-09-01" step="month" dateFormat="yyyyMM">
		<row label="测试数据1 " align="right" fontWeight="bold">
			<![CDATA[
				{call testRetSelect('${date}')}
			]]>
		</row>
		<row label="测试数据2" align="right" fontWeight="bold">
			<![CDATA[			
				select city field,avg(temp) v 
				from test_city_temp
				where dt>='${date}'
				group by city;
			]]>
		</row>
	</report>
	<report id="testWeek" title="测试动态列周" ds="default" startDate="2019-08-27" endDate="2019-09-01" step="day" dateFormat="yyyyMMdd">
		<row label="test " align="right" fontWeight="bold">
			<![CDATA[
				{call testRetSelect('${date}')}
			]]>
		</row>
	</report>
	<report id="testJavaAlignData" title="测试后台对齐数据" java-align-data="true" ds="default" startDate="2019-08-07" endDate="2019-08-09" step="day" dateFormat="yyyyMMdd">
		<row label="d1">
			<![CDATA[
				select DATE_FORMAT(dt,'%Y%m%d') field,temp v from test_city_temp where city = 1
			]]>
		</row>
		<row label="d2">
			<![CDATA[
				select DATE_FORMAT(dt,'%Y%m%d') field,temp v from test_city_temp where city = 8
			]]>
		</row>
	</report>
	
				
	<report id="TestIndex" title="首页指标结果">
		<row id="aaa" label="总有效用户数">
			select count(*) num from test_city
		</row>
		<row id="bbb" label="总有效用户数">
			select 1111
		</row>
	</report>
	
	<report id="reportJava" java="com.sumslack.dataset.api.report.impl.ReportTest" />
</reports>
```