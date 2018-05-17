package com.johnstrack.mybank.Models

import java.util.*

/**
 * Created by John on 5/17/2018 at 2:59 PM.
 */
data class Expense constructor(val category: String, val itemName: String, val price: Double,
                               val timestamp: Date, val username: String)