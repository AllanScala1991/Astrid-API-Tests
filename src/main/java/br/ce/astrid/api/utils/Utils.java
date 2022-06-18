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

    public String getBoardID() throws IOException {
        Object userID = getUserID(this.email, this.password);
        String token = getUserToken(this.email, this.password);

        OkHttpClient client = new OkHttpClient();

		RequestBody body = new FormBody.Builder()
        .add("name", "Automation Board")
        .add("userId", userID.toString())
        .build();
		
        Request createBoard = new Request.Builder()
        .url("http://localhost:8000/create/board")
        .addHeader("User-Agent", "OkHttp Automation")
        .addHeader("Authorization", "Bearer " + token)
        .post(body)
        .build();

        client.newCall(createBoard).execute();

        Request getBoards = new Request.Builder()
        .url("http://localhost:8000/find/board/" + userID.toString())
        .addHeader("User-Agent", "OkHttp Automation")
        .addHeader("Authorization", "Bearer " + token)
        .get()
        .build();

        Response boardResponse = client.newCall(getBoards).execute();

        String board = boardResponse.body().string();

        String boardId = board.split("\\,")[1]
        .split("id")[1]
        .split(":")[1]
        .replaceAll("^\"|\"$", "");
        return boardId;
    }

    public String getStageID() throws IOException {
        String boardID = this.getBoardID();
        String token = getUserToken(email, password);

        OkHttpClient client = new OkHttpClient();

		RequestBody stageBody = new FormBody.Builder()
        .add("name", "Automation Stage")
        .add("boardId", boardID)
        .build();
		
        Request createStage = new Request.Builder()
        .url("http://localhost:8000/create/stage")
        .addHeader("Authorization", "Bearer " + token)
        .addHeader("User-Agent", "OkHttp Automation")
        .post(stageBody)
        .build();

        client.newCall(createStage).execute();

        Request getStages = new Request.Builder()
        .url("http://localhost:8000/find/stage/" +  boardID)
        .addHeader("User-Agent", "OkHttp Automation")
        .addHeader("Authorization", "Bearer " + token)
        .get()
        .build();

        Response stages = client.newCall(getStages).execute();

        String stage = stages.body().string();
        String stageId = stage.split("\\,")[1]
        .split("id")[1]
        .split(":")[1]
        .replaceAll("^\"|\"$", "");
        return stageId;

    }
}
