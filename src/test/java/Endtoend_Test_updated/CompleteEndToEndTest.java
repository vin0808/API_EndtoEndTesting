package Endtoend_Test_updated;

import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;

public class CompleteEndToEndTest {
    String freshToken;
    String createdUserId;
    String createdId;

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = "https://us-central1-qa01-tekarch-accmanager.cloudfunctions.net/";
    }

    @Test
    public void getToken() {
        Response res = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .body("{\"username\":\"sept2024.vinutha@tekarch.com\",\"password\":\"Admin123\"}")
            .when()
            .post("login");

        res.then()
            .statusCode(201)
            .time(lessThan(4000L))
            .contentType(ContentType.JSON);

        freshToken = res.jsonPath().get("[0].token");
        System.out.println("Token: " + freshToken);
    }

    @Test(dependsOnMethods = "getToken")
    public void getAllUsers() {
        Header tokenHeader = new Header("token", freshToken);
        Response res = RestAssured
            .given()
            .header(tokenHeader)
            .when()
            .get("getdata");

        res.then()
            .statusCode(200)
            .contentType(ContentType.JSON);

        System.out.println("total records="+res.jsonPath().get("size()"));
		System.out.println("first record account no="+res.jsonPath().get("[0].accountno"));
		System.out.println("first record user id="+res.body().jsonPath().get("[0].userid"));
		System.out.println("first record id="+res.body().jsonPath().get("[0].id"));
    }

    @Test(dependsOnMethods = "getAllUsers")
    public void createUser() {
        Header tokenHeader = new Header("token", freshToken);
        Response res = RestAssured
            .given()
            .header(tokenHeader)
            .contentType(ContentType.JSON)
            .body("{\"accountno\":\"TA-Marshmallow01\",\"departmentno\":\"8\",\"salary\":\"1000\",\"pincode\":\"8888\"}")
            .when()
            .post("addData");

        res.then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .time(lessThan(10000L));

        // Since createUser response does not contain `userid` or `id`, fetch all users again
        Response allUsersResponse = RestAssured
            .given()
            .header(tokenHeader)
            .when()
            .get("getdata");

        allUsersResponse.then()
            .statusCode(200);

        // Find the created user by matching the account number
        List<Map<String, String>> users = allUsersResponse.jsonPath().getList("$");
        for (Map<String, String> user : users) {
            if ("TA-Marshmallow01".equals(user.get("accountno"))) {
                createdUserId = user.get("userid");
                createdId = user.get("id");
                break;
            }
        }

        if (createdUserId == null || createdId == null) {
            System.err.println("Failed to retrieve created user ID or ID. Please check if the account was created.");
        } else {
            System.out.println("Created User ID: " + createdUserId);
            System.out.println("Created ID: " + createdId);
        }
    }



    @Test(priority = 4, dependsOnMethods = "createUser")
    public void updateUser() {
        Header tokenHeader = new Header("token", freshToken);
        Response res = RestAssured
            .given()
            .header(tokenHeader)
            .contentType(ContentType.JSON)
            .body("{\"accountno\":\"TA-Marshmallow01\",\"departmentno\":\"8\",\"salary\":\"1200\",\"pincode\":\"9999\"," +
                "\"userid\":\"" + createdUserId + "\",\"id\":\"" + createdId + "\"}")
            .when()
            .put("updateData");

        res.then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .time(lessThan(10000L))
            .body("status", equalTo("success"));

        System.out.println("User updated with new salary and pincode for User ID: " + createdUserId);
    }

    @Test(priority = 5, dependsOnMethods = "updateUser")
    public void deleteUser() {
        Header tokenHeader = new Header("token", freshToken);
        Response res = RestAssured
            .given()
            .header(tokenHeader)
            .contentType(ContentType.JSON)
            .body("{\"userid\":\"" + createdUserId + "\",\"id\":\"" + createdId + "\"}")
            .when()
            .delete("deleteData");

        res.then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("status", equalTo("success"));

        System.out.println("User successfully deleted for User ID: " + createdUserId);
    }

    // Hardcoded delete method for testing specific values directly
    @Test(priority = 6)
    public void deleteUserWithHardCodedValues() {
        String hardCodedUserId = "K8BmcrR3nEhCJf6xGZKZ"; // Replace with known User ID
        String hardCodedId = "s3wrxkPHbc55RCyIV06f";    // Replace with known ID

        Header tokenHeader = new Header("token", freshToken);
        Response res = RestAssured
            .given()
            .header(tokenHeader)
            .contentType(ContentType.JSON)
            .body("{\"userid\":\"" + hardCodedUserId + "\",\"id\":\"" + hardCodedId + "\"}")
            .when()
            .delete("deleteData");

        res.then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("status", equalTo("success"));

        System.out.println("User successfully deleted with hardcoded User ID: " + hardCodedUserId);
    }
}
