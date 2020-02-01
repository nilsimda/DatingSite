package pgdp.net;

import java.util.HashMap;
import java.util.Map;

public final class HttpRequest {
    String request;
    boolean hasParameters = false;

    public HttpRequest(String request) {
        if (!request.matches("(GET|POST) /(\\S+)? HTTP/1.1")) {
            throw new IllegalArgumentException("This request does not match the expected form.");
        }
        if (request.contains("?"))
            hasParameters = true;
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
        if (!hasParameters) {
            return result;
        }
        String substringOfParameter = request.substring(request.indexOf("?") + 1, request.lastIndexOf(" "));
        if (substringOfParameter.contains("&")) {
            String[] parameters = substringOfParameter.split("&");
            for (String s : parameters) {
                String[] bothParts = s.split("=");
                if (bothParts.length == 2)
                    result.put(bothParts[0], bothParts[1]);
                else
                    result.put(bothParts[0], "");
            }
        } else {
            String[] parameters = substringOfParameter.split("=");
            result.put(parameters[0], parameters[1]);
        }
        return result;
    }

    public static void main(String[] args) {
        String test1 = "/user/100 HTTP/1.1";
        System.out.println(test1.matches("/user/\\d+ HTTP/1.1"));
        String test = "GET /find?sexualOrientation=any&minAge=19&maxAge=45&hobbies= HTTP/1.1";
        HttpRequest request = new HttpRequest(test);
        System.out.println(request.getMethod());
        System.out.println(request.getPath());
        Map<String, String> map = request.getParameters();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println("parameter name: " + entry.getKey() + " value: " + entry.getValue());
        }
    }
}

