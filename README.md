ScaffoldUnit
============

[![Build Status](https://travis-ci.org/alexxiyang/ScaffoldUnit.svg?branch=master)](https://travis-ci.org/alexxiyang/ScaffoldUnit)

Whether you have such experience：When you come to test some unit test with database operations, you have to manually change the database data again and again before you run JUnit test?
ScaffoldUnit is to deal with such problem. It will helps you initialize the test data you need before you run the test case. Let me see how ScaffoldUnit will do. 

Maven dependency
-------------
```xml
<dependency>
  <groupId>org.crazycake</groupId>
  <artifactId>ScaffoldUnit</artifactId>
  <version>1.1.0-RELEASE</version>
</dependency>
```

Quick start
-------------

####STEP 1. create ScaffoldUnit.properties
create `ScaffoldUnit.properties` at the root of classpath.  Here is an example
```properties
ScaffoldUnit.jdbc.url=jdbc:mysql://localhost:3306/sunit_test?useUnicode=true&characterEncoding=UTF-8
ScaffoldUnit.jdbc.username=root
ScaffoldUnit.jdbc.password=123456
ScaffoldUnit.jdbc.type=mysql
```

Support types:

 - mysql
 - hbase

####STEP 2. create test database
create a database named `sunit_test` and create a table `student`
```sql
CREATE TABLE `student` (
  `id` int(11) NOT NULL,
  `name` varchar(32) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

####STEP 3. create test case class
Create a test class name `HelloScaffoldUnitTest.java` and there is a method named `testBuild`. 

```java
package org.crazycake.ScaffoldUnit;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;
import org.junit.Assert;

public class HelloScaffoldUnitTest {
	
	@Test
	public void testBuild() throws IOException, SQLException, ClassNotFoundException{
		
		//1 build the scaffold data
		ScaffoldUnit.build();
		
		//2 test your code
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = null;
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sunit_test?useUnicode=true&characterEncoding=UTF-8","root", "123456");
		Statement stat = conn.createStatement();
		stat.execute("update student set name='ted' where name='jack'");
		stat.close();
		conn.close();
		
		//3 assert your result
		SCol queryCondition = new SCol();
		queryCondition.setC("id");
		queryCondition.setV(1);
		Object actual = ScaffoldUnit.queryOneValue("name","student",queryCondition);
		Assert.assertThat((String)actual,is("ted"));
		
	}
}
```


####STEP 4. create json file
You should create the json file which name is the same as your test class. And under `test/resources` create the same folders just like the folders which your java file in. For example your java file `ScaffoldUnitTest.java` package is `org.crazycake.ScaffoldUnit` then the json file path should be `test/resources/org/crazycake/ScaffoldUnit/ScaffoldUnitTest.json`.

```json
{
	"ms":[
		{
			"n":"testBuild",
			"ts":[
				{
					"t":"student",
					"rs":[
						[
							{
								"c":"id",
								"v":"1"
							},
							{
								"c":"name",
								"v":"'jack'"
							}
						]
					]
				}
			]
		}
	]
}
```
This json means before test method `testBuild` ScaffoldUnit should do  these 2 things:
1. clean table `sunit_hello` (with truncate command) . 
2. insert one row which id=3, name= 'jack'. 

####STEP 5. launch!
Run your test case and wait for the green bar!
If you add log4j to your project then you will see these logs.
```
2014-09-21 17:55:37 DEBUG ScaffoldUnitDao:47 - ScaffoldUnit.jdbc.url=jdbc:mysql://localhost:3306/sunit_test?useUnicode=true&characterEncoding=UTF-8
2014-09-21 17:55:37 DEBUG ScaffoldUnitDao:50 - ScaffoldUnit.jdbc.username=root
2014-09-21 17:55:37 DEBUG ScaffoldUnitDao:53 - ScaffoldUnit.jdbc.password=qwer1234
2014-09-21 17:55:37 DEBUG ScaffoldUnitDao:73 - truncate table student
2014-09-21 17:55:37 DEBUG ScaffoldUnitDao:73 - insert into student (id,name) values (1,'jack')
2014-09-21 17:55:37 DEBUG ScaffoldUnitDao:104 - select name from student where id=1
```

#Introducton

##We don't recover data, we clean the data before test 

**ScaffoldUnit** won't recover the test data. Instead, it clean all data of the tables which test case will use and initialize the data.

##xxxx.json
Every test case you needs ScaffoldUnit to initialize the database should have a json file be placed in `/src/test/resources` folder. And make sure the path structure this json have is the same as the test case java file. For example, you want to run this test case `org/crazycake/ScaffoldUnit/HelloScaffoldUnitTest.java` , you should create a json file name `HelloScaffoldUnitTest.json` and place it under `src/test/resources/org/crazy/ScaffoldUnit/`

###attribute names
Here are the meas of the json attributes

 - ms: method names 
 - n: name 
 - ts: tables  
 - t: table name 
 - rs: test data rows
 - c: column 
 - v: value

###only clean data
If you want ScaffoldUnit just clean the data of table and don't want to insert any data. You can only specify the table name with `t` and not write `rs` attribute. Like this:
```json
{
	"t":"nemo_clean"
}
```

###how to write value
**ScaffoldUnit** won't do anything plus to the value you defined. And it doesn't use `preparedStatment` . It just join these columns and values together. So if your value type is `String` you have to surround the value with `'` . Like this: 
```json
{
	"c":"name",
	"v":"'jack'"
}
```
###example
```json
{
	"ms":[
		{
			"n":"testComeAndBiteMe",	
			"ts":[
				{
					"t":"nemo_hello",
					"rs":[
						[
							{
								"c":"id",
								"v":"1"
							},
							{
								"c":"name",
								"v":"'foo'"
							}
						],
						[
							{
								"c":"id",
								"v":"2"
							},
							{
								"c":"name",
								"v":"'bar'"
							}
						]						
					]
				}
			]
		},
		{
			"n":"testBuild",
			"ts":[
				{
					"t":"nemo_hello",
					"rs":[
						[
							{
								"c":"id",
								"v":"3"
							},
							{
								"c":"name",
								"v":"'mike'"
							}
						],
						[
							{
								"c":"id",
								"v":"4"
							},
							{
								"c":"name",
								"v":"'kitty'"
							}
						]
					]
				},
				{
					"t":"nemo_clean"
				}
			]
		}
	]
}
```

##build methods
Call `ScaffoldUnit.build()` when you're ready. It will initialize your test data. You can also use these methods: `comeAndBiteMe` `iHateWorkOvertime` `screwU` `myBossIsAMuggle` and `wtf`. They  are all equivalent to `build()`, nothing different. If you want to add more method like them then create the pull request. :) 

##queryOneValue
For convenient, **ScaffoldUnit** provide a method `queryOneValue` . You can query one column of one row from a table without writing database connection code at all. 
```java
import static org.hamcrest.CoreMatchers.*;
...

SCol queryCondition = new SCol();
queryCondition.setC("id");
queryCondition.setV(1);
Object actual = ScaffoldUnit.queryOneValue("name","student",queryCondition);
Assert.assertThat((String)actual,is("ted"));
```

##auto initialize structure
**ScaffoldUnit** also provide a way to auto initialize database structure. Create a sql file `ScaffoldUnit.sql` under the root path of your classpath and put the initialize sql in this file. When `ScaffoldUnit` build the data occur the exception which message is "Table ... doesn't exist" , it will try to initialize the structure with `ScaffoldUnit.sql`.

An example of `ScaffoldUnit.sql` . I created it by using the export function of SQLyog. 
```sql
/*
SQLyog Ultimate v8.71 
MySQL - 5.0.51b-community-nt : Database - nemo_test
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

/*Table structure for table `nemo_clean` */

DROP TABLE IF EXISTS `nemo_clean`;

CREATE TABLE `nemo_clean` (
  `id` int(11) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `nemo_hello` */

DROP TABLE IF EXISTS `nemo_hello`;

CREATE TABLE `nemo_hello` (
  `id` int(11) NOT NULL,
  `name` varchar(32) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/*Table structure for table `nemo_keyword` */

DROP TABLE IF EXISTS `student`;

CREATE TABLE `student` (
  `id` int(11) NOT NULL,
  `name` varchar(300) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
```

##Hbase support
ScaffoldUnit can also support `Hbase` . Here is an example of Hbase configuration.
update `ScaffoldUnit.properties` under classpath
```properties
ScaffoldUnit.jdbc.url=host1:2181,host2:2181
ScaffoldUnit.jdbc.username=
ScaffoldUnit.jdbc.password=
ScaffoldUnit.jdbc.type=hbase
```
create `ScaffoldUnit.hbase` under classpath. Write down what tables and what column families these tables should have.
```bash
student:info1,info2
employee:family,company
```

> NOTE: ScaffoldUnit will truncate all tables you wrote. So make sure you use a blank hbase database instead of using production environment or use some fake table to test.

create `helloworld.json` . The first column must name `rowkey`. Column naming rule is : `column family name` : `column name`. For example `info1:name`.
```javascript
{
   "ms":[
		"n":"testHbaseBuild",
		"ts":[
			{
				"t":"student",
				"rs":[
					[
						{
							"c":"rowkey",
							"v":"row1"
						},
						{
							"c":"info1:name",
							"v":"jack"
						}
					],
					[
						{
							"c":"rowkey",
							"v":"row2"
						},
						{
							"c":"info1:name",
							"v":"ted"
						}
					]
				]
			}
		]
	]
}
```

##If you found any bugs
Please send email to idante@qq.com
