package com.bootique.bootique

import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Basket contains the order items for a specific user or session.
 */
class Basket {
    private val items = CopyOnWriteArrayList<OrderItem>()

    fun getOrderItems() = Collections.unmodifiableList(items)

    /**
     * Calculates the sum of the order item totalPrice.
     * @return BigDecimal.ZERO in case of an empty basket.
     */
    fun getTotalPrice() = items.sumBy { it.totalPrice }

    fun addOrderItem(orderItem: OrderItem) = items.add(orderItem)
}
