package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String reportingStructureIdUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Before
    public void setup() {
        reportingStructureIdUrl = "http://localhost:" + port + "/reporting-structure/{id}";
    }

    @Test
    public void testRead() {
        ReportingStructure expectedReportingStructure = new ReportingStructure();

        Employee lennon = employeeRepository.findByEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        Employee mcCartney = employeeRepository.findByEmployeeId("b7839309-3348-463b-a7e3-5de1c168beb3");
        Employee starr = employeeRepository.findByEmployeeId("03aa1462-ffa9-4978-901b-7c001562cf6f");
        Employee best = employeeRepository.findByEmployeeId("62c1084e-6e34-4630-93fd-9153afb65309");
        Employee harrison = employeeRepository.findByEmployeeId("c0c2293d-16bd-4603-8e08-638a9d18b22c");

        List<Employee> ringoReports = new ArrayList<>();
        ringoReports.add(best);
        ringoReports.add(harrison);

        starr.setDirectReports(ringoReports);

        List<Employee> lennonReports = new ArrayList<>();
        lennonReports.add(mcCartney);
        lennonReports.add(starr);

        lennon.setDirectReports(lennonReports);

        expectedReportingStructure.setEmployee(lennon);
        expectedReportingStructure.setNumberOfReports(4);

        ReportingStructure actualReportingStructure = restTemplate.getForEntity(
                reportingStructureIdUrl,
                ReportingStructure.class,
                expectedReportingStructure.getEmployee().getEmployeeId()
        ).getBody();

        assertEquals(expectedReportingStructure.getNumberOfReports(), actualReportingStructure.getNumberOfReports(), 0);
        EmployeeServiceImplTest.assertEmployeeEquivalence(expectedReportingStructure.getEmployee(), actualReportingStructure.getEmployee());
    }
}
