package test;

import game_src.BookScrabbleHandler;
import model.GameClient;
import model.GameHost;
import model.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class GameHostTest {

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("GameHostTest pid: "+utils.getProcessId());
        game_src.MyServer s=new game_src.MyServer(getBookPort("src/resources/properties.txt"), new BookScrabbleHandler(),1);
        s.start();
        sleep(1000);

        GameHost g = new GameHost("src/resources/properties.txt");
        g.start();
        sleep(1000);

        g.startGame();

        g.close();
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
