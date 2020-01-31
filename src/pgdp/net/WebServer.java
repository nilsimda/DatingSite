package pgdp.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class WebServer{
    PinguDatabase database;
    HtmlGenerator htmlGenerator;
    ServerSocket serverSocket;
    static int port = 80;
    public WebServer(PinguDatabase database) {
        try {

            this.database = database;
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
                } catch (IOException e){
                    e.printStackTrace();
                }
            };
            executorService.submit(runnable1);
            Scanner scanner = new Scanner(System.in);
            String addPenguin = scanner.nextLine();
            if(addPenguin.startsWith("add")){
                String parameter = addPenguin.substring(4);
                String [] stats = parameter.split(",");
                long id = Long.parseLong(stats[0]);
                String name = stats[1];
                String sexualOri = stats[2];
                int age = Integer.parseInt(stats[3]);
                String [] hobbyArr = stats[4].split(" ");
                Set<String> hobbies = new HashSet(Arrays.asList(hobbyArr));
                String aboutMe = stats[5];
                DatingPingu pingu = new DatingPingu(id, name, sexualOri, age, hobbies, aboutMe);
                if(webServer.database.add(pingu)){
                    System.out.println("Penguin succesfully added.");
                }
                else
                    System.out.println("Penguin was already registered, try again.");
            }
            else
                System.out.println("Unknown command.");
    }
    public void communication(Socket socket){
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            try {
                HttpRequest request = new HttpRequest(reader.readLine());
                if(request.getMethod().equals(HttpMethod.POST))
                    writer.println(new HttpResponse(HttpStatus.METHOD_NOT_ALLOWED, ""));
                else if(request.getPath().equals("/"))
                    writer.println(new HttpResponse(HttpStatus.OK, htmlGenerator.generateStartPage()));
                else if(request.getPath().equals("/find")) {
                    Map<String, String> parameters = request.getParameters();
                    Set<String> hobbies = new HashSet<>();
                    int maxAge = -1;
                    int minAge = -1;
                    String sexualOri = null;

                    for(Map.Entry<String, String> entry : parameters.entrySet()){
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
                                String[] hobbiesArr = entry.getValue().split("\\+");
                                Collections.addAll(hobbies, hobbiesArr);
                                break;
                            default:
                                writer.println(new HttpResponse(HttpStatus.BAD_REQUEST, ""));
                        }
                    }
                    if(maxAge< 0 || minAge < 0 || sexualOri == null || hobbies.isEmpty()){
                        writer.println(new HttpResponse(HttpStatus.BAD_REQUEST, ""));
                    }
                    SeachRequest seachRequest = new SeachRequest(sexualOri, minAge, maxAge, hobbies);
                    writer.println(new HttpResponse(HttpStatus.OK, htmlGenerator.generateFindPage(seachRequest, database.findMatchesFor(seachRequest))));
                }
                else if(request.getPath().matches("/user/\\d+")){
                    String path = request.getPath();
                    long id = Integer.parseInt(path.substring(path.lastIndexOf("/")+1));
                    Optional<DatingPingu> pingu = database.lookupById(id);
                    if(pingu.isPresent())
                        writer.println(new HttpResponse(HttpStatus.OK, htmlGenerator.generateProfilePage(pingu.get())));
                    else
                        writer.println(new HttpResponse(HttpStatus.NOT_FOUND, ""));
                }
            } catch (NumberFormatException e) {
                writer.println(new HttpResponse(HttpStatus.BAD_REQUEST, ""));
            } catch (IllegalArgumentException e){
                writer.println(new HttpResponse(HttpStatus.NOT_FOUND, ""));
            }
            socket.close();

        } catch (IOException e){
            System.out.println("Could not read Socket InputStream.");
        }
    }
}
