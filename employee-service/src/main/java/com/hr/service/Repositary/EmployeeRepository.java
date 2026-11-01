package com.hr.service.Repositary;

import com.hr.service.Entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee , Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email , Long id);
}
