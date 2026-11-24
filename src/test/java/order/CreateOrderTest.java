package order;

import client.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.Order;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private OrderClient orderClient;
    private final List<String> color;
    private int track;

    public CreateOrderTest(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Тестовые данные цветов: {0}")
    public static Object[][] getColorData() {
        return new Object[][]{
                {List.of("BLACK")},
                {List.of("GREY")},
                {List.of("BLACK", "GREY")},
                {null}
        };
    }

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    @After
    public void tearDown() {
        if (track != 0) {
            orderClient.cancelOrder(track);
        }
    }

    @Test
    @DisplayName("Создание заказа с различными вариантами цветов")
    @Description("Проверяем создание заказа: черный цвет, серый цвет, оба цвета, без выбора цвета")
    public void createOrderWithDifferentColors() {
        Order order = new Order(
                "Егор",
                "Гиль",
                "г. Москва, ул. Ленина, д. 10",
                "4",
                "+79991112233",
                5,
                "2026-12-01",
                "Позвонить за час до доставки",
                color
        );

        Response response = orderClient.createOrder(order);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("track", notNullValue());

        track = response.path("track");
    }
}