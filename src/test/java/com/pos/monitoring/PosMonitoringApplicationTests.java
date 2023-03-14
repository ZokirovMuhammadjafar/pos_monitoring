package com.pos.monitoring;

import com.pos.monitoring.repositories.system.Connection8005;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PosMonitoringApplicationTests {

	@Autowired
	private Connection8005 connection8005;
	@Test
	void contextLoads() {

	}

	@Test
	void machineTest(){
		connection8005.getAllChangeMachines();
	}

}
