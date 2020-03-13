package de.legendlime.departmentService.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.legendlime.departmentService.config.logging.ResponseLoggingFilter;
import de.legendlime.departmentService.domain.Department;
import de.legendlime.departmentService.domain.DepartmentDTO;
import de.legendlime.departmentService.messaging.AuditRecord;
import de.legendlime.departmentService.messaging.AuditSourceBean;
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
	
	@Autowired
	AuditSourceBean audit;

	@GetMapping(value = "/departments", 
			    produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Department> getAll(HttpServletRequest request, HttpServletResponse response) {

		AuditRecord record = new AuditRecord();
		record.setMethod("GET");
		record.setUri(request.getRequestURI());
		record.setClient(request.getRemoteAddr());
		
		String user = request.getRemoteUser();
		if (user != null) {
			record.setUser(user);
		}
		HttpSession session = request.getSession(false);
		if (session != null) {
			record.setSessionId(session.getId());
		}		
		record.setTraceId(response.getHeader(ResponseLoggingFilter.TRACE_ID));
		record.setObjectType(Department.class.getName());
		record.setObjectId(0L);
		audit.publishAuditMessage(record);

		return repo.findAll();
	}

	@GetMapping(value = "/departments/{id}", 
			    produces = MediaType.APPLICATION_JSON_VALUE)
	public Department getSingle(@PathVariable(name = "id", required = true) Long id, 
			HttpServletRequest request, HttpServletResponse response) {

		Department dept = repo.findById(id).orElseThrow(() -> 
		  new ResourceNotFoundException(NOT_FOUND + id));
		
		AuditRecord record = new AuditRecord();
		record.setMethod("GET");
		record.setUri(request.getRequestURI());
		record.setClient(request.getRemoteAddr());
		
		String user = request.getRemoteUser();
		if (user != null) {
			record.setUser(user);
		}
		HttpSession session = request.getSession(false);
		if (session != null) {
			record.setSessionId(session.getId());
		}		
		record.setTraceId(response.getHeader(ResponseLoggingFilter.TRACE_ID));
		record.setObjectType(Department.class.getName());
		record.setObjectId(dept.getDeptId());
		audit.publishAuditMessage(record);

		return dept;
	}

	@PostMapping(value = "/departments", 
			     consumes = MediaType.APPLICATION_JSON_VALUE, 
			     produces = MediaType.APPLICATION_JSON_VALUE)
	public Department create(@Valid @RequestBody DepartmentDTO dept, 
			HttpServletRequest request, HttpServletResponse response) {

		if (dept == null)
			throw new IllegalArgumentException(NOT_NULL);
		
		// Use DTO to avoid security vulnerability 
		// Persistent entities should not be used as arguments of "@RequestMapping" methods
		Department persistentDept = new Department();
		persistentDept.setDeptId(dept.getDeptId());
		persistentDept.setName(dept.getName());
		persistentDept.setDescription(dept.getDescription());
		
		AuditRecord record = new AuditRecord();
		record.setMethod("CREATE");
		record.setUri(request.getRequestURI());
		record.setClient(request.getRemoteAddr());
		
		String user = request.getRemoteUser();
		if (user != null) {
			record.setUser(user);
		}
		HttpSession session = request.getSession(false);
		if (session != null) {
			record.setSessionId(session.getId());
		}		
		record.setTraceId(response.getHeader(ResponseLoggingFilter.TRACE_ID));
		record.setObjectType(Department.class.getName());
		record.setObjectId(persistentDept.getDeptId());
		audit.publishAuditMessage(record);

		return repo.save(persistentDept);
	}

	@PutMapping(value = "/departments/{id}", 
			    consumes = MediaType.APPLICATION_JSON_VALUE, 
			    produces = MediaType.APPLICATION_JSON_VALUE)
	public Department update(@Valid @RequestBody DepartmentDTO dept, 
			                 @PathVariable(name = "id", required = true) Long id, 
			                 HttpServletRequest request, HttpServletResponse response) {
		
		Optional<Department> deptOpt = repo.findById(id);
		if (!deptOpt.isPresent())
			throw new ResourceNotFoundException(NOT_FOUND + id);
		Department d = deptOpt.get();
		d.setDeptId(dept.getDeptId());
		d.setName(dept.getName());
		d.setDescription(dept.getDescription());

		AuditRecord record = new AuditRecord();
		record.setMethod("UPDATE");
		record.setUri(request.getRequestURI());
		record.setClient(request.getRemoteAddr());
		
		String user = request.getRemoteUser();
		if (user != null) {
			record.setUser(user);
		}
		HttpSession session = request.getSession(false);
		if (session != null) {
			record.setSessionId(session.getId());
		}		
		record.setTraceId(response.getHeader(ResponseLoggingFilter.TRACE_ID));
		record.setObjectType(Department.class.getName());
		record.setObjectId(d.getDeptId());
		audit.publishAuditMessage(record);

		return repo.save(d);
	}
	
	@DeleteMapping(value = "/departments/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public ResponseEntity<?> delete(@PathVariable(name = "id", required = true) Long id, 
			HttpServletRequest request, HttpServletResponse response) {

		Optional<Department> deptOpt = repo.findById(id);
		if (!deptOpt.isPresent())
			throw new ResourceNotFoundException(NOT_FOUND + id);

		repo.delete(deptOpt.get());

		AuditRecord record = new AuditRecord();
		record.setMethod("DELETE");
		
		record.setUri(request.getRequestURI());
		record.setClient(request.getRemoteAddr());
		
		String user = request.getRemoteUser();
		if (user != null) {
			record.setUser(user);
		}
		HttpSession session = request.getSession(false);
		if (session != null) {
			record.setSessionId(session.getId());
		}		
		record.setTraceId(response.getHeader(ResponseLoggingFilter.TRACE_ID));
		record.setObjectType(Department.class.getName());
		record.setObjectId(deptOpt.get().getDeptId());
		
		audit.publishAuditMessage(record);

		return ResponseEntity.ok().build();
	}
}
