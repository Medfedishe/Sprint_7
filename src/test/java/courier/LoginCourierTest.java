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

import static org.hamcrest.Matchers.*;

public class LoginCourierTest {

    private CourierClient courierClient;
    private Courier courier;
    private int courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = CourierGenerator.getRandomCourier();
        courierClient.createCourier(courier);
    }

    @After
    public void tearDown() {
        if (courierId != 0) {
            courierClient.deleteCourier(courierId);
        } else {
            Response loginResponse = courierClient.loginCourier(CourierCredentials.from(courier));
            if (loginResponse.statusCode() == HttpStatus.SC_OK) {
                int id = loginResponse.path("id");
                courierClient.deleteCourier(id);
            }
        }
    }

    @Test
    @DisplayName("Успешный логин курьера")
    @Description("Курьер может авторизоваться, возвращается статус 200 и непустой id")
    public void courierCanLoginSuccessfully() {
        Response response = courierClient.loginCourier(CourierCredentials.from(courier));
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .body("id", notNullValue());

        courierId = response.path("id");
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("При неверном пароле возвращается статус 404 и соответствующее сообщение")
    public void courierCannotLoginWithWrongPassword() {
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), "WrongPassword");
        Response response = courierClient.loginCourier(credentials);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин с несуществующим логином")
    @Description("При логине несуществующего курьера возвращается статус 404")
    public void nonExistentCourierCannotLogin() {
        CourierCredentials credentials = new CourierCredentials("non_existent_user_" + System.currentTimeMillis(), courier.getPassword());
        Response response = courierClient.loginCourier(credentials);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин без логина")
    @Description("Если не передать логин, возвращается статус 400")
    public void loginWithoutLoginField() {
        CourierCredentials credentials = new CourierCredentials("", courier.getPassword());
        Response response = courierClient.loginCourier(credentials);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин без пароля")
    @Description("Если не передать пароль, возвращается статус 400")
    public void loginWithoutPasswordField() {
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), "");
        Response response = courierClient.loginCourier(credentials);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }
}
