package test;

import game_src.BookScrabbleHandler;
import game_src.Word;
import model.GameClient;
import model.GameClientHandler;
import model.GameHost;
import model.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class GameClientHandlerTest {

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
                c1[0] = new GameClient(Integer.toString(0));
                c1[0].endTurn();

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
        if(c1[0]!=null) {
            c1[0].getScoreTable();
            c1[0].endTurn();
        }
        c0[0].getClientTiles();
        c0[0].endTurn();
        c0[0].checkWord("hey");
        c0[0].endTurn();
        c0[0].getScoreTable();
        c0[0].endTurn();
        c0[0].checkWord("no");
        c0[0].endTurn();
        c0[0].isMyTurn();
        c0[0].endTurn();
        c0[0].getScoreTable();
        c0[0].endTurn();
        c0[0].getBoard();
        c0[0].endTurn();
        c0[0].placeWord(new Word(null,1,2,true));
        c0[0].endTurn();
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
