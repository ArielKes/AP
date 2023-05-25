package model;

import game_src.BookScrabbleHandler;
import model.utilObjects.PlaceWord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class dummyA {

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("dummyA pid: "+utils.getProcessId());
        game_src.MyServer s=new game_src.MyServer(getBookPort("src/resources/properties.txt"), new BookScrabbleHandler(),1);
        s.start();
        sleep(1000);
        GameHost g = new GameHost("src/resources/properties.txt");
        g.start();
        sleep(1000);
//        GameClient c = new GameClient();
//        c.runClient(1);
        // create four threads for four clients
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    GameClient c = new GameClient();
                    c.placeWordOnBoard("hello");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();

        }
        sleep(2000);
        g.startGame();
        while(true){
                   }
        //System.out.println("done");
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
