package com.example.demo;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class HttpRequestTest {

	@LocalServerPort
	private int port;

	@Test
	void greetingShouldReturnMessage() {
		WebTestClient client = WebTestClient.bindToServer()
				.baseUrl("http://localhost:" + port)
				.build();

		client.get().uri("/hello")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class).value(body -> assertThat(body).contains("Hello from GraalVM and Spring!💃"));
	}
}