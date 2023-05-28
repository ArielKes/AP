package test;

import game_src.BookScrabbleHandler;
import game_src.Word;
import model.GameClient;
import model.GameHost;
import model.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class GameClientTest {

    public static void main(String[] args) throws IOException, InterruptedException {

        game_src.MyServer s=new game_src.MyServer(getBookPort("src/resources/properties.txt"), new BookScrabbleHandler(),1);
        s.start();
        sleep(1000);
        GameHost g = new GameHost("src/resources/properties.txt");
        g.start();
        sleep(1000);

        final GameClient[] c0 = {null};
        new Thread(() -> {
            try {
                c0[0] = new GameClient(Integer.toString(0));
                c0[0].endTurn();

                sleep(2000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        final GameClient[] c1 = {null};
        new Thread(() -> {
            try {
                c1[0] = new GameClient(Integer.toString(1));
                c1[0].endTurn();

                sleep(2000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        final GameClient[] c2 = {null};
        new Thread(() -> {
            try {
                c2[0] = new GameClient(Integer.toString(2));
                c2[0].endTurn();

                sleep(2000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        final GameClient[] c3 = {null};
        new Thread(() -> {
            try {
                c3[0] = new GameClient(Integer.toString(3));
                c3[0].endTurn();

                sleep(2000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();


        sleep(2000);
        g.startGame();
        sleep(2000);

        c1[0].getBoard();
        sleep(2000);
        c1[0].endTurn();
        c2[0].getTiles(3);
        c2[0].endTurn();
        c0[0].checkWord(new Word(null, 3, 2, true));
        c0[0].endTurn();
        c3[0].getScoreTable();
        c3[0].endTurn();

        while(true){

        }
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
