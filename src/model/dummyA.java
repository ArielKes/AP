package model;

import game_src.BookScrabbleHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class dummyA {

    public static void main(String[] args) throws IOException {

        game_src.MyServer s=new game_src.MyServer(getBookPort("src/resources/properties.txt"), new BookScrabbleHandler(),1);
        s.start();
        GameHost g = new GameHost("src/resources/properties.txt");
        g.start();
        GameClient c = new GameClient();
        c.runClient();

        System.out.println("done");
    }

    public static int getBookPort(String propertiesFileName) throws IOException {
        HashMap<String, String> p = new HashMap<>();
        BufferedReader in = new BufferedReader(new FileReader(propertiesFileName));
        String line;
        while ((line = in.readLine()) != null) {
            String[] sp = line.split("=");
            p.put(sp[0], sp[1]);
        }
        in.close();
        return Integer.parseInt(p.get("game_server.port"));
    }
}
