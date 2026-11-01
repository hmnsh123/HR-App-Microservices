package com.hr.service.Service;

import com.hr.service.Entity.Employee;
import com.hr.service.GlobalException.DuplicateEmailException;
import com.hr.service.Repositary.EmployeeRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
//@Component
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository repository;


    public Optional<Employee> getEmployeeById(Long id) {
        return repository.findById(id);

    }

    public Employee createEmployee(Employee employee) {

        if (repository.existsByEmail(employee.getEmail())){
            throw new DuplicateEmailException("Email already exist : " + employee.getEmail());
        }

        return repository.save(employee);
    }

    public Page<Employee> getAllEmployees(Pageable pageable){
        return repository.findAll(pageable);
    }

    public Optional<Employee> updateEmployee(Employee employee , Long id){


        return repository.findById(id)
                .map(e-> {

                    if (repository.existsByEmailAndIdNot(employee.getEmail(), id)){
                        throw new DuplicateEmailException("Can't update email as email already exist : "+ employee.getEmail());
                    }

//                    e.setId(id);
                    e.setName(employee.getName());
                    e.setDepartment(employee.getDepartment());
                    e.setEmail(employee.getEmail());

                    return repository.save(e);
                });
    }

    public boolean deleteEmployee(Long id){
        if(repository.existsById(id)){
         repository.deleteById(id);
        return true;
        }
        return false;
    }


}
