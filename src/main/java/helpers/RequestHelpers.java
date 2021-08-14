package helpers;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import model.User;

import static io.restassured.RestAssured.given;

public class RequestHelpers {
    public static String TOKEN = "";

    //   get a json token from the login request
    public static String getAuthToken () {

        User testUser = new User(
                "Lionel Messi",
                "vhwnyfo@testemail.com",
                "password");

        Response response = given().body(testUser).post("/v1/user/login");
        JsonPath jsonPath = response.jsonPath();
        TOKEN = jsonPath.get("token.access_token");
        //System.out.println("New token fetched " + TOKEN);
        return TOKEN;
    }
}
