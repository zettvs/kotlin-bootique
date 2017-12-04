package com.bootique.bootique

import java.math.BigDecimal

data class OrderItem(val productId: String, val quantity: Int, val price: BigDecimal = BigDecimal.ZERO) {
    val totalPrice: BigDecimal = price * quantity
}