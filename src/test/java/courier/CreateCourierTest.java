package courier;

import client.CourierClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.Courier;
import model.CourierCredentials;
import model.CourierGenerator;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CreateCourierTest {

    private CourierClient courierClient;
    private Courier courier;
    private int courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = CourierGenerator.getRandomCourier();
        courierId = 0;
    }

    @After
    public void tearDown() {
        if (courierId != 0) {
            courierClient.deleteCourier(courierId);
        } else if (courier != null && courier.getLogin() != null && courier.getPassword() != null) {
            Response loginResponse = courierClient.loginCourier(CourierCredentials.from(courier));
            if (loginResponse.statusCode() == HttpStatus.SC_OK) {
                int id = loginResponse.path("id");
                courierClient.deleteCourier(id);
            }
        }
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Проверка, что курьера можно создать и возвращается статус 201 с ok: true")
    public void courierCanBeCreated() {
        Response response = courierClient.createCourier(courier);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("ok", is(true));

        Response loginResponse = courierClient.loginCourier(CourierCredentials.from(courier));
        courierId = loginResponse.path("id");
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Description("Проверка, что при попытке создать курьера с существующим логином возвращается 409")
    public void cannotCreateDuplicateCourier() {
        courierClient.createCourier(courier);

        Response duplicateResponse = courierClient.createCourier(courier);
        duplicateResponse.then()
                .assertThat()
                .statusCode(HttpStatus.SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Создание курьера без логина")
    @Description("Проверка, что при отсутствии поля login возвращается 400")
    public void cannotCreateCourierWithoutLogin() {
        courier.setLogin(null);
        Response response = courierClient.createCourier(courier);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    @Description("Проверка, что при отсутствии поля password возвращается 400")
    public void cannotCreateCourierWithoutPassword() {
        courier.setPassword(null);
        Response response = courierClient.createCourier(courier);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Успешное создание курьера без firstName")
    @Description("Поле firstName опциональное, курьер должен создаваться успешно")
    public void courierCanBeCreatedWithoutFirstName() {
        courier.setFirstName(null);
        Response response = courierClient.createCourier(courier);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("ok", is(true));

        Response loginResponse = courierClient.loginCourier(CourierCredentials.from(courier));
        courierId = loginResponse.path("id");
    }
}
