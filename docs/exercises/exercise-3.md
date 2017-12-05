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

Kotlin encourages you to use the [property syntax](https://kotlinlang.org/docs/reference/properties.html) whenever possible. In some situations you could also prefer/use a function to achieve similar results.

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

Note that the `baskets: ConcurrentHashMap` is wrapped in a `companion object { }`.

```kotlin
companion object {
    private val baskets = ConcurrentHashMap<String, Basket>()
}
```

The original Java code defined baskets as static, Kotlin does not support the static keyword. You can use (companion) objects instead.

### Convert ProductRepository.java to Kotlin

Open ProductRepository.java

**Exercise**: convert ProductRepository.java to Kotlin using IntelliJ (menu > Code > Convert Java File to Kotlin File). 

You will notice that getProductById() is broken. This is because the expected return type is non-nullable. While the implementation might return a null value when the product cannot be found.

**Exercise**: change the return type of `fun getProductById()` that it allows for a returning nullable Product.

<details>
<summary>The resulting code should look like this:</summary>

```kotlin
fun getProductById(productId: String): Product? {
    return products[productId]
}
```
</details>
<br>

**Exercise**: write the function `fun getProductById()` as an expression function.

<details>
<summary>The resulting code should look like this:</summary>

```kotlin
fun getProductById(productId: String) = products[productId]
```
</details>
<br>

The implementation of `fun getProducts(): List<Product>` was translated from Java, but we can improve this the Kotlin way. The goal is to return an immutable List of Products. In Kotlin we can return the products.values and convert that to a List. Which is immutable by default.

**Exercise**: return products.values as a Kotlin (immutable) List from `fun getProducts(): List<Product>`

<details>
<summary>The resulting code should look like this:</summary>

```kotlin
fun getProducts() = products.values.toList()
```
</details>
<br>

### Convert BootiqueController.java to Kotlin

Open BootiqueController.java

**Exercise**: convert BootiqueController.java to Kotlin using IntelliJ (menu > Code > Convert Java File to Kotlin File). 

The resulting code looks pretty ok.

**Exercise**: Rewrite the functions to expression functions when possible.

The addToBasket() function can still be improved. What if we are not able to find the product for the given productId?

The converted code will throw a Kotlin NullPointException because of the !! in `productById!!.listPrice`.

The fix would be to properly check if we got result from `productRepository.getProductById(orderItem.productId)`.

**Exercise**: Add a null check for non existing products and throw an IllegalArgumentException if not found.

<details>
<summary>The resulting code should look like this:</summary>

```kotlin
    @PostMapping(path = ["/baskets/{id}/items"], consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun addToBasket(@PathVariable id: String, @RequestBody orderItem: OrderItem): Basket {
        return basketRepository.getBasketById(id).apply {
            val product = productRepository.getProductById(orderItem.productId)
                    ?: throw IllegalArgumentException("Product with productId: ${orderItem.productId} not found!")
            addOrderItem(OrderItem(orderItem.productId, orderItem.quantity, product.listPrice))
        }
    }
```
</details>
<br>

Test if your application is still working as expected.

### Next steps

You have now successfully converted all of the Java code to Kotlin! Continue with exercise-4:

```
git checkout exercise-4
```