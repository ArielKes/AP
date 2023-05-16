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
the model is also a server that accepts clients and handles them, and also a client that
communicates with the book server.
*/
public class Model extends Observable{

    HashMap<String, String> properties;
    Socket fg;
    PrintWriter out2fg;
    boolean stop;

    public Model(String propertiesFileName){
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
    }

    public void start() {
        stop = false;
        new Thread(this::startServer).start();
    }

    private void startServer(){
        try {
            ServerSocket server = new ServerSocket(port);
            server.setSoTimeout(1000);
            while(!stop){
                try {
                    Socket aClient = server.accept();
                    threadPool.execute(() -> {
                        try {
                            // create a new instance of the client handler
                            Class<? extends ClientHandler> chClass = this.chInstance.getClass();
                            ClientHandler ch = chClass.getDeclaredConstructor().newInstance();
                            // handle the client
                            ch.handleClient(aClient.getInputStream(), aClient.getOutputStream());
                            // close the client handler
                            aClient.close();
                            ch.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException | InstantiationException |
                                 IllegalAccessException | NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (SocketTimeoutException ignored) {}
            }
            server.close();
            threadPool.shutdown();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        stop = true;
    }

    public void finalize(){
        out2fg.close();
        fg.close();
    }
}