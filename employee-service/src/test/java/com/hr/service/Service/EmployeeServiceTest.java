package com.hr.service.Service;

import com.hr.service.Entity.Employee;
import com.hr.service.GlobalException.DuplicateEmailException;
import com.hr.service.Repositary.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void getEmployeeById_shouldReturnEmployee_whenEmployeeExists() {

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("Rahul");
        employee.setEmail("rahul@example.com");
        employee.setDepartment("IT");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        Optional<Employee> result =
                employeeService.getEmployeeById(1L);

        assertTrue(result.isPresent());
        assertEquals("Rahul", result.get().getName());

        verify(employeeRepository).findById(1L);
    }


    @Test
    void getEmployeeById_shouldReturnEmpty_whenEmployeeDoesNotExist() {

        when(employeeRepository.findById(99L))
                .thenReturn(Optional.empty());

        Optional<Employee> result =
                employeeService.getEmployeeById(99L);

        assertTrue(result.isEmpty());

        verify(employeeRepository).findById(99L);
    }

    @Test
    void saveEmployee_shouldThrowException_whenEmailAlreadyExists() {

        Employee employee = new Employee();
        employee.setName("Rahul");
        employee.setEmail("rahul@example.com");
        employee.setDepartment("IT");

        when(employeeRepository.existsByEmail("rahul@example.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> employeeService.createEmployee(employee)
        );

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void saveEmployee_shouldSaveEmployee_whenEmailIsUnique() {

        Employee employee = new Employee();
        employee.setName("Amit");
        employee.setEmail("amit@example.com");
        employee.setDepartment("IT");

        when(employeeRepository.existsByEmail("amit@example.com"))
                .thenReturn(false);

        when(employeeRepository.save(employee))
                .thenReturn(employee);

        Employee result = employeeService.createEmployee(employee);

        assertNotNull(result);
        assertEquals("Amit", result.getName());
        assertEquals("amit@example.com", result.getEmail());

        verify(employeeRepository).existsByEmail("amit@example.com");
        verify(employeeRepository).save(employee);
    }

    @Test
    void updateEmployee_shouldUpdateEmployee_whenEmployeeExists(){

        Employee existingEmp = new Employee();
        existingEmp.setId(1L);
        existingEmp.setName("Rahul");
        existingEmp.setEmail("rahul@example.com");
        existingEmp.setDepartment("IT");

        Employee updateEmp = new Employee();
        updateEmp.setName("Rahul Sharma");
        updateEmp.setEmail("rahul.sharma@example.com");
        updateEmp.setDepartment("HR");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(existingEmp));

        when(employeeRepository.existsByEmailAndIdNot("rahul.sharma@example.com", 1L))
                .thenReturn(false);

        when(employeeRepository.save(existingEmp))
                .thenReturn(existingEmp);

        Optional<Employee> result =
                employeeService.updateEmployee(updateEmp, 1L);

        assertTrue(result.isPresent());
        assertEquals("Rahul Sharma" , result.get().getName());
        assertEquals("rahul.sharma@example.com", result.get().getEmail());
        assertEquals("HR", result.get().getDepartment());

        verify(employeeRepository).save(existingEmp);
    }

    @Test
    void updateEmployee_shouldReturnEmpty_whenEmployeeDoesNotExist() {

        Employee updatedEmployee = new Employee();
        updatedEmployee.setName("Amit");
        updatedEmployee.setEmail("amit@example.com");
        updatedEmployee.setDepartment("HR");

        when(employeeRepository.findById(99L))
                .thenReturn(Optional.empty());

        Optional<Employee> result =
                employeeService.updateEmployee( updatedEmployee, 99L);

        assertTrue(result.isEmpty());

        verify(employeeRepository).findById(99L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void updateEmployee_shouldThrowException_whenEmailBelongsToAnotherEmployee() {

        Employee existingEmployee = new Employee();
        existingEmployee.setId(1L);
        existingEmployee.setName("Rahul");
        existingEmployee.setEmail("rahul@example.com");
        existingEmployee.setDepartment("IT");

        Employee updatedEmployee = new Employee();
        updatedEmployee.setName("Rahul Sharma");
        updatedEmployee.setEmail("amit@example.com");
        updatedEmployee.setDepartment("HR");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(existingEmployee));

        when(employeeRepository.existsByEmailAndIdNot(
                "amit@example.com", 1L))
                .thenReturn(true);

        assertThrows(
                DuplicateEmailException.class,
                () -> employeeService.updateEmployee( updatedEmployee, 1L)
        );

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void deleteEmployee_shouldDeleteEmployee_whenEmployeeExists() {

        when(employeeRepository.existsById(1L))
                .thenReturn(true);

        boolean result = employeeService.deleteEmployee(1L);

        assertTrue(result);

        verify(employeeRepository).existsById(1L);
        verify(employeeRepository).deleteById(1L);
    }

    @Test
    void deleteEmployee_shouldReturnFalse_whenEmployeeDoesNotExist() {

        when(employeeRepository.existsById(99L))
                .thenReturn(false);

        boolean result = employeeService.deleteEmployee(99L);

        assertFalse(result);

        verify(employeeRepository).existsById(99L);
        verify(employeeRepository, never()).deleteById(99L);
    }

    @Test
    void getAllEmployees_shouldReturnPaginatedEmployees() {

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("Rahul");
        employee.setEmail("rahul@example.com");
        employee.setDepartment("IT");

        Pageable pageable = PageRequest.of(0, 5);

        Page<Employee> employeePage =
                new PageImpl<>(List.of(employee), pageable, 1);

        when(employeeRepository.findAll(pageable))
                .thenReturn(employeePage);

        Page<Employee> result =
                employeeService.getAllEmployees(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("Rahul", result.getContent().get(0).getName());

        verify(employeeRepository).findAll(pageable);
    }
}