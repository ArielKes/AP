package model;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.HashMap;

public class utils {
    public static long getProcessId() throws IOException {
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);}
    public static HashMap<String, String> getProperties(String propertiesFileName) throws IOException {
        HashMap<String, String> p = new HashMap<>();
        BufferedReader in = new BufferedReader(new FileReader(propertiesFileName));
        String line;
        while ((line = in.readLine()) != null) {
            String[] sp = line.split("=");
            p.put(sp[0], sp[1]);
        }
        in.close();
        return p;
    }


    public static final GameClient.Request getRequestFromInput(InputStream inputStream) throws IOException, ClassNotFoundException {
        String response = null;
        GameClient.Request request = null;
        while (inputStream.available() == 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ObjectInputStream in = new ObjectInputStream(inputStream);
        response = in.readUTF();
        if (response != null) {
            String command = in.readUTF();
            Serializable data = (Serializable) in.readObject();
            return new GameClient.Request(response, command, data);
        }


        return request;
    }

}
