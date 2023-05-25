package model;

import java.io.IOException;
import java.lang.management.ManagementFactory;

public class utils {
    public static long getProcessId() throws IOException {
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);}
}
