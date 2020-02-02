package pgdp.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class WebServer {
    PinguDatabase database;
    HtmlGenerator htmlGenerator;
    ServerSocket serverSocket;
    static int port = 80;

    public WebServer(PinguDatabase database) {
        try {

            this.database = Objects.requireNonNull(database);
            this.serverSocket = new ServerSocket(port);
            this.htmlGenerator = new HtmlGenerator();
        } catch (IOException e) {
            System.out.println("Could not start Serversocket.");
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        WebServer webServer = new WebServer(new PinguDatabase());
        Runnable runnable1 = () -> {
            try {

                while (!webServer.serverSocket.isClosed()) {
                    Socket socket = webServer.serverSocket.accept();
                    Runnable runnable2 = () -> {
                        webServer.communication(socket);
                    };
                    executorService.submit(runnable2);
                }
            } catch (IOException e) {
                System.out.println("Webserver shutdown.");
            }
        };
        executorService.submit(runnable1);
        Scanner scanner = new Scanner(System.in);
        while (!webServer.serverSocket.isClosed()) {
            String command = scanner.nextLine();
            if (command.startsWith("add")) {
                try {
                    DatingPingu pingu = DatingPingu.parse(command.substring(4));
                    if (webServer.database.add(pingu)) {
                        System.out.println("Penguin succesfully added.");
                    } else
                        System.out.println("Penguin was already registered, try again.");
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
                    System.out.println("Format following add-command was incorrect, try again.");
                }
            } else if (command.equals("shutdown")) {
                try {
                    webServer.serverSocket.close();
                    executorService.shutdown();
                } catch (IOException e) {
                    System.out.println("Error while closing the ServerSocket.");
                    return;
                }
            } else
                System.out.println("Unknown command.");
        }
    }

    public void communication(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            try {
                HttpRequest request = new HttpRequest(reader.readLine());
                if (request.getMethod().equals(HttpMethod.POST))
                    writer.println(new HttpResponse(HttpStatus.METHOD_NOT_ALLOWED, ""));
                else if (request.getPath().equals("/"))
                    writer.println(new HttpResponse(HttpStatus.OK, htmlGenerator.generateStartPage()));
                else if (request.getPath().equals("/find")) {
                    Map<String, String> parameters = request.getParameters();
                    Set<String> hobbies = null;
                    int maxAge = -1;
                    int minAge = -1;
                    String sexualOri = null;

                    for (Map.Entry<String, String> entry : parameters.entrySet()) {
                        switch (entry.getKey()) {
                            case "sexualOrientation":
                                sexualOri = entry.getValue();
                                break;
                            case "minAge":
                                minAge = Integer.parseInt(entry.getValue());
                                break;
                            case "maxAge":
                                maxAge = Integer.parseInt(entry.getValue());
                                break;
                            case "hobbies":
                                if (!entry.getValue().equals("")) {
                                    hobbies = new HashSet<>();
                                    String[] hobbiesArr = entry.getValue().split("\\+");
                                    Collections.addAll(hobbies, hobbiesArr);
                                    break;
                                } else {
                                    hobbies = new HashSet<>();
                                    break;
                                }
                            default:
                                writer.println(new HttpResponse(HttpStatus.BAD_REQUEST, ""));
                                socket.close();
                                return;
                        }
                    }
                    if (maxAge < 0 || minAge < 0 || sexualOri == null || hobbies == null) {
                        writer.println(new HttpResponse(HttpStatus.BAD_REQUEST, ""));
                        socket.close();
                        return;
                    }
                    SeachRequest seachRequest = new SeachRequest(sexualOri, minAge, maxAge, hobbies);
                    writer.println(new HttpResponse(HttpStatus.OK, htmlGenerator.generateFindPage(seachRequest, database.findMatchesFor(seachRequest))));
                } else if (request.getPath().matches("/user/\\d+")) {
                    String path = request.getPath();
                    long id = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
                    Optional<DatingPingu> pingu = database.lookupById(id);
                    if (pingu.isPresent())
                        writer.println(new HttpResponse(HttpStatus.OK, htmlGenerator.generateProfilePage(pingu.get())));
                    else
                        writer.println(new HttpResponse(HttpStatus.NOT_FOUND, ""));
                } else
                    writer.println(new HttpResponse(HttpStatus.NOT_FOUND, ""));
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                writer.println(new HttpResponse(HttpStatus.BAD_REQUEST, ""));
            } catch (IllegalArgumentException e) {
                writer.println(new HttpResponse(HttpStatus.NOT_FOUND, ""));
            }
            socket.close();

        } catch (IOException e) {
            System.out.println("Could not read Socket InputStream.");
        }
    }
}
