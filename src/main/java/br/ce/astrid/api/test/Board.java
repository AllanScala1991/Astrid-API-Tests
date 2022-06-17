package br.ce.astrid.api.test;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import br.ce.astrid.api.utils.Utils;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Board {

    private static Object userID;
    private static Object token;
    
    @BeforeClass
    public static void setup () throws IOException {
        baseURI = new Utils().getBaseUrl();
		port = new Utils().getPort();
        userID = Utils.getUserID(new Utils().getEmail(), new Utils().getPassword());
        token = Utils.getUserToken(new Utils().getEmail(), new Utils().getPassword());
    }

    @Test
    public void t01_createBoard() {     
       Map<String, String> board = new HashMap<String,String>();
       board.put("name", "Test Board");
       board.put("userId", userID.toString());
        given()
            .contentType("application/json")
            .body(board)
            .header("Authorization", "Bearer "+ token)
        .when()
            .post("/create/board")
        .then()
            .body("status", is(true))
            .body("message", is("Board criado com sucesso."))
        ;
    }

    @Test
    public void t02_sendInvalidToken() {     
       Map<String, String> board = new HashMap<String,String>();
       board.put("name", "Test Board");
       board.put("userId", userID.toString());
        given()
            .contentType("application/json")
            .body(board)
            .header("Authorization", "Bearer invalidToken")
        .when()
            .post("/create/board")
        .then()
            .body("status", is(false))
            .body("message", is("Sua sessão expirou, faça login novamente."))
        ;
    }

    @Test
    public void t03_sendInvalidBody() {     
       Map<String, String> board = new HashMap<String,String>();
       board.put("name", "Test Board");
        given()
            .contentType("application/json")
            .body(board)
            .header("Authorization", "Bearer "+ token)
        .when()
            .post("/create/board")
        .then()
            .body("status", is(false))
            .body("message", is("Todos os campos devem ser preenchidos."))
        ;
    }

    @Test
    public void t04_findBoardByUserID() {     
        given()
            .header("Authorization", "Bearer "+ token)
        .when()
            .get("/find/board/"+userID)
        .then()
            .body("status", is(true))
            .body("data.id", is(notNullValue()))
            .body("data.name", hasItem("Test Board"))
            .body("data.createdAt", is(notNullValue()))
            .body("data.isFavorited", hasItem(false))
            .body("data.isArchived", hasItem(false))
            .body("data.userId", is(notNullValue()))
        ;
    }

    @Test
    public void t05_findWithInvalidUserID() {     
        given()
            .header("Authorization", "Bearer "+ token)
        .when()
            .get("/find/board/invalidID")
        .then()
            .body("data", is(hasSize(0)))
        ;
    }

    @Test
    public void t06_findWithInvalidToken() {     
        given()
            .header("Authorization", "Bearer invalid")
        .when()
            .get("/find/board/"+userID)
        .then()
            .body("status", is(false))
            .body("message", is("Sua sessão expirou, faça login novamente."))
        ;
    }

    @Test
    public void t07_findBoardByUsernameAndID() {     
        given()
            .header("Authorization", "Bearer "+ token)
        .when()
            .get("/find/board/name/Test Board/"+userID)
        .then()
            .body("status", is(true))
            .body("data.id", is(notNullValue()))
            .body("data.name", hasItem("Test Board"))
            .body("data.createdAt", is(notNullValue()))
            .body("data.isFavorited", hasItem(false))
            .body("data.isArchived", hasItem(false))
            .body("data.userId", is(notNullValue()))
        ;
    }

    @Test
    public void t08_notFindBoardWithInvalidUsername() {     
        given()
            .header("Authorization", "Bearer "+ token)
        .when()
            .get("/find/board/name/Invalid/"+userID)
        .then()
            .body("data", is(hasSize(0)))
        ;
    }

    @Test
    public void t09_notFindBoardWithInvalidID() {     
        given()
            .header("Authorization", "Bearer "+ token)
        .when()
            .get("/find/board/name/Test Board/invalid")
        .then()
            .body("data", is(hasSize(0)))
        ;
    }

    @Test
    public void t10_notFindBoardWithInvalidToken() {     
        given()
            .header("Authorization", "Bearer invalid")
        .when()
            .get("/find/board/name/Test Board/"+ userID)
        .then()
            .body("status", is(false))
            .body("message", is("Sua sessão expirou, faça login novamente."))
        ;
    }

    @Test
    public void t11_updateBoard() {    
        
        ArrayList<String> boardId = 
        given()
            .header("Authorization", "Bearer "+ token)
        .when()
            .get("/find/board/"+userID)
        .then()
            .body("status", is(true))
            .body("data.id", is(notNullValue()))
            .extract().path("data.id");

        Map<String, String> board = new HashMap<String,String>();
        board.put("name", "Test Board Updated");
        board.put("boardId", boardId.get(0).toString());
            given()
                .contentType("application/json")
                .body(board)
                .header("Authorization", "Bearer "+ token)
            .when()
                .put("/update/board")
            .then()
                .body("status", is(true))
                .body("message", is("Board atualizado com sucesso."))
            ;
    }

    @Test
    public void t12_notUpdateBoardWithInvalidBody() {    
        Map<String, String> board = new HashMap<String,String>();
        board.put("name", "Test Board Updated");
        board.put("boardId", "invalid");
            given()
                .contentType("application/json")
                .body(board)
                .header("Authorization", "Bearer "+ token)
            .when()
                .put("/update/board")
            .then()
                .body("status", is(false))
                .body("message.meta.cause", is("Record to update not found."))
            ;
    }

    @Test
    public void t13_notUpdateBoardWithInvalidToken() {    
        Map<String, String> board = new HashMap<String,String>();
        board.put("name", "Test Board Updated");
        board.put("boardId", "invalid");
            given()
                .contentType("application/json")
                .body(board)
                .header("Authorization", "Bearer invalid")
            .when()
                .put("/update/board")
            .then()
                .body("status", is(false))
                .body("message", is("Sua sessão expirou, faça login novamente."))
            ;
    }

    @Test
    public void t14_notDeleteBoardWithInvalidID() {    
        given()
            .header("Authorization", "Bearer "+ token)
        .when()
            .delete("/delete/board/invalid")
        .then()
            .body("status", is(false))
            .body("message.meta.cause", is("Record to delete does not exist."))
        ;
    }

    @Test
    public void t15_notDeleteBoardWithInvalidToken() {    
        given()
            .header("Authorization", "Bearer invalid")
        .when()
            .delete("/delete/board/invalid")
        .then()
            .body("status", is(false))
            .body("message", is("Sua sessão expirou, faça login novamente."))
        ;
    }

    @Test
    public void t16_deleteBoard() {    
        ArrayList<String> boardId = 
        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/find/board/"+userID)
        .then()
            .body("status", is(true))
            .body("data.id", is(notNullValue()))
            .extract().path("data.id");

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .delete("/delete/board/"+ boardId.get(0).toString())
        .then()
            .body("status", is(true))
            .body("message", is("Board deletado com sucesso."))
        ;
    }
}
