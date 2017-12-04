package com.bootique.bootique

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

/**
 * Spring boot application with Swagger2 enabled.
 */
@SpringBootApplication
@EnableSwagger2
class BootiqueApplication {

    /**
     * Swagger2 configuration.
     */
    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
    }

    /**
     * Runs the Spring boot application.
     */
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(BootiqueApplication::class.java, *args)
        }
    }
}