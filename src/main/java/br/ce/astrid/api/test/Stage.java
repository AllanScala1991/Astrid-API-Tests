package br.ce.astrid.api.test;

import static org.hamcrest.Matchers.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.ce.astrid.api.utils.Utils;
import static io.restassured.RestAssured.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Stage {
    private static String stageName;
    private static Object token;
    private static String boardId;
    private static String stageId;
    private static Object userID;
    
    @BeforeClass
    public static void setup() throws IOException {
        baseURI = new Utils().getBaseUrl();
        port = new Utils().getPort();
        stageName = "Automation Stage";
        token = Utils.getUserToken(new Utils().getEmail(), new Utils().getPassword());
        boardId = new Utils().getBoardID();
        userID = Utils.getUserID(new Utils().getEmail(), new Utils().getPassword());
    }

    @Test
    public void t01_createNewStage() throws IOException {        
        Map<String, String> stage = new HashMap<String,String>();
        stage.put("name", stageName);
        stage.put("boardId", boardId);

        given()
            .contentType("application/json")
            .header("Authorization", "Bearer "+ token)
            .body(stage)
        .when()
            .post("/create/stage")
        .then()
            .body("status", is(true))
            .body("message", is("Quadro criado com sucesso."))
        ; 
    }
    
    @Test
    public void t02_notCreateNewStageWithInvalidBoardID() {        
        Map<String, String> stage = new HashMap<String,String>();
        stage.put("name", stageName);
        stage.put("boardId", "invalid");

        given()
            .contentType("application/json")
            .header("Authorization", "Bearer "+ token)
            .body(stage)
        .when()
            .post("/create/stage")
        .then()
            .body("status", is(false))
            .body("message.meta.field_name", is("Stages_boardId_fkey (index)"))
        ; 
    }

    @Test
    public void t03_notCreateNewStageWithInvalidToken() {        
        given()
            .contentType("application/json")
            .header("Authorization", "Bearer invalid")
            .body("")
        .when()
            .post("/create/stage")
        .then()
            .body("status", is(false))
            .body("message", is("Sua sessão expirou, faça login novamente."))
        ; 
    }

    @Test
    public void t04_getAllStagesByBoardID() {
        stageId = given()
            .header("Authorization", "Bearer "+ token)
        .when()
            .get("/find/stage/" + boardId)
        .then()
            .body("status", is(true))
            .body("data.size()", greaterThan(0))
            .body("data[0].id", is(notNullValue()))
            .body("data[0].name", is(stageName))
            .body("data[0].boardId", is(boardId))
            .body("data[0].createdAt", is(notNullValue()))
            .extract().path("data[0].id")
        ; 
    }

    @Test
    public void t05_notGetStageWithInvalidBoardID() {
        given()
            .header("Authorization", "Bearer "+ token)
        .when()
            .get("/find/stage/invalid")
        .then()
            .body("data.size", hasSize(0))
        ; 
    }

    @Test
    public void t06_notGetStageWithInvalidToken() {
        given()
            .header("Authorization", "Bearer invalid")
        .when()
            .get("/find/stage/" + boardId)
        .then()
            .body("status", is(false))
            .body("message", is("Sua sessão expirou, faça login novamente."))
        ; 
    }

    @Test
    public void t07_updateStage() {
        Map<String, String> stage = new HashMap<String,String>();
        stage.put("name", "Stage Updated");
        stage.put("stageId", stageId);

        given()
            .contentType("application/json")
            .header("Authorization", "Bearer "+ token)
            .body(stage)
        .when()
            .put("/update/stage")
        .then()
            .body("status", is(true))
            .body("message", is("Quadro atualizado com sucesso."))
        ; 
    }

    @Test
    public void t08_notUpdateStageWithInvalidStageID() {
        Map<String, String> stage = new HashMap<String,String>();
        stage.put("name", "Stage Updated");
        stage.put("stageId", "invalid");

        given()
            .contentType("application/json")
            .header("Authorization", "Bearer "+ token)
            .body(stage)
        .when()
            .put("/update/stage")
        .then()
            .body("status", is(false))
            .body("message.meta.cause", is("Record to update not found."))
        ; 
    }

    @Test
    public void t09_notUpdateStageWithInvalidToken() {
        given()
            .contentType("application/json")
            .header("Authorization", "Bearer invalid")
            .body("")
        .when()
            .put("/update/stage")
        .then()
            .body("status", is(false))
            .body("message", is("Sua sessão expirou, faça login novamente."))
        ; 
    }

    @Test
    public void t10_notDeleteStageWithInvalidID() {
        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .delete("/delete/stage/invalid")
        .then()
            .body("status", is(false))
            .body("message.meta.cause", is("Record to delete does not exist."))
        ; 
    }

    @Test
    public void t11_notDeleteStageWithInvalidToken() {
        given()
            .header("Authorization", "Bearer invalid")
        .when()
            .delete("/delete/stage/" + stageId)
        .then()
            .body("status", is(false))
            .body("message", is("Sua sessão expirou, faça login novamente."))
        ; 
    }

    @Test
    public void t12_deleteStage() {
        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .delete("/delete/stage/" + stageId)
        .then()
            .body("status", is(true))
            .body("message", is("Quadro deletado com sucesso."))
        ; 
    }

    @Test
    public void t13_deleteStageWithBoardID() {
        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .delete("/delete/stage/board/" + boardId)
        .then()
            .body("status", is(true))
            .body("message", is("Quadro deletado com sucesso."))
        ; 
    }

    @AfterClass
    public static void clean() {
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
