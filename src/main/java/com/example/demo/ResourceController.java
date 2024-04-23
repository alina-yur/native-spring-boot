package com.example.demo;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.FileCopyUtils;
import java.io.InputStreamReader;
import java.io.IOException;

@RestController
public class ResourceController {

    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping("/resource")
    public String getResource() {
        Resource resource = resourceLoader.getResource("classpath:message.xml");
        try {
            InputStreamReader reader = new InputStreamReader(resource.getInputStream());
            String content = FileCopyUtils.copyToString(reader);
            System.out.println("Content: " + content);
            return content;
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }
}
