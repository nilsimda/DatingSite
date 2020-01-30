package pgdp.net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class HttpRequest {
    String request;
    boolean hasParameters;

    public HttpRequest(String request) {
        if(!request.matches("GET|POST\\s\\S+")){
            throw new IllegalArgumentException("This request does not match the expected form.");
        }
        this.request = request;
    }

    public HttpMethod getMethod() {
        String method = request.substring(0, request.indexOf(" "));
        if (method.equals("GET"))
            return HttpMethod.GET;
        else //(method.equals("POST"))
            return HttpMethod.POST;
    }

    public String getPath() {
        int index = request.indexOf("?");
        if (index != -1)
            return request.substring(request.indexOf("/"), index);
        else
            return request.substring(request.indexOf("/"), request.lastIndexOf(" "));
    }

    public Map<String, String> getParameters() {
        Map<String, String> result = new HashMap<>();
        String substringOfParameter = request.substring(request.indexOf("?")+1, request.lastIndexOf(" "));
        if (substringOfParameter.contains("&")) {
            String[] parameters = substringOfParameter.split("&");
            for (String s : parameters) {
                String[] bothParts = s.split("=");
                result.put(bothParts[0], bothParts[1]);
            }
        } else {
            String[] parameters = substringOfParameter.split("=");
            result.put(parameters[0], parameters[1]);
        }
        return result;
    }

    public static void main(String[] args) {
        String test = "GET /find?sexualOrientation=any&minAge=19&maxAge=45&hobbies=swimming HTTP/1.1";
        HttpRequest request = new HttpRequest(test);
        System.out.println(request.getMethod());
        System.out.println(request.getPath());
        Map<String, String> map = request.getParameters();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println("parameter name: " + entry.getKey() + " value: " + entry.getValue());
        }
    }
}

