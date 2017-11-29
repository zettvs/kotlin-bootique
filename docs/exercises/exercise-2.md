## Exercise 2: convert more code to Kotlin

In this exercise we will convert the OrderItem class to Kotlin, our goal is to make it a bit more concise.

Open OrderItem.java and convert the file using IntelliJ (menu > Code > Convert Java File to Kotlin File). The outcome of the conversion is far from optimal, we can do way better. Remember data classes? Let get rid of the boiler-plate and convert the OrderItem class to a data class. Add the data keyword to the class so that it becomes:

```kotlin
data class OrderItem
```

Since it is a data class we can delete the equals, hashCode and toString methods, we get that for free with data classes. 

Data classes can have only 1 primary constructor. Lets cleanup that mess a bit and merge the two constructors. The result should look like:

```kotlin
data class OrderItem @JsonCreator constructor(@JsonProperty("productId") val productId: String, @JsonProperty("quantity") val quantity: Int, val price: BigDecimal) {
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
JSON parse error: Instantiation of [simple type, class com.bootique.bootique.OrderItem] value failed for JSON property price due to missing (therefore NULL) value for creator parameter price which is a non-nullable type; 
nested exception is com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException: Instantiation of [simple type, class com.bootique.bootique.OrderItem] value failed for JSON property price due to missing (therefore NULL) value for creator parameter price which is a non-nullable type
at [Source: (PushbackInputStream); line: 1, column: 30] (through reference chain: com.bootique.bootique.OrderItem["price"])
```

We broke the application :-( Remember we merged the two constructors? In the curl POST request we send only two fields for an OrderItem: productId and quantity, this used to work before we did the refactoring. But after the changes we have a constructor which requires 3 non-nullable (mandatory) properties. 

How can we fix this? First lets try to make the price field nullable (add ? after the type). You will probable notice that the totalPrice calculation is now also giving you a hard time. Because price can now be nullable, you need to add null checks in the totalPrice calculation.

The code should now look like:

```kotlin
data class OrderItem @JsonCreator constructor(@JsonProperty("productId") val productId: String, @JsonProperty("quantity") val quantity: Int, val price: BigDecimal?) {
    val totalPrice: BigDecimal?
        get() = price?.multiply(BigDecimal(quantity))
}
```

A better approach would be to avoid having to deal with null values. This way we do not have to worry about potential NPEs. We can do this by providing a default value for the price, 0 seems reasonable here. In case you are wondering where the real price is calculated, take a look at the BootiqueController.addToBasket().

```kotlin
data class OrderItem @JsonCreator constructor(@JsonProperty("productId") val productId: String, @JsonProperty("quantity") val quantity: Int, val price: BigDecimal = BigDecimal.ZERO) {
    val totalPrice: BigDecimal
        get() = price.multiply(BigDecimal(quantity))
}
```

### Polishing the code

The JSON (de)serialization in this application is handled using the Jackson library. In the spring-boot-starter-web dependency, which is defined in the maven pom.xml, the Jackson kotlin module is also being pulled in. By using the Jackson kotlin module we can cleanup the Jackson annotations a bit. The polished version of our data class would look like this:

```kotlin
data class OrderItem(val productId: String, val quantity: Int, val price: BigDecimal = BigDecimal.ZERO) {
    val totalPrice: BigDecimal = price.multiply(BigDecimal(quantity))
}
```
We can also drop the constructor keyword since there is only one constructor here _**and**_ because do not have any annotation on the constructor left.

There is one optimization left, we can improve the readability of the totalPrice calculation. Would it not be nice being able to write it like:

```kotlin
val totalPrice: BigDecimal = price * BigDecimal(quantity)
```

Since Kotlin 1.2 the Kotlin stdlib includes an overloaded times operator for BigDecimal. So we can write it in the syntax as shown above.

```kotlin
public operator inline fun java.math.BigDecimal.times(other: java.math.BigDecimal): java.math.BigDecimal
```

### Convert Product.java to Kotlin

Open Product.java and convert the file using IntelliJ (menu > Code > Convert Java File to Kotlin File). Wow, that was easy :-) 

You could consider converting it to a data class so we get the equals, hashCode and toString for free.

### Done?

Continue with exercise-3:

```kotlin
git checkout exercise-3
```