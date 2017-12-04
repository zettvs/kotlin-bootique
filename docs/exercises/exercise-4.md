## Exercise 4: Spring 5 Kotlin Bean DSL

Rewrite the BootiqueApplication to use the Spring 5 Kotlin Bean DSL.

### Kotlin Bean DSL

Spring 5 provides a [Kotlin Beans DSL](https://docs.spring.io/spring/docs/current/spring-framework-reference/languages.html#kotlin-bean-definition-dsl) to define your application configuration in a functional way.

Lets introduce a function that defines are beans in a functional way.

```kotlin
fun beans(): BeanDefinitionDsl = beans {
    ...bean definitions here
}
```

**Exercise**: add the beans function in the BootiqueApplication.kt file.

**Exercise**: add the existing Docket bean inside the beans function.

**Exercise**: remove the @Bean fun api() function.