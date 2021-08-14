import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CommentTests extends BaseTest {

    @Test(description = "This test create Comment")
    public void createComment() {
        Response response = creatingComment();
        response.print();

        //Positive Asserts
        Assert.assertEquals(response.getStatusCode(), 200, "Status code isn't 200");
        Assert.assertEquals(response.jsonPath().getString("message"), "Comment created", "Error message");

        //Negative Assert - try to create new Comment without valid credentials
        Response negativeResponse = creatingCommentWithFakeUser();
        negativeResponse.print();
        Assert.assertEquals(negativeResponse.jsonPath().getString("message"),"Please login first","This user not exists, you should not be logged in");
    }

    @Test(description = "This test get all Comments available")
    public void getAllComments() {
        //Create new comment to get all comments who belongs to
        Response responseCreate = creatingComment();
        responseCreate.print();

        //get response with all Comments
        Response response = RestAssured.given().auth().preemptive().basic("testuser", "testpass").get("/v1/comments/"+postID);
        response.print();

        //Positive Assertions
        //Verify response is 200
        Assert.assertEquals(response.getStatusCode(), 200, "Status code isn't 200");

        //Verify at least 1 comment in the result response
        int numberOfPosts = response.jsonPath().getInt("results.meta.total[0]");
        System.out.println("Number of comments available "+numberOfPosts);
        Assert.assertTrue(numberOfPosts>0, "Posts list is empty");

        //Negative Assert - try to get all comments with non existing user
        Response negativeResponse = RestAssured.given().auth().preemptive().basic("fakeUser", "fakePass").get("/v1/comments/"+postID);
        negativeResponse.print();
        Assert.assertEquals(negativeResponse.jsonPath().getString("message"),"Please login first","This user not exists, you should not be logged in");
    }

    @Test(description = "This test get one Comment")
    public void getOneComment() {
       // Create a comment to get
        Response responseCreate = creatingComment();
        responseCreate.print();

        // Get id of new comment
        int commentId = responseCreate.jsonPath().getInt("id");

        //get response with the given comment id
        Response response = RestAssured.given().auth().preemptive().basic("testuser", "testpass").get("/v1/comment/"+postID+"/"+commentId);
        response.body().print();

        //Positive Asserts
        Assert.assertEquals(response.getStatusCode(), 200, "Status code isn't 200");
        //Verify name is the given one
        String title = response.jsonPath().getString("data.name");
        Assert.assertTrue(title.equalsIgnoreCase("Name of comment"), "Name doesn't match with the expected");
        //Verify the comment is the given one
        String content = response.jsonPath().getString("data.comment");
        Assert.assertTrue(content.equalsIgnoreCase("Some comment"), "Comment doesn't match with the expected");

        //Negative Assert - Get non existing comment (1)
        Response negativeResponse = RestAssured.given().auth().preemptive().basic("testuser", "testpass").get("/v1/comment/3658/1");
        negativeResponse.print();
        Assert.assertEquals(negativeResponse.jsonPath().getString("Message"),"Comment not found","Comment was found but shouldn't be found");
    }

    @Test(description = "This test update one Comment")
    public void updateComment() {
        // Create comment to update
        Response responseCreate = creatingComment();
        responseCreate.print();

        // Get id of new comment
        int commentID = responseCreate.jsonPath().getInt("id");

        // Create JSON body to update
        JSONObject body = new JSONObject();
        body.put("name", "Updated name");
        body.put("comment", "Updated comment");

        // Update comment
        Response response = RestAssured.given().auth().preemptive().basic("testuser", "testpass").contentType(ContentType.JSON).body(body.toString())
                .put("/v1/comment/" + postID+"/"+commentID);
        response.print();

        // Positive Asserts
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200, but it's not.");
        Assert.assertEquals(response.jsonPath().getString("message"),"Comment updated","Comment was not updated, but it should");

        //Negative Assert - try to update non existing comment (1)
        Response negativeResponse = RestAssured.given().auth().preemptive().basic("testuser", "testpass").contentType(ContentType.JSON).body(body.toString())
                .put("/v1/comment/3658/1");
        negativeResponse.print();
        Assert.assertEquals(negativeResponse.jsonPath().getString("message"),"Comment could not be updated","Comment was found but shouldn't be found");
    }

    @Test(description = "This test delete a Comment")
    public void deleteComment() {
        // Create Comment to delete
        Response responseCreate = creatingComment();
        responseCreate.print();

        // Get id of new comment
        int commentId = responseCreate.jsonPath().getInt("id");

        // Delete the new comment
        Response responseToDelete = RestAssured.given().auth().preemptive().basic("testuser", "testpass")
                .delete("/v1/comment/"+ postID+"/"+ commentId);
        responseToDelete.print();

        // Positive Asserts
        Assert.assertEquals(responseToDelete.getStatusCode(), 200, "Status code should be 200, but it's not.");
        Assert.assertEquals(responseToDelete.jsonPath().get("message"), "Comment deleted", "Comment it's not deleted, but should be");

        // Negative Assert - try to delete a Comment already deleted
        Response responseDeleted = RestAssured.given().auth().preemptive().basic("testuser", "testpass")
                .delete("/v1/comment/"+ postID+"/"+ commentId);
        responseDeleted.print();
        Assert.assertEquals(responseDeleted.jsonPath().get("error"),"Comment not found","Comment it's already deleted");
    }
}
