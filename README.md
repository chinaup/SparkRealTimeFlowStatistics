视频网站实时流统计实战
============

工具：hadoop、spark、flume、hbase、kafka

实现步骤
====
日志生成：crontab -e    */1 * * * * /root/logs/log_generator.sh  定时器每分钟产生一次日志

数据采集：使用flume监听logs文件夹下的日志数据传入kafka中
