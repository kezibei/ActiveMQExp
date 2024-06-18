package sonomon.deser.gadgets;

import java.io.FileInputStream;

public class FileRead {
	public static byte[] getjavaSerializedData(String payload) throws Exception {
    	FileInputStream inputFromFile = new FileInputStream(payload);
    	byte[] bs = new byte[inputFromFile.available()];
    	inputFromFile.read(bs);
    	inputFromFile.close();
    	return bs;
    }
}