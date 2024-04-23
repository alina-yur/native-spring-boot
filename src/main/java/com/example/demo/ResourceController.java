package com.example.demo;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;

@RestController
public class ResourceController {

    @GetMapping("/resource")
    public String getResource() {
        Resource xml = new ClassPathResource("message.xml");
        try (InputStreamReader reader = new InputStreamReader(xml.getInputStream())) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            return "Failed to load resource";
        }
    }
}

