package com.bootique.bootique

import java.math.BigDecimal

class Basket {
    private val items = mutableListOf<OrderItem>()

    fun getOrderItems() = items.toList() // immutable

    fun addOrderItem(orderItem: OrderItem) = items.add(orderItem)

    fun getTotalPrice() = items.fold(BigDecimal.ZERO) { accumulated, it -> accumulated + (it.price * it.quantity) }
}