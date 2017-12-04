## Exercise 4: Spring 5 Kotlin Bean DSL

In this exercise we will show a real world example of using a DSL written in Kotlin. We will use the Spring 5 Kotlin bean defintion DSL to rewrite the BootiqueApplication.kt class to this DSL.

### Kotlin Bean DSL

Spring 5 provides a [Kotlin Beans definition DSL](https://docs.spring.io/spring/docs/current/spring-framework-reference/languages.html#kotlin-bean-definition-dsl) to define your application configuration in a functional way.

We will add this to our existing Spring Boot application. In this exercise the goal is to make the SpringBootApplication class more concise. Later you can decide whether you like it or not :-)

**Exercise**: remove the curly braces from the BootiqueApplication class.

We can define the DSL using a Kotlin function in the BootiqueApplication. Below is an example of a Kotlin function that defines the BeanDefinitionDsl.
                                                                          
```kotlin
fun beans(): BeanDefinitionDsl = beans {
    bean<T> { 
        ...instantiation
    }
}
```

**Exercise**: add the beans() function in the BootiqueApplication.kt file.

Configure the existing `@Bean fun api(): Docket` in the Kotlin bean definition DSL.

**Exercise**: add the Docket bean inside the beans function.

We don`t need the old @Bean function anymore.

**Exercise**: remove the `@Bean fun api(): Docket` function.

We need to configure the SpringApplication runner with the BeanDefinitionDsl. Spring Boot 2 has some Kotlin extensions to do just that:

```kotlin
fun main(args: Array<String>) {
    runApplication<BootiqueApplication>(*args) {
        addInitializers(beans())
    }
}
```

**Exercise**: Replace the existing main, including companion object.

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

### Next steps

Your are almost there, continue with the last exercise, exercise-5:

```
git checkout exercise-5
```