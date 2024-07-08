package com.example.demo;

import org.springframework.aot.hint.annotation.Reflective;

public class Message {

	@Reflective
    public static final String MESSAGE = "Hello from Spring Native!🤖";
}
