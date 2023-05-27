package game_src;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public interface ClientHandler {
	void handleClient(InputStream inFromclient, OutputStream outToClient) throws IOException;
	void handleClient(InputStream inFromclient, OutputStream outToClient, Socket serverSocket) throws IOException;
	void handleClient(Socket clientSocket, Socket serverSocket) throws IOException;
	void close();
}
