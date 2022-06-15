package br.ce.astrid.api.test;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import static org.hamcrest.Matchers.*;
import br.ce.astrid.api.utils.Utils;

import static io.restassured.RestAssured.*;

import java.util.HashMap;
import java.util.Map;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Login {
    
    private static String email;
    private static String password;
    
    @BeforeClass
    public static void setup() {
        baseURI = new Utils().getBaseUrl();
        port = new Utils().getPort();
        email = new Utils().getEmail();
        password = new Utils().getPassword();
    }

    @Test
    public void t01_loginSuccessfully() {
        Map<String, String> user = new HashMap<String,String>();
        user.put("email", email);
        user.put("password", password);

        given()
            .contentType("application/json")
            .body(user)
        .when()
            .post("/login")
        .then()
            .body("status", is(true))
            .body("token", is(notNullValue()))
        ;
    }

    @Test
    public void t02_notLoginWithInvalidEmail() {
        Map<String, String> user = new HashMap<String,String>();
        user.put("email", "invalid@");
        user.put("password", password);

        given()
            .contentType("application/json")
            .body(user)
        .when()
            .post("/login")
        .then()
            .body("status", is(false))
            .body("message", is("Usuário ou Senha incorretos."))
        ;
    }

    @Test
    public void t03_notLoginWithInvalidPassword() {
        Map<String, String> user = new HashMap<String,String>();
        user.put("email", email);
        user.put("password", "invalid");

        given()
            .contentType("application/json")
            .body(user)
        .when()
            .post("/login")
        .then()
            .body("status", is(false))
            .body("message", is("Usuário ou Senha incorretos."))
        ;
    }

    @Test
    public void t04_notLoginWithNotSendBody() {
        given()
            .contentType("application/json")
            .body("")
        .when()
            .post("/login")
        .then()
            .body("status", is(false))
            .body("message", is("O email e a senha são obrigatórios."))
        ;
    }
}
