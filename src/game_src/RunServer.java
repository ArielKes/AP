package game_src;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class RunServer {
    public static class TestClientHandler implements ClientHandler {
        BufferedReader in;
        PrintWriter out;

        @Override
        public void handleClient(InputStream inFromClient, OutputStream outToClient) {
            try {
                in = new BufferedReader(new InputStreamReader(inFromClient));
                out = new PrintWriter(outToClient, true);
                String line;
                while (true) {
                    line = in.readLine();
                    if (line == null){
                        break;
                    }
                    System.out.println("Server got: " + line);
                    out.println(line);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void handleClient(InputStream inFromclient, OutputStream outToClient, Socket serverSocket) {

        }

        @Override
        public void handleClient(Socket clientSocket, Socket serverSocket) throws IOException {

        }

        @Override
        public void close() {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                System.out.println("Error closing client");
            }
        }
    }


    public static class ClientHandler1 implements ClientHandler {
        PrintWriter out;
        Scanner in;

        @Override
        public void handleClient(InputStream inFromclient, OutputStream outToClient) {
            out = new PrintWriter(outToClient);
            in = new Scanner(inFromclient);
            String text = in.next();
            out.println(new StringBuilder(text).reverse());
            out.flush();
        }

        @Override
        public void handleClient(InputStream inFromclient, OutputStream outToClient, Socket serverSocket) {

        }

        @Override
        public void handleClient(Socket clientSocket, Socket serverSocket) throws IOException {

        }

        @Override
        public void close() {
            in.close();
            out.close();
        }

    }


    public static void main(String[] args) {
        System.out.println("Server is starting");
        MyServer server = new MyServer(8080, new ClientHandler1(), 3);
        server.start();
        System.out.println("Server is running at port 8080");
        Scanner scanner = new Scanner(System.in);
        String command = scanner.next();
        while (!command.equals("stop")) {
            command = scanner.next();

        }
        scanner.close();
        server.close();
        System.out.println("Server is closed");
        }
}