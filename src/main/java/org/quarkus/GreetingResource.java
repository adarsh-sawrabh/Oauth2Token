package org.quarkus;

import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/auth")
@ApplicationScoped
public class GreetingResource {

    final String url = "https://sso.lab.authconnected.com";
    final String client_id = "75bad09a";
    final String client_secret = "17003919c34d9ad829f1c8607f66d39c";
    final String userName= "auth-admin";
    final String password = "werYoV945Hp@3l";

    public String getToken() throws IOException{
        String credentials = Base64.getEncoder().encodeToString((client_id+":"+client_secret).getBytes());

        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type","password");
        parameters.put("username",userName);
        parameters.put("password",password);

        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Authorization","Basic "+ credentials);

        try(DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())){
            outputStream.writeBytes(getParamsString(parameters));
        }
        try(BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(connection.getInputStream()))){
            String response = bufferedReader.lines().collect(Collectors.joining());
            JsonObject jsonObject = new JsonObject(response);
            return jsonObject.getString("access_token");
        }
    }

    private String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
    }
}
