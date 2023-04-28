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
import static stellarburgers.user.UserGenerator.randomeUser;

public class OrderListTest {
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
    @DisplayName("Проверка получения списка заказов авторизованным пользователем.")
    @Description("Тест проверяет, что авторизованный пользователь может получить список заказов.")
    public void orderListCanBeGetAuthUserTest() {
        UserClient.userCreate(user);
        Response userLogin = UserClient.userLogin(user);
        userAccessToken = UserClient.getUserAccessToken(userLogin);

        List<IngredientData> ingredientList = IngredientClient.getIngredientList();
        List<String> ingredients = List.of(
                ingredientList.get(0).get_id(),
                ingredientList.get(1).get_id(),
                ingredientList.get(2).get_id());

        OrderClient.orderCreate(new Order(ingredients), userAccessToken);
        Response orderList = OrderClient.getAuthUserOrdersList(userAccessToken);
        OrderClient.checkResponseGetOrdersList(orderList, SC_OK, true);
    }

    @Feature(value = "api/orders")
    @Test
    @DisplayName("Проверка получения списка заказов неавторизованным пользователем.")
    @Description("Тест проверяет, что неавторизованный пользователь не может получить список заказов. Возвращается ошибка.")
    public void orderListCanNotBeGetUserWithoutAuthTest() {
        Response newUser = UserClient.userCreate(user);
        userAccessToken = UserClient.getUserAccessToken(newUser);

        Response orderList = OrderClient.getUserOrdersListWithoutAuth();
        OrderClient.checkResponseWithInvalidData(orderList, SC_UNAUTHORIZED, false, "You should be authorised");
    }
}
