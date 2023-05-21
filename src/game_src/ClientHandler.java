package game_src;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public interface ClientHandler {
	void handleClient(InputStream inFromclient, OutputStream outToClient);
	void handleClient(InputStream inFromclient, OutputStream outToClient, Socket serverSocket);
	void close();
}
