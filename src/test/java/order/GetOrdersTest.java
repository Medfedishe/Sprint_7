package order;

import client.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;

public class GetOrdersTest {

    private OrderClient orderClient;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверка, что в тело ответа возвращается непустой список заказов")
    public void getOrdersListReturnsOrders() {
        Response response = orderClient.getOrdersList();
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("orders", notNullValue());
    }
}
