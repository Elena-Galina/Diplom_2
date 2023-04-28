package stellarburgers;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class BaseSpecification {

    public static String BASE_URI = "https://stellarburgers.nomoreparties.site/";
    public static String PATH_USER = "api/auth/register";
    public static String PATH_GET_UPDATE_DELETE_USER = "api/auth/user";
    public static String PATH_USER_LOGIN = "api/auth/login";
    public static String PATH_INGREDIENTS = "api/ingredients";
    public static String PATH_ORDER = "api/orders";

    public static RequestSpecification getBaseSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setContentType(ContentType.JSON)
                //  .log(LogDetail.ALL)
                .build();
    }
}
