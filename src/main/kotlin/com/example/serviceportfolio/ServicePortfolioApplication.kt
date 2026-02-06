package com.example.serviceportfolio

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@RestController
class MyApplication {

    @RequestMapping("/")
    fun home() = "Hello World!"

}
fun main(args: Array<String>) {
    runApplication<MyApplication>(*args)
}
