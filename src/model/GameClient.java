package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class GameClient {
    protected final Socket hs;
    private final HashMap<String, String> properties;

    public GameClient() throws IOException {
        this.properties = getProperties("properties.txt");
        this.hs = getGameHostSocket();
    }

    Socket getGameHostSocket() throws IOException {
        assert properties != null;
        int port = Integer.parseInt(properties.get("GameHost_port"));
        return new Socket(properties.get("GameHost_ip"), port);
    }

    private HashMap<String, String> getProperties(String propertiesFileName) throws IOException {
        HashMap<String, String> p = new HashMap<>();
        BufferedReader in = new BufferedReader(new FileReader(propertiesFileName));
        String line;
        while ((line = in.readLine()) != null) {
            String[] sp = line.split(",");
            p.put(sp[0], sp[1]);
        }
        in.close();
        return p;
    }


}



