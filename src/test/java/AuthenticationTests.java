import model.User;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;
import specifications.RequestSpecifications;
import specifications.ResponseSpecifications;

import static helpers.DataHelper.generateRandomEmail;
import static io.restassured.RestAssured.given;

public class AuthenticationTests extends BaseTest{
    /**
 * I only used this class to create my own User and test the Login and Logout
 * */
    @Test(description = "This test aims to register a user")
    public void testRegister(){
        User testUser = new User(
                    "Lionel Messi",
                    generateRandomEmail(),
                    "password");

        String email = testUser.getEmail();

        given()
                .body(testUser)
            .when()
                .post("/v1/user/register")
            .then()
                .log().all()
                .spec(ResponseSpecifications.validatePositiveResponse())
                .body("user.email", Matchers.equalTo(email))
                .body("user.name", Matchers.equalTo("Lionel Messi"));
    }

    @Test(description = "This test aims to register login a user")
    public void testLogin(){
        User testUser = new User(
                "Lionel Messi",
                "vhwnyfo@testemail.com",
                "password");

        given()
                .body(testUser)
                .when()
                .post("/v1/user/login")
                .then()
                .log().all()
                .spec(ResponseSpecifications.validatePositiveResponse())
                .body("token.access_token", Matchers.notNullValue())
                .body("user.email", Matchers.equalTo("vhwnyfo@testemail.com"));
    }

    @Test(description = "This aims to test logout")
    public void testLogOut(){

        given()
                .spec(RequestSpecifications.useJWTAuthentication())
                .when()
                .get("/v1/user/logout")
                .then()
                .spec(ResponseSpecifications.validatePositiveResponse())
                .body("message", Matchers.equalTo("Successfully logged out"));
    }
}
