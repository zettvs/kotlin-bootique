## Exercise 2: convert more code to Kotlin

In this exercise we will convert the Product.java and OrderItem.java classes to Kotlin, our goal is to make it more concise.

### Convert Product.java to Kotlin

Open Product.java and convert the file using IntelliJ (menu > Code > Convert Java File to Kotlin File). Wow, that was easy :-) 

### Convert OrderItem.java to Kotlin

Open OrderItem.java and convert the file using IntelliJ (menu > Code > Convert Java File to Kotlin File). The outcome of the conversion is far from optimal, we can do way better. Remember Kotlin [data classes](https://kotlinlang.org/docs/reference/data-classes.html)? Let get rid of the boiler-plate and convert the OrderItem class to a data class. Add the data keyword to the class so that it becomes:

```kotlin
data class OrderItem
```

Since it is a data class we can delete the equals, hashCode and toString methods, we get that for free with data classes. 

Data classes can have only 1 primary constructor. Lets merge the two constructors. The result should look like:

```kotlin
data class OrderItem @JsonCreator constructor(@JsonProperty("productId") val productId: String, 
                                              @JsonProperty("quantity") val quantity: Int, 
                                              val price: BigDecimal) {
    val totalPrice: BigDecimal
        get() = price.multiply(BigDecimal(quantity))
}
```

Build the project with maven (./mvnw clean verify), the build should succeed.

Start the application (./mvnw spring-boot:run) and see if the application still works. 

Now execute the following curl command on the terminal:

```                                                                                                                                                                                                                                                                                                                                                            
curl -H "Content-Type: application/json" -X POST -d '{"productId":"1","quantity":2}' http://localhost:8080/baskets/1/items
```

We should see an exception in the application logs:

```
Failed to read HTTP message: org.springframework.http.converter.HttpMessageNotReadableException: 
JSON parse error: Instantiation of [simple type, class com.bootique.bootique.OrderItem] value failed 
for JSON property price due to missing (therefore NULL) value for creator parameter price which is a non-nullable type; 
nested exception is com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException: 
Instantiation of [simple type, class com.bootique.bootique.OrderItem] value failed 
for JSON property price due to missing (therefore NULL) value for creator parameter price which is a non-nullable type
at [Source: (PushbackInputStream); line: 1, column: 30] (through reference chain: com.bootique.bootique.OrderItem["price"])
```

We broke the application :-( Remember we merged the two constructors? In the curl POST request we send only two fields for an OrderItem: productId and quantity, this used to work before we did the refactoring. But after the changes we have a constructor which requires 3 non-nullable (mandatory) properties. 

How can we fix this? First lets try to make the price field nullable (add ? after the type). You will probable notice that the totalPrice calculation is now also giving you a hard time. Because price can now be nullable, you need to add null checks in the totalPrice calculation.

This code looks like:

```kotlin
data class OrderItem @JsonCreator constructor(@JsonProperty("productId") val productId: String, 
                                              @JsonProperty("quantity") val quantity: Int, 
                                              val price: BigDecimal?) {
    val totalPrice: BigDecimal?
        get() = price?.multiply(BigDecimal(quantity))
}
```

A better approach would be to avoid having to deal with null values. This way we do not have to worry about potential NPEs. We can do this by providing a default value for the price, BigDecimal.ZERO was used in the Java version, we will use that here as well. 

In case you are wondering where the real price is calculated, take a look at the BootiqueController.addToBasket().

```kotlin
data class OrderItem @JsonCreator constructor(@JsonProperty("productId") val productId: String, 
                                              @JsonProperty("quantity") val quantity: Int, 
                                              val price: BigDecimal = BigDecimal.ZERO) {
    val totalPrice: BigDecimal
        get() = price.multiply(BigDecimal(quantity))
}
```

We can improve the readability of the totalPrice calculation. Would it not be nice being able to write it like:

```kotlin
val totalPrice: BigDecimal = price * quantity
```

This can be achieved by using [operator overloading](https://kotlinlang.org/docs/reference/operator-overloading.html) in Kotlin. The Kotlin stdlib includes [overloaded operators](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/java.math.-big-decimal/index.html) for Java types like java.math.BigDecimal. This allows use to write the above statement like:

```kotlin
val totalPrice: BigDecimal = price * BigDecimal(quantity)
```

This is not yet how we want to write the expression because we still need to wrap the quantity in a BigDecimal Object in order to use the operator. Lets look at the signature for times operator on java.math.BigDecimal. 

```kotlin
public operator inline fun java.math.BigDecimal.times(other: java.math.BigDecimal): java.math.BigDecimal
```

As you probably know  _price.times(BigDecimal(quantity))_ is the same as _price * BigDecimal(quantity)_. We want to be able to invoke the times function with a Int argument, therefore we need to implement our own overloaded operator. Give it a try.


### Polishing the code

This application communicates over HTTP using JSON, as you have seen in Swagger or in the curl command in the introduction.md. The JSON (de)serialization in this application is handled by the Jackson library, the spring-boot-starter-web dependency pulls in all these Jackson dependencies for us.

In the BasketController the JSON data is mapped from the POST data directly on the OrderItem class. As you can see, in the OrderItem class we are instructing the Jackson library, with the @JsonCreator and @JsonProperty, how to map the JSON data to our Java (or Kotlin) class. 

Reason for having the @JsonProperty annotation is that when compiling Java code, the parameter names of the constructor parameters are lost, so Jackson does not know how to map the json properties to the OrderItem class. In Kotlin, constructor parameter names are preserved when compiling code. We can therefore get rid of the @JsonProperty annotations. 

As a bonus feature, the Jackson library also allows us to omit the @JsonCreator annotation when using Kotlin (these features are provided by the jackson kotlin module).

We can also drop the constructor keyword since there is only one constructor here _**and**_ because do not need/have any annotation on the constructor left.

The resulting polished data class looks like:

```kotlin
data class OrderItem(val productId: String, val quantity: Int, val price: BigDecimal = BigDecimal.ZERO) {
    val totalPrice: BigDecimal = price * quantity
}
```

### Done?

You could consider converting the Product class to a data class so we get the equals, hashCode and toString for free.

Continue with exercise-3:

```kotlin
git checkout exercise-3
```