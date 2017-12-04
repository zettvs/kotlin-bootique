package com.bootique.bootique

import java.math.BigDecimal

/**
 * A product that can be bought.
 */
data class Product(val id: String, val title: String, val brand: String, val listPrice: BigDecimal)