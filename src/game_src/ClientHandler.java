package game_src;

import java.io.InputStream;
import java.io.OutputStream;

public interface ClientHandler {
	void handle(InputStream inFromclient, OutputStream outToClient);
	void close();
}
