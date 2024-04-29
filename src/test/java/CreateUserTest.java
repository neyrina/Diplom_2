import client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.UserRequest;
import io.restassured.response.ValidatableResponse;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest {
    private UserClient userClient;
    private String accessToken;
    private ValidatableResponse response;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    @DisplayName("Удаление пользователя")
    public void deleteUser() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken)
                    .assertThat().statusCode(SC_ACCEPTED)
                    .body("success", equalTo(true));
        }
    }

    @Test
    @DisplayName("Создание нового пользователя")
    public void createUserTest() {
        UserRequest userRequest = UserRequest.getRandomUserRequest();
        response = userClient.create(userRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        accessToken = response.extract().path("accessToken");
    }

    @Test
    @DisplayName("Создание нового пользователя без поля email")
    public void createUserWithoutEmailTest() {
        UserRequest userRequest = UserRequest.getRandomUserRequestWithoutEmailField();
        response = userClient.create(userRequest);
        accessToken = response.extract().path("accessToken");
        response
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание нового пользователя без поля имя")
    public void createUserWithoutNameTest() {
        UserRequest userRequest = UserRequest.getRandomUserRequestWithoutNameField();
        response = userClient.create(userRequest);
        accessToken = response.extract().path("accessToken");
        response
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание нового пользователя без поля пароль")
    public void createUserWithoutPasswordFieldTest() {
        UserRequest userRequest = UserRequest.getRandomUserRequestWithoutPasswordField();
        response = userClient.create(userRequest);
        accessToken = response.extract().path("accessToken");
        response
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание пользователя который уже существует")
    public void createNotUniqueUserTest() {
        UserRequest userRequest = UserRequest.getRandomUserRequest();
        response = userClient.create(userRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
        accessToken = response.extract().path("accessToken");
        userClient.create(userRequest)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false));
    }
}
