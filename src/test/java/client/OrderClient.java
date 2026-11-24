package client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends BaseClient {

    private static final String ORDERS_PATH = "/api/v1/orders";

    @Step("Создание заказа (POST /api/v1/orders)")
    public Response createOrder(Order order) {
        return given()
                .spec(getBaseSpec())
                .body(order)
                .when()
                .post(ORDERS_PATH);
    }

    @Step("Получение списка заказов (GET /api/v1/orders)")
    public Response getOrdersList() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(ORDERS_PATH);
    }

    @Step("Отмена заказа по трек-номеру (PUT /api/v1/orders/cancel)")
    public Response cancelOrder(int track) {
        return given()
                .spec(getBaseSpec())
                .queryParam("track", track)
                .when()
                .put(ORDERS_PATH + "/cancel");
    }
}
