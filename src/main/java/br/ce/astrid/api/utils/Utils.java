package br.ce.astrid.api.utils;

import okhttp3.*;
import java.io.IOException;
import java.util.Base64;

import org.json.JSONObject;

public class Utils {
    private String email = "automation@mail.com";
    private String password = "123456";
    private String baseUrl = "http://localhost";
    private Integer port = 8000;

    public static String login(String email, String password) throws IOException{
        OkHttpClient client = new OkHttpClient();

		RequestBody body = new FormBody.Builder()
        .add("email", email)
        .add("password", password)
        .build();
		
        Request request = new Request.Builder()
        .url("http://localhost:8000/login")
        .addHeader("User-Agent", "OkHttp Automation")
        .post(body)
        .build();

        Response response = client.newCall(request).execute();

        String token = response.body().string();

        return token;
	}

    public static Object getUserID(String email, String password) throws IOException {
        String token = Utils.login(email, password);
        String[] jwt = token.split("\\.");
        String decode = new String(Base64.getUrlDecoder().decode(jwt[1]));
		JSONObject payload = new JSONObject(decode);
		return payload.get("id");
    }

    public static String getUserToken(String email, String password) throws IOException {
        String jwt = Utils.login(email, password);
        String[] splitToken = jwt.split("token");
        String[] splitPunctuation = splitToken[1].split(":");
        String[] splitKeys = splitPunctuation[1].split("}");
        String token = splitKeys[0].replaceAll("^\"|\"$", "");// Remove todas as "" do token
        return token;
    } 

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public Integer getPort() {
        return this.port;
    }
}
