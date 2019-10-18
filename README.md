[![QQ](http://pub.idqqimg.com/wpa/images/group.png)](https://jq.qq.com/?_wv=1027&k=5HWgxBZ)

极速编写后台API
===================

欢迎加入QQ群讨论（群号：604844003）

以一种全新的方式编写后台API，你可以通过`Java`或`JS`语言极速的编写后台API，通过自动生成的配置文件，在该配置文件中新增api或report节点来创建动态API，我们封装了大量的后台js类供配置文件使用，
这样在调试前端程序时，我们可以**无需关注后台代码**，也**无需重启**的情况下调试各个Rest接口。

v1.0.2
- 可以通过`cloud.get/post/delete/put`接口调用Sumslack开放平台云API


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
- 内含Excel通用导入程序，Excel通过加入一个定义Sheet可将任意Excel导入到数据库中；

<img src='http://h5.sumslack.com/1212.png'  alt='preview' />

一个demo的xml文件：

```html
<?xml version="1.0" encoding="UTF-8"?>
<xml>
<datasources>
    <datasource name="test">
      	 <property name="driver" value="com.mysql.jdbc.Driver" />
		  <property name="url" value="jdbc:mysql://[IP]:[PORT]/[DATABASE_NAME]?useUnicode=true&amp;characterEncoding=utf8&amp;autoReconnect=true" />
		  <property name="user" value="xxxxx" />
		  <property name="password" value="xxxxx" />
    </datasource>
</datasources>
<reports ds="default">
	<report id="testJS" title="根据日期自动groupby" type="map" startDate="2019-08-05" endDate="2019-08-12" step="day" java-align-data="true" dateFormat="yyyy-MM-dd">
		<row id="jstest" label="测试测试" lang="js" field-field="dt" field-v="num">
			return Db.use().query("select date_format(dt,'%Y-%m-%d') dt,temp num from test_city_temp where city=?",1);
		</row>
		<row id="jstest222" label="测试测试" lang="js" field-field="dt" field-v="num">
			return Db.use().query("select date_format(dt,'%Y-%m-%d') dt,temp num from test_city_temp where city=?",2);
		</row>
	</report>				
	<report id="TestIndex" title="多个数据集数据会自动合并">
		<row id="aaa" label="总有效用户数">
			select count(*) num from test_city
		</row>
		<row id="bbb" label="总有效用户数">
			select 1111
		</row>
	</report>
	
	<report id="reportJava" title="Java类编写业务" java="com.sumslack.dataset.api.report.impl.ReportTest" />
</reports>
<apis>
	<api id="login" lang="js" title="登录接口">
		<![CDATA[			
		logger.d("start login...");
		var _user = Db.one("select id,user_name from user where user_name = ? and password = ?",params.username,params.password);
		if(_user!=null){
			var _token = Auth.genToken();
			var userBean = {userid:_user.id,username:_user.user_name,token:_token};
			var res = Auth.storeToken(_token,userBean);
			if(res){
				return {code:0,msg:"login ok.",user:userBean};
			}else{
				return {code:500,msg:"login failed."};
			}
		}
		return {code:404,msg:"用户名密码错误."};
		]]>
	</api>
	<api id="logout" lang="js" title="登出接口">
		Auth.logout("${token}","${userid}");
		return "logout ok";
	</api>
  	<api id="array" lang="js" auth="true" title="需要验证的测试接口">
		return [{a:1},{a:2}];
	</api>
	<api id="testds" lang="js" ds="test" title="测试自定义数据源">
		return Db.list("select * from base_user where uid>?",0);
	</api>
</apis>
</xml>
```