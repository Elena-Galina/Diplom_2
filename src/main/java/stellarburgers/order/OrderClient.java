package stellarburgers.order;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static stellarburgers.BaseSpecification.*;

public class OrderClient {

    @Step("Передать запрос на создание заказа авторизованным пользователем (\"api/orders\")")
    public static Response orderCreate(Order order, String userAccessToken) {
        return given()
                .spec(getBaseSpecification())
                .auth().oauth2(userAccessToken)
                .body(order)
                .when()
                .post(PATH_ORDER);
    }

    @Step("Передать запрос на создание заказа неавторизованным пользователем (\"api/orders\")")
    public static Response orderCreateWithoutAuth(Order order) {
        return given()
                .spec(getBaseSpecification())
                .body(order)
                .when()
                .post(PATH_ORDER);
    }

    @Step("Проверить статус кода и тело ответа.")
    public static void checkResponseOrderCreate(Response orderCreate, int code, boolean success) {
        orderCreate.then().assertThat()
                .statusCode(code)
                .and()
                .body("success", equalTo(success))
                .body("name", notNullValue())
                .body("order", notNullValue());
    }

    @Step("Проверить статус кода (500).")
    public static void checkResponseErrorOrderCreate(Response orderCreate, int code) {
        orderCreate.then().assertThat().statusCode(code);
    }

    @Step("Проверить статус кода (4хх) и тело ответа при передаче некорректных данных.")
    public static void checkResponseWithInvalidData(Response orderCreate, int code, boolean success, String message) {
        orderCreate.then().assertThat()
                .statusCode(code)
                .and()
                .body("success", equalTo(success))
                .body("message", equalTo(message));
    }

    @Step("Передать запрос на получение списка заказов конкретного авторизованного пользователя (\"api/orders\")")
    public static Response getAuthUserOrdersList(String userAccessToken) {
        return given()
                .spec(getBaseSpecification())
                .auth().oauth2(userAccessToken)
                .when()
                .get(PATH_ORDER);
    }

    @Step("Передать запрос на получение списка заказов неавторизованного пользователя (\"api/orders\")")
    public static Response getUserOrdersListWithoutAuth() {
        return given()
                .spec(getBaseSpecification())
                .when()
                .get(PATH_ORDER);
    }

    @Step("Проверить статус кода (200) и тело ответа при получении списка заказов.")
    public static void checkResponseGetOrdersList(Response ordersList, int code, boolean success) {
        ordersList.then().assertThat()
                .statusCode(code)
                .and()
                .body("success", equalTo(success))
                .body("orders", notNullValue())
                .body("total", notNullValue())
                .body("totalToday", notNullValue());
    }
}

