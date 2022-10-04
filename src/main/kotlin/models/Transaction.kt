package models

import java.util.*

data class Transaction(
    val withdrawn: Long? = null,
    val deposit: Long? = null,
    val from: String,
    val to: String,
    val doneBy: String,
    val doneOn: Date
)
