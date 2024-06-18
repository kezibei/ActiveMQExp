package sonomon;

import com.unboundid.util.Base64;

public class Config {
    public static  String rhost = "127.0.0.1";
    public static  int rport = 61616;
    public static  String lhost = "127.0.0.1";
    public static  int ldapport = 1389;
    public static  int httpport = 9998;
    public static  String gadget = "cb19";
    public static  String cmd = "whoami";
    public static  String url = "http://127.0.0.1:9998/";
	public static  String base64cmd;
    public static  String web_user = "admin";
    public static  String web_pass = "admin";
	
    public Config() {
    	
	}
    
	public static String getWeb_user() {
		return web_user;
	}
	public static void setWeb_user(String web_user) {
		Config.web_user = web_user;
	}
	
	public static String getWeb_pass() {
		return web_pass;
	}
	public static void setWeb_pass(String web_pass) {
		Config.web_pass = web_pass;
	}
    
	public static String getRhost() {
		return rhost;
	}
	public static void setRhost(String rhost) {
		Config.rhost = rhost;
	}
	public static int getRport() {
		return rport;
	}
	public static void setRport(int rport) {
		Config.rport = rport;
	}
	public static String getLhost() {
		return lhost;
	}
	public static void setLhost(String lhost) {
		Config.lhost = lhost;
		Config.url = "http://"+lhost+":"+httpport+"/";
	}
	public static int getLdapport() {
		return ldapport;
	}
	public static void setLdapport(int ldapport) {
		Config.ldapport = ldapport;
	}
	public static int getHttpport() {
		return httpport;
	}
	public static void setHttpport(int httpport) {
		Config.httpport = httpport;
		Config.url = "http://"+lhost+":"+httpport+"/";
	}
	public static String getGadget() {
		return gadget;
	}
	public static void setGadget(String gadget) {
		Config.gadget = gadget;
	}
	public static String getCmd() {
		return cmd;
	}
	public static void setCmd(String cmd) {
		Config.cmd = cmd;
		try {
			Config.base64cmd = Base64.encode(Config.cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String getUrl() {
		return url;
	}
    
}
