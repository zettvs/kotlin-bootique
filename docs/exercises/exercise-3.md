## Exercise 3: more conversion fun

### Convert Basket.java to Kotlin

Open Basket.java

**Exercise**: convert Basket.java to Kotlin using IntelliJ (menu > Code > Convert Java File to Kotlin File). 

After the conversion the code is broken, just take a look at the totalPrice calculation. Apparently IntelliJ is not capable of figuring out all the stream concatenations and tries to make the best out of it. 

We can fix this by writing the calculation in exactly the same way as we would do with the Java Stream API:

```kotlin
val totalPrice: BigDecimal        
    get() = items.stream().map(OrderItem::totalPrice).reduce(BigDecimal.ZERO, BigDecimal::add)
```

Kotlin has a richer (functional) API which allows us write this in a different way without using the Java Stream API, for example we could achieve the same using the fold function:

```kotlin
val totalPrice: BigDecimal 
    get() = items.fold(BigDecimal.ZERO, { sum, item -> sum.plus(item.totalPrice) })
```

You could argue if the above is more concise than the Java Stream API version. A more concise way of writing this would be:

```kotlin
val totalPrice: BigDecimal 
    get() = items.sumBy { item -> item.totalPrice }
```

Kotlin already has build-in functions to sum Int and Doubles, but not BigDecimals:

```kotlin
public inline fun <T> Iterable<T>.sumBy(selector: (T) -> Int): Int 

public inline fun <T> Iterable<T>.sumByDouble(selector: (T) -> Double): Double
```

**Exercise**: write an extension function which allows for summing BigDecimals.

Hint: look at the implementation of `public inline fun <T> Iterable<T>.sumBy(selector: (T) -> Int): Int`

<details>
  <summary>The resulting code should look like this:</summary>
  
```kotlin
fun <T> Iterable<T>.sumBy(selector: (T) -> BigDecimal): BigDecimal {
    var sum = BigDecimal.ZERO
    for (element in this) {
        sum += selector(element)
    }
    return sum
}
```
</details>
<br>
### Property syntax vs functions

Kotlin encourages you to use the [property syntax](https://kotlinlang.org/docs/reference/properties.html) whenever possible. In some situations you could also use a function to achieve similar results.

**Exercise**: rewrite the totalPrice calculation as a function expression.

<details>
  <summary>The resulting code should look like this:</summary>

```kotlin
fun getTotalPrice() = items.sumBy { it.totalPrice }
```
</details>
<br>

### Convert BasketRepository.java to Kotlin

Open BasketRepository.java

**Exercise**: convert BasketRepository.java to Kotlin using IntelliJ (menu > Code > Convert Java File to Kotlin File). 

Two interesting things about the converted code. First have a look at the getBasketById(). There is some explicit casting now and the computeIfAbsent looks not so pretty.

**Exercise**: remove the cast to java.util.Map<String, Basket>

See if you can find a method in the Kotlin map which would be a nice replacement for computeIfAbsent

**Exercise**: replace computeIfAbsent by a more concise Kotlin alternative

<details>
  <summary>The resulting code should look like this:</summary>

```kotlin
fun getBasketById(id: String): Basket = baskets.getOrPut(id) { Basket() }
```
</details>
<br>

```kotlin
companion object {
    private val baskets = ConcurrentHashMap<String, Basket>()
}
```

### Convert BootiqueController.java to Kotlin

Open BootiqueController.java

**Exercise**: convert BootiqueController.java to Kotlin using IntelliJ (menu > Code > Convert Java File to Kotlin File). 

addToBasket

```kotlin
val (_, _, _, listPrice) = productRepository.getProductById(orderItem.productId)
```

replace

### Convert ProductRepository.java to Kotlin

Open ProductRepository.java

**Exercise**: convert ProductRepository.java to Kotlin using IntelliJ (menu > Code > Convert Java File to Kotlin File). 
