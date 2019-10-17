package de.legendlime.departmentService.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.legendlime.departmentService.config.Secrets;
import de.legendlime.departmentService.domain.Department;
import de.legendlime.departmentService.domain.DepartmentDTO;
import de.legendlime.departmentService.repository.DepartmentRepository;

@RestController
@RequestMapping(value = "v1")
public class DepartmentController {
	
	private static final String NOT_FOUND = "Department not found, ID: ";
	private static final String NOT_NULL = "Department cannot be null";

	@Autowired
	private DepartmentRepository repo;
	
	@Autowired 
	VaultTemplate vaultTemplate;
	
	@Autowired
	DataSourceProperties dsProperties;

	@GetMapping(value = "/departments", 
			    produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Department> getAll() {

		return repo.findAll();
	}

	@GetMapping(value = "/department/{id}", 
			    produces = MediaType.APPLICATION_JSON_VALUE)
	public Department getSingle(@PathVariable(name = "id", required = true) Long id) {

		return repo.findById(id).orElseThrow(() -> 
		  new ResourceNotFoundException(NOT_FOUND + id));
	}

	@PostMapping(value = "/departments", 
			     consumes = MediaType.APPLICATION_JSON_VALUE, 
			     produces = MediaType.APPLICATION_JSON_VALUE)
	public Department create(@Valid @RequestBody DepartmentDTO dept) {

		if (dept == null)
			throw new IllegalArgumentException(NOT_NULL);
		
		// Use DTO to avoid security vulnerability 
		// Persistent entities should not be used as arguments of "@RequestMapping" methods
		Department persistentDept = new Department();
		persistentDept.setDeptId(dept.getDeptId());
		persistentDept.setName(dept.getName());
		persistentDept.setDescription(dept.getDescription());

		return repo.save(persistentDept);
	}

	@PutMapping(value = "/department/{id}", 
			    consumes = MediaType.APPLICATION_JSON_VALUE, 
			    produces = MediaType.APPLICATION_JSON_VALUE)
	public Department update(@Valid @RequestBody DepartmentDTO dept, 
			                 @PathVariable(name = "id", required = true) Long id) {
		
		Optional<Department> deptOpt = repo.findById(id);
		if (!deptOpt.isPresent())
			throw new ResourceNotFoundException(NOT_FOUND + id);
		Department d = deptOpt.get();
		d.setDeptId(dept.getDeptId());
		d.setName(dept.getName());
		d.setDescription(dept.getDescription());
		return repo.save(d);
	}
	
	@DeleteMapping(value = "/department/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> delete(@PathVariable(name = "id", required = true) Long id) {

		Optional<Department> deptOpt = repo.findById(id);
		if (!deptOpt.isPresent())
			throw new ResourceNotFoundException(NOT_FOUND + id);
		repo.delete(deptOpt.get());
		return ResponseEntity.ok().build();
	}
	
    @GetMapping("/getConfigFromVault")
    public String getConfigFromProperty() throws JsonProcessingException {
    	// this is only to check if the secret configuration 
    	// properties really come from Vault and not from the configuration
    	VaultResponseSupport<Secrets> response = 
    			vaultTemplate.read("secret/DepartmentService", Secrets.class);
    	StringBuffer buf = new StringBuffer();
    	buf.append("\nUsername: " + response.getData().getUsername() + 
    			   "\nPassword: " + response.getData().getPassword());
    	
    	// ObjectMapper mapper = new ObjectMapper();
    	// String jsonString = mapper.writeValueAsString(dsProperties);
    	buf.append("\n Datasource Username: " + dsProperties.getUsername() + 
    	           "\nDatasource Password: " + dsProperties.getPassword());
    	return buf.toString();
    }
   

}
