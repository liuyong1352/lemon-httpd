**http响应报文格式**

Status-Line<br>  
`*`( general-header 
               | response-header 
               | entity-header )<br>  
CRLF<br>  
[ message-body ]<br>  

Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF  

message-header = field-name ":" [ field-value ] CRLF