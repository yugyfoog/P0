package com.training.p0.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)

class LoginDAOImplTest {
	Credentials c;
	LoginDAO login;
	static int userid;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		login = new LoginDAOImpl();
		c = new Credentials("dummy-username", "dummy-password");
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	@Order(value = 1)
	void testNewUser() {
		userid = login.newUser(c);
		assertNotEquals(-1, userid);
	}

	@Test
	@Order(value = 2)
	void testValidate() {
		int found = login.validate(c);
		assertEquals(userid, found);
	}

	@Test
	@Order(value = 3)
	void testDeleteUser() {
		boolean found = login.deleteUser(userid);
		assertEquals(true, found);
	}

}
