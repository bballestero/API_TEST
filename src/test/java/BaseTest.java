import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import specifications.RequestSpecifications;

public class BaseTest {

    protected int postID;

    @Parameters("host")
    @BeforeSuite(alwaysRun = true)
    public void setup(@Optional("https://api-coffee-testing.herokuapp.com") String host) {
        System.out.printf("Test Host: %s%n", host);
        RestAssured.baseURI = host;
    }

    protected Response creatingPost() {
        //Create JSON Body for new Post
        JSONObject body = new JSONObject();
        body.put("title", "Title of the post");
        body.put("content", "Post content");

        //Get Response
        return RestAssured.given(RequestSpecifications.useJWTAuthentication()).contentType(ContentType.JSON).
                body(body.toString()).post("/v1/post");
    }

    protected Response creatingComment() {
        // Create a post who belongs to comment
        Response responseCreate = creatingPost();
        responseCreate.print();

        // Get id of new Post
        postID = responseCreate.jsonPath().getInt("id");

        //Create JSON Body for new Comment
        JSONObject body = new JSONObject();
        body.put("name", "Name of comment");
        body.put("comment", "Some comment");

        //Get Response
        return RestAssured.given().auth().preemptive().basic("testuser", "testpass").contentType(ContentType.JSON).
                body(body.toString()).post("/v1/comment/"+postID);
    }

    protected Response creatingCommentWithFakeUser() {
        // Create a post who belongs to comment
        Response responseCreate = creatingPost();
        responseCreate.print();

        // Get id of new Post
        postID = responseCreate.jsonPath().getInt("id");

        //Create JSON Body for new Comment
        JSONObject body = new JSONObject();
        body.put("name", "Name of comment");
        body.put("comment", "Some comment");

        //Get Response
        return RestAssured.given().auth().preemptive().basic("username", "123").contentType(ContentType.JSON).
                body(body.toString()).post("/v1/comment/"+postID);
    }

}
