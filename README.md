视频网站实时流统计实战
============

工具：hadoop、spark、flume、hbase、kafka

step1：
日志生成：crontab -e 
          */1 * * * * /root/logs/log_generator.sh  定时器每分钟产生一次日志
