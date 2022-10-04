package models

import java.util.*

abstract class User {
    abstract val userName: String
    abstract val password: String
}

enum class Gender {
    MALE,
    FEMALE
}

open class Parent(
    override val userName: String,
    override val password: String,
    val gender: Gender,
    open val bankAccount: List<BankAccount>
) : User()

data class Mother(
    override val userName: String,
    override val password: String,
    override val bankAccount: List<BankAccount>
) : Parent(
    userName = userName,
    password = userName,
    gender = Gender.FEMALE,
    bankAccount = bankAccount
)

data class Father(
    override val userName: String,
    override val password: String,
    override val bankAccount: List<BankAccount>
) : Parent(
    userName = userName,
    password = userName,
    gender = Gender.MALE,
    bankAccount = bankAccount
)

data class Kid(
    override val userName: String,
    override val password: String,
    var lastTransactionTime: Date? = null,
    val mother:String,
    val father: String,
    var canTransferMoreThenLimit:Boolean = false
) : User()
