package com.bootique.bootique

import java.math.BigDecimal

operator fun BigDecimal.times(quantity: Int) = this.times(BigDecimal(quantity))