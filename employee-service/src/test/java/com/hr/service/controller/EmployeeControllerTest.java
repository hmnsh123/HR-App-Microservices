package com.hr.service.controller;

import com.hr.service.Controller.EmployeeController;
//import com.hr.service.Entity.Employee;
import com.hr.service.Entity.Employee;
import com.hr.service.Service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    // 1. GET employee: 200 OK
    @Test
    void getEmployeeById_shouldReturn200_whenEmployeeExists()
            throws Exception {

        Employee employee = createEmployee();

        when(employeeService.getEmployeeById(1L))
                .thenReturn(Optional.of(employee));

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Rahul"))
                .andExpect(jsonPath("$.email")
                        .value("rahul@example.com"))
                .andExpect(jsonPath("$.department").value("IT"));
    }

    // 2. GET employee: 404 Not Found
    @Test
    void getEmployeeById_shouldReturn404_whenEmployeeDoesNotExist()
            throws Exception {

        when(employeeService.getEmployeeById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isNotFound());
    }

    // 3. POST employee: 201 Created
    @Test
    void createEmployee_shouldReturn201_whenEmployeeIsValid()
            throws Exception {

        Employee employee = createEmployee();

        when(employeeService.createEmployee(any(Employee.class)))
                .thenReturn(employee);

        mockMvc.perform(post("/api/employees")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "Rahul",
                                  "email": "rahul@example.com",
                                  "department": "IT"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Rahul"))
                .andExpect(jsonPath("$.email")
                        .value("rahul@example.com"));
    }

    // 4. POST employee: invalid input returns 400
    @Test
    void createEmployee_shouldReturn400_whenNameIsMissing()
            throws Exception {

        mockMvc.perform(post("/api/employees")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "",
                                  "email": "rahul@example.com",
                                  "department": "IT"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    // 5. GET all employees: pagination
    @Test
    void getAllEmployees_shouldReturnPaginatedEmployees()
            throws Exception {

        Employee employee = createEmployee();

        Page<Employee> employeePage = new PageImpl<>(
                List.of(employee),
                org.springframework.data.domain.PageRequest.of(0, 5),
                1
        );

        when(employeeService.getAllEmployees(any(Pageable.class)))
                .thenReturn(employeePage);

        mockMvc.perform(get("/api/employees")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name")
                        .value("Rahul"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    // 6. PUT employee: successful update
    @Test
    void updateEmployee_shouldReturn200_whenEmployeeExists()
            throws Exception {

        Employee updatedEmployee = createEmployee();
        updatedEmployee.setName("Rahul Sharma");
        updatedEmployee.setEmail("rahul.sharma@example.com");
        updatedEmployee.setDepartment("HR");

        when(employeeService.updateEmployee(
               any(Employee.class),eq(1L)))
                .thenReturn(Optional.of(updatedEmployee));

        mockMvc.perform(put("/api/employees/1")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "Rahul Sharma",
                                  "email": "rahul.sharma@example.com",
                                  "department": "HR"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Rahul Sharma"))
                .andExpect(jsonPath("$.department").value("HR"));
    }

    // 7. PUT employee: 404 Not Found
    @Test
    void updateEmployee_shouldReturn404_whenEmployeeDoesNotExist()
            throws Exception {

        when(employeeService.updateEmployee(
                any(Employee.class), eq(1L)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/employees/99")
                        .contentType("application/json")
                        .content("""
                                {
                                  "name": "Amit",
                                  "email": "amit@example.com",
                                  "department": "HR"
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    // 8. DELETE employee: 204 No Content
    @Test
    void deleteEmployee_shouldReturn204_whenEmployeeExists()
            throws Exception {

        when(employeeService.deleteEmployee(1L))
                .thenReturn(true);

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());
    }

    // 9. DELETE employee: 404 Not Found
    @Test
    void deleteEmployee_shouldReturn404_whenEmployeeDoesNotExist()
            throws Exception {

        when(employeeService.deleteEmployee(99L))
                .thenReturn(false);

        mockMvc.perform(delete("/api/employees/99"))
                .andExpect(status().isNotFound());
    }

    // Common test data
    private Employee createEmployee() {

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("Rahul");
        employee.setEmail("rahul@example.com");
        employee.setDepartment("IT");

        return employee;
    }
}