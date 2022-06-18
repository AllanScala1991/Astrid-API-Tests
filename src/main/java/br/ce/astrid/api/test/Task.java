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
public class Task {
    private static Object token;
    private static String stageId;
    private static String taskName;
    private static String taskId;
    private static Object userID;
    
    @BeforeClass
    public static void setup() throws IOException {
        baseURI = new Utils().getBaseUrl();
        port = new Utils().getPort();
        token = Utils.getUserToken(new Utils().getEmail(), new Utils().getPassword());
        stageId = new Utils().getStageID();
        taskName = "Automation Task";
        userID = Utils.getUserID(new Utils().getEmail(), new Utils().getPassword());
    }

    @Test
    public void t01_createNewTask() {
        Map<String, String> task = new HashMap<String,String>();
        task.put("name", taskName);
        task.put("stageId", stageId);
        task.put("description", "Any description");
        task.put("urgency", "low");

        given()
            .contentType("application/json")
            .header("Authorization", "Bearer "+ token)
            .body(task)
        .when()
            .post("/create/task")
        .then()
            .body("status", is(true))
            .body("message", is("Task criada com sucesso."))
        ;
    }

    @Test
    public void t02_notCreateTaskWithInvalidStageID() {
        Map<String, String> task = new HashMap<String,String>();
        task.put("name", taskName);
        task.put("stageId", "invalid");
        task.put("description", "Any description");
        task.put("urgency", "low");

        given()
            .contentType("application/json")
            .header("Authorization", "Bearer "+ token)
            .body(task)
        .when()
            .post("/create/task")
        .then()
            .body("status", is(false))
            .body("message.meta.field_name", is("Tasks_stageId_fkey (index)"))
        ;
    }

    @Test
    public void t03_notCreateTaskWithInvalidToken() {
        Map<String, String> task = new HashMap<String,String>();
        task.put("name", taskName);
        task.put("stageId", stageId);
        task.put("description", "Any description");
        task.put("urgency", "low");

        given()
            .contentType("application/json")
            .header("Authorization", "Bearer invalid")
            .body(task)
        .when()
            .post("/create/task")
        .then()
            .body("status", is(false))
            .body("message", is("Sua sessão expirou, faça login novamente."))
        ;
    }

    @Test
    public void t04_findTaskByStageID() {
        taskId = given()
            .header("Authorization", "Bearer "+ token)
        .when()
            .get("/find/task/stage/" + stageId)
        .then()
            .body("status", is(true))
            .body("data.size()", is(greaterThan(0)))
            .body("data[0].name", is(taskName))
            .body("data[0].stageId", is(stageId))
            .extract().path("data[0].id")
        ;
    }

    @Test
    public void t05_notFindTaskWithInvalidStageID() {
        given()
            .header("Authorization", "Bearer "+ token)
        .when()
            .get("/find/task/stage/invalid")
        .then()
            .body("data.size", hasSize(0))
        ;
    }

    @Test
    public void t06_notFindTaskWithInvalidToken() {
        given()
            .header("Authorization", "Bearer invalid")
        .when()
            .get("/find/task/stage/" + stageId)
        .then()
            .body("status", is(false))
            .body("message", is("Sua sessão expirou, faça login novamente."))       
        ;
    }

    @Test
    public void t07_findTaskByTaskID() {
        given()
            .header("Authorization", "Bearer "+ token)
        .when()
            .get("/find/task/" + taskId)
        .then()
            .body("status", is(true))
            .body("data.size()", is(greaterThan(0)))
            .body("data[0].name", is(taskName))
            .body("data[0].stageId", is(stageId))
        ;
    }

    @Test
    public void t08_notFindTaskWithInvalidToken() {
        given()
            .header("Authorization", "Bearer invalid")
        .when()
            .get("/find/task/" + taskId)
        .then()
            .body("status", is(false))
            .body("message", is("Sua sessão expirou, faça login novamente.")) 
        ;
    }

    @Test
    public void t09_updateTask() {
        Map<String, String> task = new HashMap<String,String>();
        task.put("name", "Update Stage Name");
        task.put("taskId", taskId);
        task.put("description", "Update Description");
        task.put("urgency", "high");
        task.put("currentStage", stageId);

        given()
            .contentType("application/json")
            .header("Authorization", "Bearer "+ token)
            .body(task)
        .when()
            .put("/update/task")
        .then()
            .body("status", is(true))
            .body("message", is("Task atualizada com sucesso."))
        ;
    }

    @Test
    public void t10_notUpdateTaskWithInvalidTaskID() {
        Map<String, String> task = new HashMap<String,String>();
        task.put("name", "Update Stage Name");
        task.put("taskId", "invalid");
        task.put("description", "Update Description");
        task.put("urgency", "high");
        task.put("currentStage", stageId);

        given()
            .contentType("application/json")
            .header("Authorization", "Bearer "+ token)
            .body(task)
        .when()
            .put("/update/task")
        .then()
            .body("status", is(false))
            .body("message.meta.cause", is("Record to update not found."))
        ;
    }

    @Test
    public void t11_notUpdateTaskWithInvalidToken() {
        Map<String, String> task = new HashMap<String,String>();
        task.put("name", "Update Stage Name");
        task.put("taskId", taskId);
        task.put("description", "Update Description");
        task.put("urgency", "high");
        task.put("currentStage", stageId);

        given()
            .contentType("application/json")
            .header("Authorization", "Bearer invalid")
            .body(task)
        .when()
            .put("/update/task")
        .then()
            .body("status", is(false))
            .body("message", is("Sua sessão expirou, faça login novamente.")) 
        ;
    }

    @Test
    public void t12_notDeleteTaskWithInvalidTaskID() {
        given()
            .header("Authorization", "Bearer "+ token)
        .when()
            .delete("/delete/task/invalid")
        .then()
            .body("status", is(false))
            .body("message.meta.cause", is("Record to delete does not exist."))
        ;
    }

    @Test
    public void t13_notDeleteTaskWithInvalidToken() {
        given()
            .header("Authorization", "Bearer token")
        .when()
            .delete("/delete/task/" + taskId)
        .then()
            .body("status", is(false))
            .body("message", is("Sua sessão expirou, faça login novamente.")) 
        ;
    }

    @Test
    public void t14_deleteTask() {
        given()
            .header("Authorization", "Bearer "+ token)
        .when()
            .delete("/delete/task/" + taskId)
        .then()
            .body("status", is(true))
            .body("message", is("Task deletada com sucesso."))
        ;
    }

    @Test
    public void t15_deleteTaskWithStageID() {
        t01_createNewTask();

        given()
            .header("Authorization", "Bearer "+ token)
        .when()
            .delete("/delete/task/stage/" + stageId)
        .then()
            .body("status", is(true))
            .body("message", is("Task deletada com sucesso."))
        ;
    }

    @AfterClass
    public static void clean() {
        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .delete("/delete/stage/" + stageId)
        .then()
            .body("status", is(true))
            .body("message", is("Stage deletado com sucesso."))
        ; 

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
