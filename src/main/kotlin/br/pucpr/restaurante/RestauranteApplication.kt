package br.pucpr.restaurante

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class RestauranteApplication

fun main(args: Array<String>) {
	runApplication<RestauranteApplication>(*args)
}