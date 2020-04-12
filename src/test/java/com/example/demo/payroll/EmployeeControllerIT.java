package com.example.demo.payroll;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerIT {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/employees";
    }

    @Test
    void getEmployees() {
        RestAssured.when()
                .get()
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.OK.value())
                .body("_embedded.employeeList.size()", equalTo(2))
                .body("_embedded.employeeList[0].id", equalTo(1))
                .body("_embedded.employeeList[0].name", equalTo("Bilbo Baggins"))
                .body("_embedded.employeeList[0].role", equalTo("burglar"))
                .body("_embedded.employeeList[0]._links.self.href", endsWith("/employees/1"))
                .body("_embedded.employeeList[0]._links.employees.href", endsWith("/employees"))
                .body("_links.self.href", endsWith("/employees"))
        ;
    }
}
