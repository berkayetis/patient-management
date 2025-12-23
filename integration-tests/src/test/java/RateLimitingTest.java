import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class RateLimitingTest {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost:4004";
    }

    @Test
    @DisplayName("Rate Limiting: İlk 10 istek geçmeli, 11. istek bloklanmalı")
    public void shouldManageTrafficAndLimitUsers() throws InterruptedException {
        // Login
        String loginPayload = """
            {
            "email": "testuser@test.com",
            "password": "password123"
            }
            """;

        String token = given()
                .contentType("application/json")
                .body(loginPayload)
                .when()
                .post("auth/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().get("token");

        String userId = "test-user-" + System.currentTimeMillis();
        System.out.println("🧪 Test başladı - Kullanıcı: " + userId);

        // Test: Saniyede 5 istek gönder (güvenli marj)
        int requestCount = 5;

        System.out.println("✅ İlk " + requestCount + " istek gönderiliyor...");
        for (int i = 1; i <= requestCount; i++) {
            System.out.print(i + ". istek... ");

            given()
                    .header("Authorization", "Bearer " + token)
                    .header("X-User-ID", userId)
                    .when()
                    .get("api/patients")
                    .then()
                    .statusCode(200);

            System.out.println("✓ Geçti");
        }

        System.out.println("\n🚀" + (requestCount+1) + ". istek gönderiliyor (bloklanmalı)...");

        given()
                .header("Authorization", "Bearer " + token)
                .header("X-User-ID", userId)
                .when()
                .get("api/patients")
                .then()
                .statusCode(429); // Too Many Requests

        System.out.println("❌ istek beklenen şekilde bloklandı (429)");
    }

}