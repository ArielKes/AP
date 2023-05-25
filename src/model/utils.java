package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
}
