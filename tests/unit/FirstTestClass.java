package Week2Test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class FirstTestClass {

	@Test
	@DisplayName("TC1")
	void empNumber() {
		System.out.println("Employee ID: 547795");
	}
	
	@Test
	@DisplayName("TC2")
	void empName() {
		System.out.println("Employee Name:  Ashwath Mukundan");
	}
	
	@Test
	@DisplayName("TC3")
	void empOrg() {
		System.out.println("Employee Org: Cognizant");
	}
}
