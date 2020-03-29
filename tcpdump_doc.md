### tcpdump

使用以下命名

tcpdump -S -i lo port 8080 

### 1、类型的关键字

[x] host：指明一台主机。如：host 10.1.110.110
[x] net：指明一个网络地址，如：net 10.1.0.0
[x] port：指明端口号：如：port 8090

### 2、确定方向的关键字

src：ip包的源地址，如：src 10.1.110.110
dst：ip包的目标地址。如：dst 10.1.110.110

### 3、协议的关键字（缺省是所有协议的信息包）

fddi、ip、arp、rarp、tcp、udp


6、参数详解

n：显示ip，而不是主机名；

使用curl 发起POST请求：
curl -v http://127.0.0.1:8080 -d 'hi httpd'
   
   
    
