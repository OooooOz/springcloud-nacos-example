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
