package stellarburgers.order_tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import stellarburgers.ingredient.IngredientClient;
import stellarburgers.ingredient.IngredientData;
import stellarburgers.order.Order;
import stellarburgers.order.OrderClient;
import stellarburgers.user.User;
import stellarburgers.user.UserClient;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static stellarburgers.user.UserGenerator.*;

public class OrderCreateTest {
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

    @Feature(value = "api/orders")
    @Test
    @DisplayName("Проверка создания авторизованным пользователем заказа с ингредиентами.")
    @Description("Тест проверяет, что авторизованный пользователь может создать заказ с ингредиентами.")
    public void orderWithIngredientCanBeCreateAuthUserTest() {
        UserClient.userCreate(user);
        Response userLogin = UserClient.userLogin(user);
        userAccessToken = UserClient.getUserAccessToken(userLogin);

        List<IngredientData> ingredientList = IngredientClient.getIngredientList();
        List<String> ingredients = List.of(
                ingredientList.get(0).get_id(),
                ingredientList.get(1).get_id(),
                ingredientList.get(2).get_id());

        Response orderCreate = OrderClient.orderCreate(new Order(ingredients), userAccessToken);
        OrderClient.checkResponseOrderCreate(orderCreate, SC_OK, true);
    }

    @Feature(value = "api/orders")
    @Test
    @DisplayName("Проверка создания авторизованным пользователем заказа без ингредиентов.")
    @Description("Тест проверяет, что авторизованный пользователь не может создать заказ без ингредиентов. Возвращается ошибка")
    public void orderWithoutIngredientCanNotBeCreateAuthUserTest() {
        UserClient.userCreate(user);
        Response userLogin = UserClient.userLogin(user);
        userAccessToken = UserClient.getUserAccessToken(userLogin);

        List<String> ingredients = List.of();
        Response orderCreate = OrderClient.orderCreate(new Order(ingredients), userAccessToken);
        OrderClient.checkResponseWithInvalidData(orderCreate, SC_BAD_REQUEST, false, "Ingredient ids must be provided");
    }

    @Feature(value = "api/orders")
    @Test
    @DisplayName("Проверка создания авторизованным пользователем заказа с неверным хешем ингредиентов.")
    @Description("Тест проверяет, что авторизованный пользователь не может создать заказ с неверным хешем ингредиентов. Возвращается ошибка 500")
    public void orderWithInvalidIngredientCanNotBeCreateAuthUserTest() {
        UserClient.userCreate(user);
        Response userLogin = UserClient.userLogin(user);
        userAccessToken = UserClient.getUserAccessToken(userLogin);

        List<String> ingredients = List.of(randomNumber());
        Response orderCreate = OrderClient.orderCreate(new Order(ingredients), userAccessToken);
        OrderClient.checkResponseErrorOrderCreate(orderCreate, SC_INTERNAL_SERVER_ERROR);
    }

    @Feature(value = "api/orders")
    @Test
    @DisplayName("Проверка создания неавторизованным пользователем заказа с ингредиентами.")
    @Description("Тест проверяет, что неавторизованный пользователь не может создать заказ с ингредиентами.")
    public void orderWithIngredientCanBeCreateWithoutAuthTest() {
        Response newUser = UserClient.userCreate(user);
        userAccessToken = UserClient.getUserAccessToken(newUser);

        List<IngredientData> ingredientList = IngredientClient.getIngredientList();
        List<String> ingredients = List.of(
                ingredientList.get(0).get_id(),
                ingredientList.get(1).get_id(),
                ingredientList.get(2).get_id());

        Response orderCreate = OrderClient.orderCreateWithoutAuth(new Order(ingredients));
        OrderClient.checkResponseOrderCreate(orderCreate, SC_OK, true);
    }

    @Feature(value = "api/orders")
    @Test
    @DisplayName("Проверка создания неавторизованным пользователем заказа без ингредиентов.")
    @Description("Тест проверяет, что неавторизованный пользователь не может создать заказ без ингредиентов. Возвращается ошибка")
    public void orderWithoutIngredientCanNotBeCreateWithoutAuthTest() {
        Response newUser = UserClient.userCreate(user);
        userAccessToken = UserClient.getUserAccessToken(newUser);

        List<String> ingredients = List.of();
        Response orderCreate = OrderClient.orderCreateWithoutAuth(new Order(ingredients));
        OrderClient.checkResponseWithInvalidData(orderCreate, SC_BAD_REQUEST, false, "Ingredient ids must be provided");
    }
}