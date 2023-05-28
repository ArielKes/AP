package model;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

public class utils {
    public static long getProcessId() throws IOException {
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);}
    public static HashMap<String, String> getProperties(String propertiesFileName) throws IOException {
        HashMap<String, String> p = new HashMap<>();
        BufferedReader in = new BufferedReader(new FileReader(propertiesFileName));
        String line;
        while ((line = in.readLine()) != null) {
            String[] sp = line.split("=");
            p.put(sp[0], sp[1]);
        }
        in.close();
        return p;
    }


    public static final String getRespondFromServer(Socket hs) {

        String respond = null;
        while (respond == null){
            try {
                hs.setSoTimeout(1000);
                BufferedReader in = new BufferedReader(new InputStreamReader(hs.getInputStream()));
                respond = in.readLine();
                if (respond != null) break;
            } catch (SocketTimeoutException e) {

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return respond;
    }

    public static final GameClient.Request getResponseFromServer1(Socket hs) throws IOException, ClassNotFoundException {
        String response = null;
        GameClient.Request request = null;

        ObjectInputStream in = new ObjectInputStream(hs.getInputStream());
        response = in.readUTF();
        if (response != null) {
            String command = in.readUTF();
            Serializable data = (Serializable) in.readObject();
            return new GameClient.Request(response, command, data);
        }
        return request;
    }

    public static final String getRespondFromServer(InputStream inFromclient) {

        String respond = null;
        while (respond == null){
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(inFromclient));
                respond = in.readLine();
                if (respond != null) break;
            } catch (SocketTimeoutException e) {

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return respond;
    }
}
