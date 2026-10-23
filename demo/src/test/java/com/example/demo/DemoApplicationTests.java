package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.demo.repository.IVehiculeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private IVehiculeRepository vehiculeRepository;

	@Test
	void contextLoads() {
		assertNotNull(vehiculeRepository);
	}

}
