ScaffoldUnit
============

Whether you have such experience：When you come to test some unit test with database operations, you have to manually change the database data again and again before you run JUnit test?
ScaffoldUnit is to deal with such problem. It will helps you initialize the test data you need before you run the test case. Let me see how ScaffoldUnit will do. 

> Only tested on mysql for now.

Maven dependency
-------------
```xml
<dependency>
  <groupId>org.crazycake</groupId>
  <artifactId>ScaffoldUnit</artifactId>
  <version>1.0.0-RELEASE</version>
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
```

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
		ScaffoldUnit.dbAssertThat("select name from student where id=1", is("ted"));
		
	}
}
```


####STEP 4. create json file
You should create the json file beside your test class which name is the same as your test class. Here we take `ScaffoldUnitTest.java` for example. You should create a json file named `ScaffoldUnit.json` beside it.

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
Every test case you needs ScaffoldUnit to initialize the database should have a json file beside it.
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
###more complex example
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

##Database assert
For convenient, **ScaffoldUnit** provide a method `dbAssertThat` . You can assign a sql and a `Matcher` . **ScaffoldUnit** will run this sql and compare the first column of first row of result to the `Matcher`
```java
import static org.hamcrest.CoreMatchers.*;
...

ScaffoldUnit.dbAssertThat("select name from student where id=1", is("ted"));
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

DROP TABLE IF EXISTS `nemo_keyword`;

CREATE TABLE `nemo_keyword` (
  `keyword_id` int(11) NOT NULL auto_increment,
  `keyword` varchar(300) NOT NULL,
  PRIMARY KEY  (`keyword_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
```


##If you found any bugs
Please send email to idante@qq.com