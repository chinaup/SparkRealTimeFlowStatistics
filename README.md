视频网站实时流统计实战
============

工具：hadoop、spark、flume、hbase、kafka

实现步骤
====
日志生成：crontab -e    */1 * * * * /root/logs/log_generator.sh  定时器每分钟产生一次日志

数据采集：使用flume监听logs文件夹下的日志数据传入kafka中

实时流处理：开发sparkstreaming程序获取kafka数据源的实时流数据，进行处理后保存到HBase数据库中

可视化展示：建立springboot项目读取hbase数据库中的数据，使用echarts实时展示位饼图
