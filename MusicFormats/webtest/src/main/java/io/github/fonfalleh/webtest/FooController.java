package io.github.fonfalleh.webtest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class FooController {

    @GetMapping(value = "/foo", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] test(@RequestParam String notes) throws IOException {
        return LilyPng.foo(notes);
    }
}
