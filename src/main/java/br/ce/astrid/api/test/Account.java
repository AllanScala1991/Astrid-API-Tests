package br.ce.astrid.api.test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import com.github.javafaker.Faker;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;

import br.ce.astrid.api.utils.Utils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Account {
	
	private static String fakeName;
	private static String fakeEmail;
	private static String fakePassword;
	private static Faker faker;

	@BeforeClass
	public static void setup()throws IOException{
		baseURI = new Utils().getBaseUrl();
		port = new Utils().getPort();
		faker = new Faker();
		fakeName = faker.name().firstName();
		fakeEmail = fakeName + "@mail.com";
		fakePassword = faker.number().digits(5);
	}

	@Test
	public void t01_createNewAccount() {
		Map<String, String> newUser = new HashMap<String, String>();
		newUser.put("name", fakeName);
		newUser.put("email", fakeEmail);
		newUser.put("password", fakePassword);
		
		given()
			.contentType("application/json")
			.body(newUser)
		.when()
			.post("/user/create")
		.then()
			.body("status", is(true))
			.body("message", is("Usuário criado com sucesso."))
		;
		
	}
	
	@Test
	public void t02_sendInvalidBody() {
		Map<String, String> newUser = new HashMap<String, String>();
		newUser.put("name", fakeName);
		newUser.put("email", fakeEmail);
		
		given()
			.contentType("application/json")
			.body(newUser)
		.when()
			.post("/user/create")
		.then()
			.body("status", is(false))
			.body("message", is("Todos os campos devem ser preenchidos."))
		;
	}

	@Test
	public void t03_getOneUserByID() throws IOException {
		Object userID =  Utils.getUserID(fakeEmail, fakePassword);
		given()
		.when()
			.get("/user/find/"+ userID)
		.then()
			.body("status", is(true))
			.body("data.id", is(userID))
			.body("data.name", is(fakeName))
			.body("data.email", is(fakeEmail))
			.body("data.password", is(notNullValue()))
		;
	}
	
	@Test
	public void t04_sendInvalidUserID() {
		given()
		.when()
			.get("/user/find/invalidID")
		.then()
			.body("data", nullValue())
		;
	}

	@Test
	public void t05_updateUser() throws IOException{
		String userID =  Utils.getUserID(fakeEmail, fakePassword).toString();
		Map<String, String> user = new HashMap<String,String>();
		user.put("name", "update Name");
		user.put("email", "updateMail@mail.com");
		user.put("id", userID);

		given()
			.contentType("application/json")
			.body(user)
		.when()
			.put("/user/update")
		.then()
			.body("status", is(true))
			.body("message", is("Usuário atualizado com sucesso."))
		;
	}

	@Test
	public void t06_sendInvalidUserID() {
		Map<String, String> user = new HashMap<String,String>();
		user.put("name", "update Name");
		user.put("email", "updateMail@mail.com");
		user.put("id", "invalid");

		given()
			.contentType("application/json")
			.body(user)
		.when()
			.put("/user/update")
		.then()
			.body("status", is(false))
			.body("message.code", is("P2025"))
			.body("message.meta.cause", is("Record to update not found."))
		;
	}

	@Test
	public void t07_deleteUserWithInvalidID() {
		given()
		.when()
			.delete("/user/delete/invalidID")
		.then()
			.body("status", is(false))
			.body("message.meta.cause", is("Record to delete does not exist."))
		;
	}

	@Test
	public void t08_deleteUser() throws IOException {
		String userID =  Utils.getUserID("updateMail@mail.com", fakePassword).toString();

		given()
		.when()
			.delete("/user/delete/"+userID)
		.then()
			.body("status", is(true))
			.body("message", is("Usuário deletado com sucesso."))
		;
	}
}

