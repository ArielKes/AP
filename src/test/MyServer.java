package test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MyServer {

    int port;
    ClientHandler ch;
    volatile boolean stop = false;
    int maxClients;
    ThreadPoolExecutor threadPool;

    public MyServer(int port, ClientHandler ch, int maxClients) {
        this.port = port;
        this.ch = ch;
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
                            RunServer.ClientHandler1 c = new RunServer.ClientHandler1();
                            c.handleClient(aClient.getInputStream(), aClient.getOutputStream());
                            aClient.close();
                            c.close();
                        } catch (IOException e) {
                            e.printStackTrace();
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
