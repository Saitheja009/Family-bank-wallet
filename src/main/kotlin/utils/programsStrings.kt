package utils

import models.BankAccount
import models.Transaction
import java.util.*

internal const val welcomeMsg = "Welcome to Money-Money bank wallet"
internal const val loginFallBackMessage = "Please login to start banking"
internal const val loginUserMsg = "Please enter your user name"
internal const val loginPasswordMsg = "Please enter your password"
internal const val loginSuccessful = "Login successful!!!"
internal const val loginFailed = "Login failed"
internal const val selectMenu = "Please select a menu option"
internal const val wrongMenuSelected = "Please select a correct menu item"
internal const val invalidState = "Invalid state"
internal const val notificationActionMsg = "Please select a notification to take action or press # to continue"
internal const val shouldDiscardNotification = "Do you want to mark notification as read"
internal const val noNotifications = "Sorry you don't have any notifications"
internal const val kidTransactionLimiterMessage =
    "Sorry you can't transfer money today. \nDo you like to ask your mother permission"
internal const val amountToSend = "How much do you like to spend"
internal const val whereToSpend = "Who are you sending it to"
internal const val invalidResponseMsg = "Please give a valid response"
internal const val transactionFailedOFLowBalance =
    "Sorry not able to do transaction because of low balance,\nwe have sent notification to your Mother\nplease try again after some time"
internal const val lowBalanceTransferMoney = "You have low balance, do you want to add money  to your wallet"
internal const val noTransactionsFound = "No transactions not found"
internal const val blockSelectMsg = "Please select a family member to block"
internal const val noUsersToBlock = "Found no users to block"
internal const val selectBankAccount = "Please select bank account to transfer money"
internal const val noBankAccounts = "You don't have any bank accounts to transfer money from"
internal const val validSelection = "Please enter a valid selection"
internal const val amountToTransfer = "Please enter amount you want to transfer"
internal const val amountShouldBeLessThanBank = "Amount exceeds your bank wallet balance"
internal const val transferSuccessful = "Transfer successful"
internal const val transferMoneyConsent = "Do you want to transfer money"
internal const val limitPermissionMsg = "Raised a permission request to your mother for the transaction"
internal const val providePermissionForTwiceWallet = "Do you want to provide permission to use wallet twice"
internal const val yesNoMsg = "1. Yes\n2. No"
internal const val transactionRequestToMother = "Request for transaction is sent to your mother"
internal const val skippingThatForNow = "Sure, we are skipping that for now"
internal const val wallet = "WALLET"
internal const val markedNotificationAsMarked = "Successfully marked notification as read"
internal const val transactionFailed = "Transaction Failed!"
internal const val requestForOverLimitTransaction = "Raised a permission request to your mother for the transaction"
internal const val logOutMsg = "Logout successful"
internal const val providedPermissionForOverLimitTransaction = "Permission provided for overLimit transaction"
internal const val transferredRequestToFather = "Transferred request to father"
internal const val providedPermissionForUsingWallet = "Permission provided for using wallet"
internal const val blockedUserSuccessfully = "Blocked user successfully"
internal const val blockedMsg = "You are blocked from using the wallet\nPlease try again after sometime"

// Common Menu specific
internal const val NOTIFICATIONS = 1
internal const val TRANSFER_MONEY = 2
internal const val SPEND_MONEY = 3
internal const val TRANSACTIONS = 4
internal const val EXIT = 6
internal const val LOGOUT = 7
internal const val WITHDRAW_MONEY = 8

// Parent specific
internal const val PARENT_BLOCK_FAMILY_MEMBER = 5

// Kid specific
internal const val kidTransactionLimit = 51

// Balance Low Limit
internal const val bankBalanceLowLimit = 100


internal val kidMenu: Map<Int, String> = mapOf(
    NOTIFICATIONS to "Notifications",
    SPEND_MONEY to "Spend Money",
    TRANSACTIONS to "View Transactions",
    LOGOUT to "Logout",
    EXIT to "Exit"
)

val motherMenu: Map<Int, String> = mapOf(
    NOTIFICATIONS to "Notifications",
    WITHDRAW_MONEY to "Withdraw money",
    TRANSFER_MONEY to "Money Transfer",
    SPEND_MONEY to "Spend Money",
    TRANSACTIONS to "View transactions",
    LOGOUT to "Logout",
    EXIT to "Exit"
)

val fatherMenu: Map<Int, String> = mapOf(
    NOTIFICATIONS to "Notifications",
    WITHDRAW_MONEY to "Withdraw money",
    TRANSFER_MONEY to "Money Transfer",
    TRANSACTIONS to "View transactions",
    SPEND_MONEY to "Spend Money",
    PARENT_BLOCK_FAMILY_MEMBER to "Block a family member",
    LOGOUT to "Logout",
    EXIT to "Exit"
)

fun getDateInHumanReadableFormat(date: Date): String {
    return date.toString()
}

fun booleanInputReader(question: String? = null): Boolean? {
    if (!question.isNullOrEmpty()) {
        println(question)
        println(yesNoMsg)
    }
    intInputReader()?.let {
        when (it) {
            1 -> {
                return true
            }

            2 -> {
                return false
            }

            else -> {
                println(invalidResponseMsg)
                return null
            }
        }
    }
    println(invalidResponseMsg)
    return null
}

fun longInputReader(question: String? = null): Long? {
    if (!question.isNullOrEmpty()) println(question)
    val userInput = readLine()
    return if (!userInput.isNullOrEmpty()) {
        try {
            userInput.toLong()
        } catch (e: Exception) {
            null
        }
    } else {
        println(invalidResponseMsg)
        null
    }
}

fun intInputReader(question: String? = null): Int? {
    if (!question.isNullOrEmpty()) println(question)
    val userInput = readLine()
    return if (!userInput.isNullOrEmpty()) {
        try {
            userInput.toInt()
        } catch (e: Exception) {
            null
        }
    } else {
        println(invalidResponseMsg)
        null
    }
}

fun printTransaction(transaction: Transaction, withTitle: Boolean = false, SNo: Int = 1) {
    if (withTitle) {
        println(
            String.format(
                "%5s %10s %10s %20s %10s %20s %20s",
                "S.no",
                "Withdrawn",
                "Deposit",
                "From",
                "To",
                "Done by",
                "Date"
            )
        )
    }
    println(
        String.format(
            "%5d %10s %10s %20s %10s %20s %20s",
            SNo,
            (transaction.withdrawn ?: 0).toString(),
            (transaction.deposit ?: 0).toString(),
            transaction.from ?: "---",
            transaction.to ?: "---",
            transaction.doneBy ?: "---",
            getDateInHumanReadableFormat(transaction.doneOn)
        )
    )
}

fun printBankAccounts(bankAccounts: List<BankAccount>) {
    println(
        String.format(
            "%5s %20s %10s",
            "S.no",
            "Bank name",
            "Balance"
        )
    )
    bankAccounts.forEachIndexed { index, bankAccount ->
        println(
            String.format(
                "%5d %20s %10s",
                index + 1,
                bankAccount.name,
                bankAccount.amount,
            )
        )
    }
}