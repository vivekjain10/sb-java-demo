package com.example.demo.payroll;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private EmployeeRepository employeeRepository;

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
                .body("_links.self.href", endsWith("/employees"));
    }

    @Test
    void createEmployee() throws Exception {
        Path newEmployeeJsonPath = Paths.get("src/test/resources/data/new-employee.json");
        String newEmployeeJson = new String(Files.readAllBytes(newEmployeeJsonPath));

        assertThat(employeeRepository.count()).isEqualTo(2);

        RestAssured.given()
                .body(newEmployeeJson)
                .contentType(ContentType.JSON)
                .when()
                .post()
                .then()
                .log().ifValidationFails()
                .statusCode(HttpStatus.CREATED.value());

        assertThat(employeeRepository.count()).isEqualTo(3);
    }
}
