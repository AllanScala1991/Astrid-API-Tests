package br.ce.astrid.api.test;

import org.junit.BeforeClass;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class Board {
    
    @BeforeClass
    public void setup () {
        baseURI = "http://localhost";
		port = 8000;
    }

    @Test
    public void createBoard() {
        Map<String, String> board = new HashMap<String,String>();
        given()
            .log().all()
        .when()
            .post("")
        .then()
            .log().all()
        ;
    }
}
