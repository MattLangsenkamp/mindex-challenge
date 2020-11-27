package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeService employeeService;

    @Override
    public ReportingStructure read(String id) {
        LOG.debug("Computing employee reportingStructure dynamically for employee id [{}]", id);

        Employee employee = employeeService.read(id, -1);
        int numberOfReports = TraverseReportingStructureTree(employee);
        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(numberOfReports);
        return reportingStructure;
    }

    private int TraverseReportingStructureTree(Employee employee) {
        int count = 0;
        if (employee.getDirectReports() != null) {
            count += employee.getDirectReports().size();
            // making assumption reporting structure cannot be cyclic
            for (Employee e : employee.getDirectReports()) {
                count += TraverseReportingStructureTree(e);
            }
        }
        return count;
    }
}
