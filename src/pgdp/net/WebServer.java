package pgdp.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;

public class WebServer {
    PinguDatabase database;
    HtmlGenerator htmlGenerator;
    //ExecutorService executorService;
    ServerSocket serverSocket;
    static int port = 80;
    public WebServer(PinguDatabase database, HtmlGenerator htmlGenerator, ServerSocket serverSocket){
        this.database = database;
        //this.executorService = executorService;
        this.serverSocket = serverSocket;
    }

    public static void main(String[] args) {
        try {
            WebServer webServer = new WebServer(new PinguDatabase(), new HtmlGenerator(), new ServerSocket());
            webServer.serverSocket.accept();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // TODO
}
