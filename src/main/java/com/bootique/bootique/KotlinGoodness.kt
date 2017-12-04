package com.bootique.bootique

import java.math.BigDecimal

operator fun BigDecimal.times(quantity: Int) = this.times(BigDecimal(quantity))

fun <T> Iterable<T>.sumBy(selector: (T) -> BigDecimal): BigDecimal {
    var sum = BigDecimal.ZERO
    for (element in this) {
        sum += selector(element)
    }
    return sum
}