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
            return new GameClient.Request(command,response, data);
        }


        return request;
    }

    public static HashMap<String, Integer> convertStringToHashMap(String input) {
        HashMap<String, Integer> resultMap = new HashMap<>();

        // Split the string by newline character to get individual key-value pairs
        String[] keyValuePairs = input.split("\n");

        for (String keyValue : keyValuePairs) {
            // Split each key-value pair by colon
            String[] pair = keyValue.split(": ");

            if (pair.length == 2) {
                String key = pair[0].trim();
                int value = Integer.parseInt(pair[1].trim());

                // Add the key-value pair to the HashMap
                resultMap.put(key, value);
            }
        }

        return resultMap;
    }
}


