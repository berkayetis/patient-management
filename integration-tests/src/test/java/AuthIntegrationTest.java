import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class AuthIntegrationTest {
    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    public void shouldReturnUnauthorizedWithInvalidUser() {
        // 1.arrange
        // 2.act
        // 3.assert

        String loginPayload = """
                {
                "email": "invalid_user@test.com",
                "password": "wrong_password"
                }
                """;

        Response response = given().contentType(ContentType.JSON).body(loginPayload)
                .when().post("/login")
                .then().statusCode(401)
                .body("token", notNullValue())
                .extract()
                .response();

    }

    @Test
    public void shouldReturnOkWithValidToken() {
        // 1.arrange
        // 2.act
        // 3.assert

        String loginPayload = """
                {
                "email": "testuser@test.com",
                "password": "password123"
                }
                """;

        Response response = given().contentType(ContentType.JSON).body(loginPayload)
                .when().post("/login")
                .then().statusCode(200)
                .body("token", notNullValue())
                .extract()
                .response();

        System.out.println("Generated token: " + response.jsonPath().getString("token"));

    }
}
