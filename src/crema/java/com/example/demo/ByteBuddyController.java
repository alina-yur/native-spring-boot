package com.example.demo;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ByteBuddyController {

	@GetMapping("/bytebuddy")
	public String generateGreeting() throws Throwable {
		Class<?> generatedType = new ByteBuddy()
				.subclass(Object.class)
				.name("com.example.demo.generated.Greeting" + System.nanoTime())
				.defineMethod("message", String.class, Visibility.PUBLIC)
				.intercept(FixedValue.value("Hello from a runtime-loaded Byte Buddy class!"))
				.make()
				.load(ByteBuddyController.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
				.getLoaded();

		MethodHandle constructor = MethodHandles.publicLookup()
				.findConstructor(generatedType, MethodType.methodType(void.class));
		Object greeting = constructor.invoke();
		MethodHandle message = MethodHandles.publicLookup()
				.findVirtual(generatedType, "message", MethodType.methodType(String.class));
		return (String) message.invoke(greeting);
	}
}
