package br.ce.astrid.api.utils;

import okhttp3.*;
import java.io.IOException;
import java.util.Base64;

import org.json.JSONObject;

public class Utils {
    public static String[] login(String email, String password) throws IOException{
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

        String[] jwt = token.split("\\.");

        return jwt;
	}

    public static Object getUserID(String email, String password) throws IOException {
        String[] jwt = Utils.login(email, password);
        String decode = new String(Base64.getUrlDecoder().decode(jwt[1]));
		JSONObject payload = new JSONObject(decode);
		return payload.get("id");
    }
}
