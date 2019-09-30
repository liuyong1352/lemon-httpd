
## 本次修改内容

### 增加测试程序  优化输出
使用tcpdump -S -i lo port 10080 观察 , 此处在linux操作比较方便 
进入到编译后 target目录 classes下  

启动server 

java org.lemon.HttpServer 10080

启动test

java org.lemon.HttpTest http://127.0.0.1:10080


多次write,会发和多tcp报文段  
修改后只发一次数据