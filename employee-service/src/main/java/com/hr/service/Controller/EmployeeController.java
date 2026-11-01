package com.hr.service.Controller;

import com.hr.service.Entity.Employee;
import com.hr.service.Service.EmployeeService;
import jakarta.validation.Valid;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@Validated
public class EmployeeController {

    private final EmployeeService service;
    public EmployeeController(EmployeeService service){
        this.service = service;
    }



    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id){

          return   service.getEmployeeById(id)
                  .map(ResponseEntity :: ok)
                  .orElseGet( () -> ResponseEntity.notFound().build());
    }

    @PostMapping()
    public ResponseEntity<Employee> createEmployee(
            @Valid @RequestBody Employee employee){
              Employee saved=   service.createEmployee(employee);
        return  ResponseEntity.ok(saved);
    }

//    GET /api/employees?page=0&size=5&sortBy=name&direction=asc
    @GetMapping()
    public ResponseEntity<Page<Employee>> getAllEmployees(
            @RequestParam(defaultValue = "0")
            @Min(value = 0 , message = "Page number must be greater than 0")
            int page,

            @RequestParam(defaultValue = "5")
            @Min(value = 1 , message = "Size must be greater than 1")
            @Max(value = 10, message = "Size must not exceed 10")
            int size,


            @RequestParam(defaultValue = "name")
            String sortBy,
            @RequestParam(defaultValue = "asc") String direction
            ){

        List<String> allowedSort = List.of("id", "name", "email", "department");

        if (!allowedSort.contains(sortBy.toLowerCase())){
            throw new IllegalArgumentException(
                    "Invalid sortBy field : " + sortBy
            );
        }
        if (!direction.equalsIgnoreCase("asc")
                && !direction.equalsIgnoreCase("desc")){
            throw new IllegalArgumentException("Direction must be 'asc' or 'desc' " + direction);
        }

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page ,size, sort);

        return ResponseEntity.ok(
                 service.getAllEmployees(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id,
                                                   @RequestBody Employee employee){
        return service.updateEmployee(employee, id)
                .map(ResponseEntity::ok)
                .orElseGet(()-> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable Long id
    ){
        if (service.deleteEmployee(id))
        return ResponseEntity.noContent().build();
        else
            return ResponseEntity.notFound().build();
    }
}
