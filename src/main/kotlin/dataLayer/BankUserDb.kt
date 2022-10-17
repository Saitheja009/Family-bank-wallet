package dataLayer

import models.*

class BankUserDb {
    val users = listOf<User>(
        Mother(
            userName = "Mother",
            password = "123",
            bankAccount = listOf(
                BankAccount(
                    name = "Bank of Montreal",
                    amount = 10000
                ),
                BankAccount(
                    name = "SCOTIA BANK",
                    amount = 20000
                ),
                BankAccount(
                    name = "CIBC",
                    amount = 20000
                )
            )
        ),
        Father(
            userName = "Father",
            password = "123",
            bankAccount = listOf(
                BankAccount(
                    name = "TD",
                    amount = 10000
                ),
                BankAccount(
                    name = "RBC",
                    amount = 20000
                )
            )
        ),
        Kid(
            userName = "Saitheja",
            password = "123",
            mother = "Mother",
            father = "Father",
        ),
        Kid(
            userName = "Kid2",
            password = "123",
            mother = "Mother",
            father = "Father",
        ),
        Kid(
            userName = "Kid3",
            password = "123",
            mother = "Mother",
            father = "Father",
        ),
        Kid(
            userName = "Kid4",
            password = "123",
            mother = "Mother",
            father = "Father",
        ),
        Kid(
            userName = "Kid5",
            password = "123",
            mother = "Mother",
            father = "Father",
        ),
        Kid(
            userName = "Kid6",
            password = "123",
            mother = "Mother",
            father = "Father",
        ),
        Kid(
            userName = "Kid7",
            password = "123",
            mother = "Mother",
            father = "Father",
        ),
        Kid(
            userName = "Kid8",
            password = "123",
            mother = "Mother",
            father = "Father",
        ),

    )
    val notifications = mutableListOf<Notification>()
    val bankAccount: BankAccount = BankAccount(amount = 1000, name = "WALLET")
    val transactions = mutableListOf<Transaction>()
    val blockedMembers = mutableListOf<User>()
}