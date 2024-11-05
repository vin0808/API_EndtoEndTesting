package com.sep.basic.Day1;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class FirstTest2 {

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
		RequestSpecification req=RestAssured.given().pathParam("num","3");
		Response res=req.when().get("users/{num}");
		
		res.then().statusCode(200);
		res.then().time(lessThan(4000L));
		res.then().body("data.first_name",equalTo("Emma"));
		
	System.out.println(	res.getStatusCode());
	System.out.println(res.contentType());
	System.out.println(res.asString());
	res.prettyPrint();
	
	JsonPath json=res.jsonPath();
	String firstName=json.get("data.first_name");
	System.out.println(firstName);
		 
		
		
		
		
	}
	
	@Test
	public void simpleTest3() {
		RestAssured.baseURI="https://reqres.in/";
		RestAssured.basePath="api/";
		RequestSpecification req=RestAssured.given().queryParam("page","2");
		Response res=req.when().get("users");
		
		res.then().statusCode(200);
		res.then().time(lessThan(4000L));
		res.prettyPrint();
		
		 JsonPath json= res.jsonPath();
		List<String> emails=  json.getList("data.email");
		System.out.println(emails);
		for(String email:emails) {
			System.out.println(email);
		}
		System.out.println("-----------------------------");
		List<String> emailslessthanid=json.getList("data.findAll{obj->obj.id<=10}.email");
		for(String email:emailslessthanid) {
			System.out.println(email);
		}
		System.out.println("-----------------------------");
		
		String emailslessthanidSingle=json.getString("data.find{obj->obj.id<=10}.email");
		System.out.println(emailslessthanidSingle);
		
		System.out.println(res.jsonPath().getInt("data.id.sum()"));
		System.out.println(res.jsonPath().getInt("data.id.min()"));
		System.out.println(res.jsonPath().getInt("data.id.max()"));
		
		
		res.then().body("total",equalTo(12) );
		
	}
}
//