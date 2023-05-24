package com.pos.monitoring;

import com.pos.monitoring.repositories.system.Connection8005;
import com.pos.monitoring.services.MachineService;
import com.pos.monitoring.services.jobs.JobService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import java.lang.reflect.Field;
import java.util.Locale;

@SpringBootTest
class PosMonitoringApplicationTests {

	@Autowired
	private MessageSource messageSource;
	@Autowired
	private Connection8005 connection8005;
	@Autowired
	private MachineService machineService;



	@Test
	void contextLoads() {
		machineService.getStat("09011");
	}


	public class MyClass {
		int myInt;
		double myDouble;
		boolean myBoolean;
		char myChar;

	}
//	@Test
//	void machineTest(){
//		connection8005.getAllChangeMachines();
//	}
	@Test
	void machineTestUpdate(){
		System.out.println(messageSource.getMessage("SERVER_ERROR_FROM_PLUM", new Object[]{"ASADA"}, Locale.forLanguageTag("uz")));
//		jobService.synchronizeMachine();
	}


}
