
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import specifications.RequestSpecifications;


public class PostTests extends BaseTest{

    @Test(priority = 1)
    public void createPost() {
        Response response = creatingPost();
        response.print();

        //Positive Asserts
        Assert.assertEquals(response.getStatusCode(), 200, "Status code isn't 200");
        Assert.assertEquals(response.jsonPath().getString("message"), "Post created", "Error message");

        //Negative Assert - try to create new Post without valid credentials
        Response negativeResponse = RestAssured.given().post("/v1/post");
        negativeResponse.print();
        Assert.assertEquals(negativeResponse.jsonPath().getString("message"),"Please login first","This user not exists, you should not be logged in");
    }

    @Test(priority = 2)
    public void getAllPosts() {
       //get response with all Posts
       Response response = RestAssured.given(RequestSpecifications.useJWTAuthentication()).get("/v1/posts");
       response.print();

       //Positive Assertions
       //Verify response is 200
        Assert.assertEquals(response.getStatusCode(), 200, "Status code isn't 200");

        //Verify at least 1 Post in the result response
        int numberOfPosts = response.jsonPath().getInt("results.meta.total[0]");
        System.out.println("Number of Posts available "+numberOfPosts);
        Assert.assertTrue(numberOfPosts>0, "Posts list is empty");

        //Negative Assert - try to get all posts with non existing user
        Response negativeResponse = RestAssured.given().get("/v1/posts");
        negativeResponse.print();
        Assert.assertEquals(negativeResponse.jsonPath().getString("message"),"Please login first","This user not exists, you should not be logged in");
    }

    @Test(priority = 3)
    public void getOnePost() {
        // Create a post to get
        Response responseCreate = creatingPost();
        responseCreate.print();

        // Get id of new Post
        int id = responseCreate.jsonPath().getInt("id");

        //get response with the given post id
        Response response = RestAssured.given(RequestSpecifications.useJWTAuthentication()).get("/v1/post/"+id);
        response.body().print();

        //Positive Asserts
        Assert.assertEquals(response.getStatusCode(), 200, "Status code isn't 200");
        //Verify title is the given one
        String title = response.jsonPath().getString("data.title");
        Assert.assertTrue(title.equalsIgnoreCase("Title of the post"), "Title doesn't match with the expected");
        //Verify the content is the given one
        String content = response.jsonPath().getString("data.content");
        Assert.assertTrue(content.equalsIgnoreCase("Post content"), "Content doesn't match with the expected");

        //Negative Assert - Get non existing post (1)
        Response negativeResponse = RestAssured.given(RequestSpecifications.useJWTAuthentication()).get("/v1/post/1");
        negativeResponse.print();
        Assert.assertEquals(negativeResponse.jsonPath().getString("Message"),"Post not found","Post was found but shouldn't be found");
    }

    @Test(priority = 4)
    public void updatePost() {
        // Create Post to update
        Response responseCreate = creatingPost();
        responseCreate.print();

        // Get id of new Post
        int id = responseCreate.jsonPath().getInt("id");

        // Create JSON body to update
        JSONObject body = new JSONObject();
        body.put("title", "Updated title");
        body.put("content", "Updated content");

        // Update Post
        Response response = RestAssured.given(RequestSpecifications.useJWTAuthentication()).contentType(ContentType.JSON).body(body.toString())
                .put("/v1/post/" + id);

        response.print();

        // Positive Asserts
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200, but it's not.");
        Assert.assertEquals(response.jsonPath().getString("message"),"Post updated","Post was not updated, but it should");

        //Negative Assert - try to update non existing post (1)
        Response negativeResponse = RestAssured.given(RequestSpecifications.useJWTAuthentication()).contentType(ContentType.JSON).body(body.toString())
                .put("/v1/post/1");
        negativeResponse.print();
        Assert.assertEquals(negativeResponse.jsonPath().getString("error"),"Post not found","Post was found but shouldn't be found");
    }

    @Test(priority = 5)
    public void deletePost() {
        // Create Post to delete
        Response responseCreate = creatingPost();
        responseCreate.print();

        // Get id of new Post
        int id = responseCreate.jsonPath().getInt("id");

        // Delete the new Post
        Response responseToDelete = RestAssured.given(RequestSpecifications.useJWTAuthentication())
                .delete("/v1/post/" + id);
        responseToDelete.print();

        // Positive Asserts
        Assert.assertEquals(responseToDelete.getStatusCode(), 200, "Status code should be 200, but it's not.");
        Assert.assertEquals(responseToDelete.jsonPath().get("message"), "Post deleted", "Post it's not deleted, but should be");

        // Negative Asserts - try to delete a Post already deleted
        Response responseDeleted = RestAssured.given(RequestSpecifications.useJWTAuthentication())
                .delete("/v1/post/" + id);
        responseDeleted.print();
        Assert.assertEquals(responseDeleted.jsonPath().get("error"),"Post not found","Post it's already deleted");
    }

}
