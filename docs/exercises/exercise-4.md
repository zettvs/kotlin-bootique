## Exercise 4: Spring 5 Kotlin Bean DSL

Rewrite the BootiqueApplication to use the Spring 5 Kotlin Bean DSL.

### Kotlin Bean DSL

Spring 5 provides a [Kotlin Beans DSL](https://docs.spring.io/spring/docs/current/spring-framework-reference/languages.html#kotlin-bean-definition-dsl) to define your application configuration in a functional way.

Lets introduce a function that defines are beans in a functional way.

**Exercise**: remove the curly braces from the class.

Below is an example of a Kotlin function that defines the BeanDefinitionDsl.

```kotlin
fun beans(): BeanDefinitionDsl = beans {
    ...bean definitions here
}
```

**Exercise**: add the beans function in the BootiqueApplication.kt file.

Configure the existing @Beans in the Kotlin Bean DSL/

**Exercise**: add the Docket bean inside the beans function.

Cleanup the old @Bean method.

**Exercise**: remove the @Bean fun api() function.

We need to provide the Spring Boot application with the BeanDefinitionDsl.

**Exercise**:Replace the existing main, including companion object by:

```kotlin
fun main(args: Array<String>) {
    runApplication<BootiqueApplication>(*args) {
        addInitializers(beans())
    }
}
```

We should now have a file with a Kotlin class with only two annotations defined on the class. And besides that it contains two functions, beans() and main().

<section>
<summary>The resulting code should look like this:</summary>

```kotlin
/**
 * Spring boot application with Swagger2 enabled.
 */
@SpringBootApplication
@EnableSwagger2
class BootiqueApplication

/**
 * Swagger2 configuration.
 */
fun beans() = beans {
    bean<Docket> {
        Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
    }
}

/**
 * Runs the Spring boot application.
 */
fun main(args: Array<String>) {
    runApplication<BootiqueApplication>(*args) {
        addInitializers(beans())
    }
}
```
</section>
<br>

We could even simplify the code even further by in-lining the beans() function inside runApplication.

**Exercise**: move  `beans { ... }` inside of the `runApplication { ... }` block

<section>
<summary>The resulting code should look like this:</summary>

```kotlin
/**
 * Spring boot application with Swagger2 enabled.
 */
@SpringBootApplication
@EnableSwagger2
class BootiqueApplication

/**
 * Runs the Spring boot application.
 */
fun main(args: Array<String>) {
    runApplication<BootiqueApplication>(*args) {
        beans {
            bean<Docket> {
                Docket(DocumentationType.SWAGGER_2)
                        .select()
                        .apis(RequestHandlerSelectors.any())
                        .paths(PathSelectors.any())
                        .build()
            }
        }
    }
}
```
</section>