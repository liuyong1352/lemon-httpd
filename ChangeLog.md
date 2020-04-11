## 本次修改内容

#### http1.0.1.7-重构Client 修改成NIO


#### http1.0.1.6-修改结束判断方式

1.修改异步运行所有任务代码
2.client不可以使用#判断所有消息已接受完，发出的消息可能会在#之后到达，需修改成判断字节数来表示接受完毕

#### http1.0.1.5-解决register性能问题

通过异步任务解决

#### http1.0.1.4-将Server端修改成MutilReactor,遇到register性能问题

1. register方法,如果存在线程切换性能严重下降，内部使用了lock

SelectorImpl.lockAndDoSelect synchronized(this.publicKeys) 与 
SelectorImpl.register(SelectorImpl.java:132)  synchronized(this.publicKeys) {this.implRegister(var4);}

存在锁竞争,doselect 又属于阻塞操作，所以新的连接注册就会需要一直等待锁释放

#### http1.0.1.3-将Server端修改成MutilReactor


#### http1.0.1.2-修改Client端进行多线程测试

1. 模拟Rst报文
2. 修改client端方便多线测试
3. 修改client发送报文包含中文问题

### 解决Server发送太快，Client端并未完全接收
使用注册Op_Write事件进行处理 ， 在服务端发送write返回为不完全发送的时候 ，注册该事件
客户端读取数据后，例如读走1024字节，不会立马出发Op_Write，在window上面发现需要读取大概
800*1024字节才会出发，服务端Op_Write ； 至于这个现象个人推测是Tcp协议，认为网络已经拥塞了
，采取了拥塞处理算法策略

### server端 tcp拆包粘包递归实现

1. Client不停的发送消息，先不管响应内容，如果等待读取内容，就模拟不出来
2. 解决拆包粘包Handler需要保留上次未消费完剩余的字节，等待下次读取放到缓存的最前端，然后继续处理
3. server 太快，可能tcp发送队列已满， 这个一般由于客户端没有进行处理，处理速度太慢，由于server配置成了异步模式socketChannel.write并不会阻塞
但是返回的字节数为0，或者小于待发送的字节数

### 实现单线程Reactor

实现自定义协议格式，对报文解析传入的字符串 ，这么做有个问题如果要解析http请求无法处理

细节点：

SocketChannel关闭细节

1. 客户端主动关闭 ，服务端read会得到-1
2. 客户端不关闭直接退出，服务端继续read，直接抛异常
3. 服务端如果不调用close ， selector中注册的keys，会一直增加，导致select异常


