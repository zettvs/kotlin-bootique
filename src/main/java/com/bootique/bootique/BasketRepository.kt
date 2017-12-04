package com.bootique.bootique

import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

/**
 * Dummy implementation of a Basket persistent store, keeps the baskets in memory.
 *
 * Restarting the app will wipe all the data.
 */
@Repository
class BasketRepository {

    fun getBasketById(id: String): Basket = baskets.getOrPut(id) { Basket() }

    companion object {
        private val baskets = ConcurrentHashMap<String, Basket>()
    }
}
