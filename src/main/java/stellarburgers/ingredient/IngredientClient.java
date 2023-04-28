package stellarburgers.ingredient;

import io.qameta.allure.Step;

import java.util.List;

import static io.restassured.RestAssured.given;
import static stellarburgers.BaseSpecification.*;

public class IngredientClient {
    @Step("Передать запрос на получение списка ingredient (\"api/ingredients\")")
    public static List<IngredientData> getIngredientList() {
        return given()
                .spec(getBaseSpecification())
                .get(PATH_INGREDIENTS)
                .body().as(IngredientList.class).getData();
    }
}
