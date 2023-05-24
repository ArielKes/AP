package model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class GameClient {
    protected final Socket hs;
    private final HashMap<String, String> properties;
    PrintWriter out;

    public GameClient() throws IOException {
        this.properties = getProperties("src/resources/properties.txt");
        this.hs = getGameHostSocket();
    }

    public void runClient() throws IOException {
        out = new PrintWriter(hs.getOutputStream());
        out.println("Q:moshe");
        out.flush();
    }

    Socket getGameHostSocket() throws IOException {
        assert properties != null;
        int port = Integer.parseInt(properties.get("game_host.port"));
        return new Socket(properties.get("game_host.ip"), port);
    }

    private HashMap<String, String> getProperties(String propertiesFileName) throws IOException {
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
