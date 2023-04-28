package stellarburgers.user;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static stellarburgers.BaseSpecification.*;

public class UserClient {
    static String userAccessToken;

    @Step("Передать запрос на создание пользователя (\"api/auth/register\")")
    public static Response userCreate(User user) {
        return given()
                .spec(getBaseSpecification())
                .body(user)
                .when()
                .post(PATH_USER);
    }

    @Step("Передать запрос на авторизацию пользователя (\"api/auth/login\")")
    public static Response userLogin(User user) {
        return given()
                .spec(getBaseSpecification())
                .body(user)
                .when()
                .post(PATH_USER_LOGIN);
    }

    @Step("Проверить статус кода и тело ответа.")
    public static void checkResponseUserCreateOrLogin(Response newUser, int code, boolean success) {
        newUser.then().assertThat()
                .statusCode(code)
                .and()
                .body("success", equalTo(success))
                .body("user", notNullValue())
                .body("accessToken", containsString("Bearer"))
                .body("refreshToken", notNullValue());
    }

    @Step("Получить accessToken созданного пользователя.")
    public static String getUserAccessToken(Response newUser) {
        String accessToken =
                newUser.then()
                        .extract()
                        .path("accessToken");
        String[] accessTokenWithoutBearer = accessToken.split(" ");
        userAccessToken = accessTokenWithoutBearer[1];
        return userAccessToken;
    }

    @Step("Передать запрос на изменение данных авторизованного пользователя (\"api/auth/user\")")
    public static Response setUserUpdate(User userUpdate, String userAccessToken) {
        return given()
                .spec(getBaseSpecification())
                .auth().oauth2(userAccessToken)
                .body(userUpdate)
                .when()
                .patch(PATH_GET_UPDATE_DELETE_USER);
    }

    @Step("Проверить статус кода и тело ответа при изменении данных")
    public static void checkResponseUserUpdate(Response userUpdate, int code, boolean success) {
        userUpdate.then().assertThat()
                .statusCode(code)
                .and()
                .body("success", equalTo(success))
                .and()
                .extract()
                .path("user");
    }

    @Step("Передать запрос на изменение данных неавторизованного пользователя (\"api/auth/user\")")
    public static Response setUserUpdateWithoutAuth(User userUpdate) {
        return given()
                .spec(getBaseSpecification())
                .body(userUpdate)
                .when()
                .patch(PATH_GET_UPDATE_DELETE_USER);
    }

    @Step("Проверить статус кода (4хх) и тело ответа при передаче некорректных данных.")
    public static void checkResponseError(Response userUpdate, int code, boolean success, String message) {
        userUpdate.then().assertThat()
                .statusCode(code)
                .and()
                .body("success", equalTo(success))
                .body("message", equalTo(message));
    }

    @Step("Передать запрос на удаление пользователя (\"api/auth/user\")")
    public static Response setUserDelete(String userAccessToken) {
        return given()
                .spec(getBaseSpecification())
                .auth().oauth2(userAccessToken)
                .when()
                .delete(PATH_GET_UPDATE_DELETE_USER);
    }

    @Step("Удалить тестовые данные после выполнения теста.")
    public static void userDeleteAfterTest(String userAccessToken, int code, boolean success, String message) {
        Response response = setUserDelete(userAccessToken);
        response.then().assertThat()
                .statusCode(code)
                .and()
                .body("success", equalTo(success))
                .body("message", equalTo(message));
    }

    @Step("Вывести в консоль ответ на успешный запрос.")
    public static void printResponseBodyToConsole(Response response) {
        System.out.println(response.body().asString());
    }
}
