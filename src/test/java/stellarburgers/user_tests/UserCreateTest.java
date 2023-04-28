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

import static stellarburgers.user.UserGenerator.randomeUser;
import static org.apache.http.HttpStatus.*;

public class UserCreateTest {
    private User user;
    private String userAccessToken;

    @Before
    public void setUp() {
        user = randomeUser();
    }

    @After
    public void tearDown() {
        if (userAccessToken != null) {
            UserClient.userDeleteAfterTest(userAccessToken, SC_ACCEPTED, true, "User successfully removed");
        }
    }

    @Feature(value = "api/auth/register")
    @Test
    @DisplayName("Проверка создания пользователя.")
    @Description("Тест проверяет создание уникального пользователя. Успешный запрос возвращает \"success\": true.")
    public void userCanBeCreatedTest() {
        Response newUser = UserClient.userCreate(user);

        UserClient.checkResponseUserCreateOrLogin(newUser, SC_OK, true);
        UserClient.printResponseBodyToConsole(newUser);

        userAccessToken = UserClient.getUserAccessToken(newUser);
    }

    @Feature(value = "api/auth/register")
    @Test
    @DisplayName("Проверка создания пользователя, который уже зарегистрирован.")
    @Description("Тест проверяет, что повторное создание уже зарегистрированного пользователя возвращает ошибку.")
    public void doubleUserCanNotBeCreatedTest() {
        Response newUser1 = UserClient.userCreate(user);
        userAccessToken = UserClient.getUserAccessToken(newUser1);

        Response newUser2 = UserClient.userCreate(user);
        UserClient.checkResponseError(newUser2, SC_FORBIDDEN, false, "User already exists");
    }

    @Feature(value = "api/auth/register")
    @Test
    @DisplayName("Проверка создания пользователя без заполнения поля name.")
    @Description("Тест проверяет, что нельзя создать пользователя, если не заполнено обязательное поле name. Возвращается ошибка.")
    public void userCanNotBeCreatedWithoutNameTest() {
        Response newUser = UserClient.userCreate(new User("", user.getEmail(), user.getPassword()));

        UserClient.checkResponseError(newUser, SC_FORBIDDEN, false,"Email, password and name are required fields");
    }

    @Feature(value = "api/auth/register")
    @Test
    @DisplayName("Проверка создания пользователя без заполнения поля email.")
    @Description("Тест проверяет, что нельзя создать пользователя, если не заполнено обязательное поле email. Возвращается ошибка.")
    public void userCanNotBeCreatedWithoutEmailTest() {
        Response newUser = UserClient.userCreate(new User(user.getName(), "", user.getPassword()));
        UserClient.checkResponseError(newUser, SC_FORBIDDEN, false, "Email, password and name are required fields");
    }

    @Feature(value = "api/auth/register")
    @Test
    @DisplayName("Проверка создания пользователя без заполнения поля password.")
    @Description("Тест проверяет, что нельзя создать пользователя, если не заполнено обязательное поле password. Возвращается ошибка.")
    public void userCanNotBeCreatedWithoutPasswordTest() {
        Response newUser = UserClient.userCreate(new User(user.getName(), user.getEmail(), ""));
        UserClient.checkResponseError(newUser, SC_FORBIDDEN, false,"Email, password and name are required fields");
    }
}