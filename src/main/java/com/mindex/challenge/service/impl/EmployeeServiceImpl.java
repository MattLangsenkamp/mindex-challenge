package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Reading employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    /**
     *
     * @param id id of employee to fetch
     * @param reportingTreeDepth depth in which to populate reporting tree structure. 0 returns
     *                           employee specified as is from database. number < 0 gets full
     *                           tree regardless of depth
     *
     * @return an employee with its structure of direct reports filled out to the specified depth
     */
    @Override
    public Employee read(String id, int reportingTreeDepth) {
        LOG.debug("Reading employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);
         if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        BuildReportingStructureTree(employee, reportingTreeDepth);

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    private void BuildReportingStructureTree(Employee employee, int reportingTreeDepth) {
        List<Employee> directReports = employee.getDirectReports();

        // specified depth has been reached
        if (reportingTreeDepth == 0) return;

        if (directReports != null) {
            List<Employee> fullDirectReports = new ArrayList<Employee>();
            // making assumption reporting structure cannot be cyclic
            for (Employee e : directReports) {
                Employee employeeToTraverse = PotentiallyRefetchEmployee(e);
                BuildReportingStructureTree(employeeToTraverse, reportingTreeDepth - 1);
                fullDirectReports.add(employeeToTraverse);
            }
            employee.setDirectReports(fullDirectReports);
        }
    }

    private Employee PotentiallyRefetchEmployee(Employee employee) {
        Employee returnedEmployee = employee;
        // making assumption that employee with no first or last name is just a class with the id
        // and not a full employee
        if (employee.getFirstName() == null || employee.getLastName() == null) {
            returnedEmployee = this.read(employee.getEmployeeId());
        }
        return returnedEmployee;
    }
}
