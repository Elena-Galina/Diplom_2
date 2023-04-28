package stellarburgers.user_tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.user.User;
import stellarburgers.user.UserClient;

import static org.apache.http.HttpStatus.*;
import static stellarburgers.user.UserGenerator.*;

public class UserLoginTest {
    private User user;
    private String userAccessToken;

    @Before
    public void setUp() {
        user = randomeUser();
    }

    @After
    public void tearDown() {
        UserClient.userDeleteAfterTest(userAccessToken, SC_ACCEPTED, true, "User successfully removed");
    }

    @Feature(value = "api/auth/login")
    @Test
    @DisplayName("Проверка авторизации под существующим пользователем.")
    @Description("Тест проверяет авторизацию под существующим пользователем. Успешный запрос возвращает \"success\": true.")
    public void userCanBeLoginTest() {
        Response newUser = UserClient.userCreate(user);
        userAccessToken = UserClient.getUserAccessToken(newUser);

        Response userLogin = UserClient.userLogin(user);
        UserClient.printResponseBodyToConsole(userLogin);

        UserClient.checkResponseUserCreateOrLogin(userLogin, SC_OK, true);
    }

    @Feature(value = "api/auth/login")
    @Test
    @DisplayName("Проверка авторизации пользователя с некорректными данными.")
    @Description("Тест проверяет, что нельзя авторизовать пользователя с неверным логином и паролем. Возвращается ошибка.")
    public void userCanNotBeLoginWithInvalidFieldsTest() {
        Response newUser = UserClient.userCreate(user);
        userAccessToken = UserClient.getUserAccessToken(newUser);

        Response incorrectUserLogin = UserClient.userLogin(randomeUser());
        UserClient.checkResponseError(incorrectUserLogin, SC_UNAUTHORIZED, false,
                "email or password are incorrect");
    }
}