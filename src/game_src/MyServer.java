package game_src;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MyServer {

    int port;
    ClientHandler chInstance;
    volatile boolean stop = false;
    int maxClients;
    ThreadPoolExecutor threadPool;

    public MyServer(int port, ClientHandler ch, int maxClients) {
        this.port = port;
        this.chInstance = ch;
        this.maxClients = maxClients;
        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxClients);
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
                            //todo : remove this comment and delete this line after testing
//                            // close the client handler
//                            aClient.close();
//                            ch.close();
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
}
