package sonomon.deser;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class MyURLClassLoader {
  private URLClassLoader classLoader;
  
  public MyURLClassLoader(String jarName) {
    try {
      this.classLoader = getURLClassLoader(jarName);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } 
  }
  
  public Class loadClass(String className) {
    try {
      Method method = URLClassLoader.class.getDeclaredMethod("findClass", new Class[] { String.class });
      method.setAccessible(true);
      Class clazz = (Class)method.invoke(this.classLoader, new Object[] { className });
      return clazz;
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } 
    return null;
  }
  
  private URLClassLoader getURLClassLoader(String jarName) throws MalformedURLException {
    String path = System.getProperty("user.dir") + File.separator + "lib" + File.separator + jarName;
    File file = new File(path);
    if (!file.exists()) {
		System.out.println("[*] 必须存在此文件: "+path+"\r\n");
		return null;
	}
    URL url = file.toURI().toURL();
    URLClassLoader urlClassLoader = new URLClassLoader(new URL[] { url });
    return urlClassLoader;
  }
}
