#### 1. win端口占用处理

> - netstat -aon|findstr "8448"
> - tasklist|findstr "9088"
> - taskkill /T /F /PID 9088
>

#### 2. win10 环境中 RocketMq环境搭建

> start mqnamesrv.cmd
> start mqbroker.cmd -n 127.0.0.1:9876 autoCreateTopicEnable=true
> mvn clean package -Dmaven.test.skip=true
> java -jar target/rocketmq-dashboard-1.0.1-SNAPSHOT.jar
>
>https://blog.csdn.net/ProBaiXiaodi/article/details/127428563
> https://blog.51cto.com/u_15077562/4194762
> https://blog.csdn.net/qq_43631716/article/details/119747200
> https://blog.51cto.com/09112012/5045979

#### 3. 分支重命名
> - git branch -m old new
> - git push origin :old
> - git push --set-upstream origin new

#### 4. 下载的jar加载到本地mvn仓库

> mvn install:install-file -Dfile=./xxx-1.0.0.jar -DgroupId=cn.xxx.xxx -DartifactId=xxx -Dversion=1.0.0 -Dpackaging=jar

#### 5. excel的VLOOKUP方法

> - =VLOOKUP(B2,SheetJS!A:F,3,0)
> - 在SheetJS的A-F列查找本sheet的B2匹配的数据，取匹配的行数据的第三列数据

#### 6. 一些分析语句（MySQL 8+可用的）：

> - 查看事务隔离级别：SHOW VARIABLES LIKE 'TRANSACTION_ISOLATION'
> - 查看事务超时时间：SHOW VARIABLES LIKE 'INNODB_LOCK_WAIT_TIMEOUT'
> - 查看存储引擎状态：SHOW ENGINE INNODB STATUS;
> - 查看锁数据的分析：SELECT * FROM PERFORMANCE_SCHEMA.DATA_LOCKS;
> - 查看存储引擎事务：SELECT * FROM INFORMATION_SCHEMA.INNODB_TRX
> - 杀死死锁线程：KILL 46601363（线程id由存储引擎事务语句可查到trx_mysql_thread_id）
