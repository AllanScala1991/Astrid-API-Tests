package br.ce.astrid.api.test;
import static io. restassured. RestAssured.given;
import static org.hamcrest.Matchers.*;
import org.junit.Test;

public class Account {
	
	@Test
	public void createNewAccount() {
		given()
			.log().all()
		.when()
			.get("https://viacep.com.br/ws/83701300/json/")
		.then()
			.log().all()
		;
		
	}
	
}
