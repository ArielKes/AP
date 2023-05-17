package model;

import java.io.BufferReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/*
this part is in charge of communicating with the book server and acting as a proxy to the other clients.
*/
public class ModelCommunicator extends Observable{

    HashMap<String, String> properties;
    Socket fg;
    PrintWriter out2fg;



    public ModelCommunicator(String propertiesFileName) {
        properties = new HashMap<>();
        BufferReader in = new BufferReader(new FileReader(propertiesFileName));
        String line;
        while(line = in.readLine()!=null){
            String sp[] = line.split(",");
            properties.put(sp[0],sp[1]);
        }
        in.close();

        int port = Integer.parseInt(properties.get("port"));
        fg = new Socket(properties.get("ip"),port);
        out2fg = new PrintWriter(fg.getOutputStream());

        new Thread(
                () -> {
                    try {
                        Socket server = new Socket(properties.get("ip"), port);
                        while(!stop){


                        }
                        server.close();
                    } catch (Exception e) {
                        System.out.println("Exception was thrown when running the communicator");
                        e.printStackTrace();
                    }
                }).start();
    }

    public void close(){
        stop = true;
    }
}