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

import static stellarburgers.user.UserGenerator.*;
import static org.apache.http.HttpStatus.*;

public class UserUpdateTest {
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

    @Feature(value = "api/auth/user")
    @Test
    @DisplayName("Проверка изменения поля name у авторизованного пользователя.")
    @Description("Тест проверяет, что у авторизованного пользователя можно изменить поле name.")
    public void userCanBeUpdateNameTest() {
        UserClient.userCreate(user);
        Response userLogin = UserClient.userLogin(user);
        userAccessToken = UserClient.getUserAccessToken(userLogin);

        Response userUpdate = UserClient.setUserUpdate(new User(randomName(), user.getEmail(), user.getPassword()), userAccessToken);
        UserClient.checkResponseUserUpdate(userUpdate, SC_OK, true);
    }

    @Feature(value = "api/auth/user")
    @Test
    @DisplayName("Проверка изменения поля email у авторизованного пользователя.")
    @Description("Тест проверяет, что у авторизованного пользователя можно изменить поле email.")
    public void userCanBeUpdateEmailTest() {
        UserClient.userCreate(user);
        Response userLogin = UserClient.userLogin(user);
        userAccessToken = UserClient.getUserAccessToken(userLogin);

        Response userUpdate = UserClient.setUserUpdate(new User(user.getName(), randomName() + "@mail.ru", user.getPassword()), userAccessToken);
        UserClient.checkResponseUserUpdate(userUpdate, SC_OK, true);
    }

    @Feature(value = "api/auth/user")
    @Test
    @DisplayName("Проверка изменения email авторизованного пользователя на уже используемый другим пользователем.")
    @Description("Тест проверяет, что у авторизованного пользователя нельзя изменить email, если такой уже используется.")
    public void userCanBeUpdateDoubleEmailTest() {
        UserClient.userCreate(user);
        Response userLogin = UserClient.userLogin(user);
        userAccessToken = UserClient.getUserAccessToken(userLogin);

        User user2 = randomeUser();
        UserClient.userCreate(user2);

        Response userUpdate = UserClient.setUserUpdate(new User(user.getName(), user2.getEmail(), user.getPassword()), userAccessToken);
        UserClient.checkResponseError(userUpdate, SC_FORBIDDEN, false, "User with such email already exists");
    }

    @Feature(value = "api/auth/user")
    @Test
    @DisplayName("Проверка изменения поля password у авторизованного пользователя.")
    @Description("Тест проверяет, что у авторизованного пользователя можно изменить поле password.")
    public void userCanBeUpdatePassTest() {
        UserClient.userCreate(user);
        Response userLogin = UserClient.userLogin(user);
        userAccessToken = UserClient.getUserAccessToken(userLogin);

        Response userUpdate = UserClient.setUserUpdate(new User(user.getName(), user.getEmail(), randomPassword()), userAccessToken);
        UserClient.checkResponseUserUpdate(userUpdate, SC_OK, true);
    }

    @Feature(value = "api/auth/user")
    @Test
    @DisplayName("Проверка изменения поля name у неавторизованного пользователя.")
    @Description("Тест проверяет, что у неавторизованного пользователя нельзя изменить поле name. Возвращается ошибка.")
    public void userWithoutAuthCanNotBeUpdateNameTest() {
        Response newUser = UserClient.userCreate(user);
        userAccessToken = UserClient.getUserAccessToken(newUser);

        Response userUpdate = UserClient.setUserUpdateWithoutAuth(new User(randomName(), user.getEmail(), user.getPassword()));
        UserClient.checkResponseError(userUpdate, SC_UNAUTHORIZED, false, "You should be authorised");
    }

    @Feature(value = "api/auth/user")
    @Test
    @DisplayName("Проверка изменения поля email у неавторизованного пользователя.")
    @Description("Тест проверяет, что у неавторизованного пользователя нельзя изменить поле email. Возвращается ошибка.")
    public void userWithoutAuthCanNotBeUpdateEmailTest() {
        Response newUser = UserClient.userCreate(user);
        userAccessToken = UserClient.getUserAccessToken(newUser);

        Response userUpdate = UserClient.setUserUpdateWithoutAuth(new User(user.getName(), randomName() + "@mail.ru", user.getPassword()));
        UserClient.checkResponseError(userUpdate, SC_UNAUTHORIZED, false, "You should be authorised");
    }

    @Feature(value = "api/auth/user")
    @Test
    @DisplayName("Проверка изменения поля password у неавторизованного пользователя.")
    @Description("Тест проверяет, что у неавторизованного пользователя нельзя изменить поле password. Возвращается ошибка.")
    public void userWithoutAuthCanNotBeUpdatePassTest() {
        Response newUser = UserClient.userCreate(user);
        userAccessToken = UserClient.getUserAccessToken(newUser);

        Response userUpdate = UserClient.setUserUpdateWithoutAuth(new User(user.getName(), user.getEmail(), randomPassword()));
        UserClient.checkResponseError(userUpdate, SC_UNAUTHORIZED, false, "You should be authorised");
    }
}