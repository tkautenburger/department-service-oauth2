package de.legendlime.departmentService.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import de.legendlime.departmentService.domain.Department;

public interface DepartmentRepository extends CrudRepository<Department, Long> {
	
	public List<Department> findAll();

}
