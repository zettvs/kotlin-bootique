## Exercise 2: convert more code to Kotlin

In this exercise we will convert the Product.java and OrderItem.java classes to Kotlin, our goal is to make it more concise.

### Convert Product.java to Kotlin

Open Product.java and convert the file using IntelliJ (menu > Code > Convert Java File to Kotlin File). Wow, that was easy :-) 

### Convert OrderItem.java to Kotlin

Open OrderItem.java and convert the file using IntelliJ (menu > Code > Convert Java File to Kotlin File). The outcome of the conversion is far from optimal, we can do way better. Remember Kotlin [data classes](https://kotlinlang.org/docs/reference/data-classes.html)? Let get rid of the boiler-plate and convert the OrderItem class to a data class. Add the data keyword to the class so that it becomes:

```kotlin
data class OrderItem
```

With data classes we get the equals, hashCode and toString method for free:
 
**Exercise**: delete the equals, hashCode and toString methods.

Data classes can have only 1 primary constructor:
 
**Exercise**: merge the two constructors into a single primary constructor.

<details>
  <summary>The resulting code should look like this:</summary>
  
```kotlin
data class OrderItem @JsonCreator constructor(@JsonProperty("productId") val productId: String, 
                                              @JsonProperty("quantity") val quantity: Int, 
                                              val price: BigDecimal) {
    val totalPrice: BigDecimal
        get() = price.multiply(BigDecimal(quantity))
}
```
</details>
<br>
In case you are wondering where the price value is being provided, then have a look at the BootiqueController.addToBasket().

#### Verify the changes

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

We broke the application :-( Remember we merged the two constructors? In the POST body we send only two fields _{"productId":"1","quantity":2}_ for an OrderItem, this used to work when there were to constructors, after the changes we should have ended up with a constructor which requires 3 non-nullable (mandatory) parameters. 

How can we fix this with Kotlin? First try to make the price field nullable and add the ? after the BigDecimal type. You will probable notice that the totalPrice calculation is now also giving you a hard time. Price can now be nullable therefore you need to add null checks in the totalPrice calculation.

<details>
  <summary>An example with nullable price:</summary>
  
```kotlin
data class OrderItem @JsonCreator constructor(@JsonProperty("productId") val productId: String, 
                                              @JsonProperty("quantity") val quantity: Int, 
                                              val price: BigDecimal?) {
    val totalPrice: BigDecimal?
        get() = price?.multiply(BigDecimal(quantity))
}
```
</details>

A better approach would be to avoid having to deal with null values, this way we do not have to worry about potential NPEs. We can do this by providing a default value for the price, in the Java version price was assigned the value of BigDecimal.ZERO, use that here as well. 

**Exercise**: assign the default value to the price parameter, restart the application and try to run the same curl command as before.

<details>
  <summary>An examlple with a default value for price:</summary>
  
```kotlin
data class OrderItem @JsonCreator constructor(@JsonProperty("productId") val productId: String, 
                                              @JsonProperty("quantity") val quantity: Int, 
                                              val price: BigDecimal = BigDecimal.ZERO) {
    val totalPrice: BigDecimal
        get() = price.multiply(BigDecimal(quantity)) // evaluated every time we access the totalPrice property or call getTotalPrice() from Java.
}
```
</details>

In the code snippet above the constructor arguments are val, immutable, which means after assignment the value cannot be changed. Therefore we can also write the totalPrice assignment as an expression. Would it not be nice if we could write it like:

```kotlin
val totalPrice: BigDecimal = price * quantity
```

This can be achieved by using [operator overloading](https://kotlinlang.org/docs/reference/operator-overloading.html) in Kotlin. The Kotlin stdlib includes [overloaded operators](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/java.math.-big-decimal/index.html) for Java types like java.math.BigDecimal. This allows use to write the above statement like:

```kotlin
val totalPrice: BigDecimal = price * BigDecimal(quantity)
```

**Exercise**: replace the totalPrice calculation with the assignment snippet above.

This is not yet how we want to write the expression because we still have to wrap the quantity in a BigDecimal in order to use the operator. Lets look at the signature for times operator on java.math.BigDecimal. 

```kotlin
public operator inline fun java.math.BigDecimal.times(other: java.math.BigDecimal): java.math.BigDecimal
```

As you probably know  _price.times(BigDecimal(quantity))_ is the same as _price * BigDecimal(quantity)_. We want to be able to invoke the times function with a Int argument so that we do not need to wrap it in a BigDecimal. Therefore we need to implement our own overloaded operator, simular to the one in the Koltin stdlib. 

**Exercise**: write an operator for java.math.BigDecimal that accepts an Int.

<details>
  <summary>The resulting code should look like this:</summary>
  
```kotlin
public operator inline fun java.math.BigDecimal.times(other: Int): java.math.BigDecimal
```
</details>

### Polishing the code

This application communicates over HTTP using JSON, as you have seen in Swagger or in the curl command in the introduction.md. The JSON (de)serialization in this application is handled by the Jackson library, the spring-boot-starter-web dependency pulls in all these Jackson dependencies for us.

In the BasketController the JSON data is mapped from the POST data directly on the OrderItem class. As you can see, in the OrderItem class we are instructing the Jackson library, with the @JsonCreator and @JsonProperty, how to map the JSON data to our Java (or Kotlin) class. 

Reason for having the @JsonProperty annotation is that when compiling Java code, the parameter names of the constructor parameters are lost, so Jackson does not know how to map the json properties to the OrderItem class. In Kotlin, constructor parameter names are preserved when compiling code. We can therefore get rid of the @JsonProperty annotations. 

As a bonus feature, the Jackson library also allows us to omit the @JsonCreator annotation when using Kotlin (these features are provided by the jackson kotlin module).

We can also drop the constructor keyword since there is only one constructor here _**and**_ because do not need/have any annotation on the constructor left.

**Exercise**: cleanup the code by removing the Jackson annotations

<details>
  <summary>The resulting polished data class looks like:</summary>

```kotlin
data class OrderItem(val productId: String, val quantity: Int, val price: BigDecimal = BigDecimal.ZERO) {
    val totalPrice = price * quantity // evaluated only once!
}

public operator inline fun java.math.BigDecimal.times(other: Int): java.math.BigDecimal
```
</details>

### Done?

You could consider converting the Product class to a data class so we get the equals, hashCode and toString for free.

**Exercise**: Continue with exercise-3:

```kotlin
git checkout exercise-3
```