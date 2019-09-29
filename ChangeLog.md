
## 本次修改内容

### 增加测试程序  优化输出
使用tcpdump -S -i lo port 10080 观察
多次write,会发和多tcp报文段  
修改后只发一次数据