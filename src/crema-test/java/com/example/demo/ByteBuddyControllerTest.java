package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ByteBuddyControllerTest {

	@Test
	void generatesAndLoadsGreetingClass() throws Throwable {
		assertThat(new ByteBuddyController().generateGreeting())
				.isEqualTo("Hello from a runtime-loaded Byte Buddy class!");
	}
}
