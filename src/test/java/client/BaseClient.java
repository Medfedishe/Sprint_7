package client;

import config.ApiConfig;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class BaseClient {

    protected RequestSpecification getBaseSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ApiConfig.BASE_URL)
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .build();
    }
}