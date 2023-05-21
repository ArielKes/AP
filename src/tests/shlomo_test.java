package tests;

import game_src.BookScrabbleHandler;
import game_src.MyServer;
import static model.GameHost.getProperties;
import java.io.IOException;
import java.util.HashMap;

;

public class shlomo_test {





    public static void main(String[] args) throws IOException {
        System.out.println("Server is starting");
        HashMap<String, String> properties  = getProperties("src/resources/properties.txt");
        int port = Integer.parseInt(properties.get("game_server.port"));
        MyServer server = new MyServer(port, new BookScrabbleHandler(), 3);
        server.start();

    }
}
