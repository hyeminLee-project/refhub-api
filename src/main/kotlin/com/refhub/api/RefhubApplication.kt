package com.refhub.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class RefhubApplication

fun main(args: Array<String>) {
    runApplication<RefhubApplication>(*args)
}
