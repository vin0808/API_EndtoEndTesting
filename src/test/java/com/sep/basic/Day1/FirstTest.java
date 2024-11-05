package com.sep.basic.Day1;


	import static org.hamcrest.Matchers.*;
	import org.testng.annotations.Test;

	import io.restassured.RestAssured;
	import io.restassured.builder.RequestSpecBuilder;
	import io.restassured.path.json.JsonPath;
	import io.restassured.response.Response;
	import io.restassured.specification.RequestSpecification;

	public class FirstTest {

		@Test
		public void simpleTest() {
			
			RestAssured
			.given()
			.when()
				.get("https://reqres.in/api/users/2")
			.then()
				.statusCode(200)
				.time(lessThan(4000L))
				.body("data.first_name",equalTo("Janet"))
				.body("data.id", notNullValue());
			
		}
		
		@Test
		public void simpleTest2() {
			RestAssured.baseURI="https://reqres.in/";
			RestAssured.basePath="api/";
			RequestSpecification req=RestAssured.given();
			Response res=req.when().get("users/2");
			
			res.then().statusCode(200);
			res.then().time(lessThan(4000L));
			res.then().body("data.first_name",equalTo("Janet"));
			
		System.out.println(	res.getStatusCode());
		System.out.println(res.contentType());
		System.out.println(res.asString());
		res.prettyPrint();
		
		JsonPath json=res.jsonPath();
		String firstName=json.get("data.first_name");
		System.out.println(firstName);
			 
			
			
			
			
		}
	}

