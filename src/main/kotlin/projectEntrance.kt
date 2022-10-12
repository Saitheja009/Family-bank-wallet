import models.*
import utils.*

var shouldResetContext: Boolean = true
fun main() {
    var shouldRun: Boolean = true
    val bankWallet: BankWallet = BankWallet()
    println(welcomeMsg)
    while (shouldRun) {
        when {
            !Session.isLoggedIn -> {
                // Take credentials and login
                login(bankWallet)
            }

            Session.isLoggedIn -> {
                val shouldStop = handleUserSelection(bankWallet)
                shouldRun = shouldStop
            }
        }
    }
}

fun resetContext() {
    if (shouldResetContext) {
        Session.context.menuSelected = null
    } else {
        shouldResetContext = true
    }
}

fun handleUserSelection(bankWallet: BankWallet): Boolean {
    when (Session.context.menuSelected) {
        NOTIFICATIONS -> {
            showNotifications(bankWallet.getCurrentUserNotifications(), bankWallet)
            resetContext()
        }

        WITHDRAW_MONEY -> {
            checkBalanceAndWithdrawMoney(bankWallet)
            resetContext()
        }

        TRANSFER_MONEY -> {
            checkBalanceAndTransferMoney(bankWallet)
            resetContext()
        }

        SPEND_MONEY -> {
            takeRequiredDetailsAndSpendMoney(bankWallet)
            resetContext()
        }

        TRANSACTIONS -> {
            showTransactions(bankWallet.getAllTransactions())
            resetContext()
        }

        PARENT_BLOCK_FAMILY_MEMBER -> {
            takeRequiredDetailsAndBlockAFamilyMember(bankWallet)
            resetContext()
        }

        EXIT -> {
            return false
        }

        LOGOUT -> {
            bankWallet.logout()
            println(logOutMsg)
        }

        else -> showMenu(bankWallet.getMenu())
    }
    return true
}

fun checkBalanceAndWithdrawMoney(bankWallet: BankWallet) {
    if (bankWallet.getBalance() > 0) {
        longInputReader("How much do you like to withdraw")
            ?.let {
                if (bankWallet.getBalance() >= it) {
                    val bankAccounts = bankWallet.getBankAccounts()
                    if (!bankAccounts.isNullOrEmpty()) {
                        bankAccounts.forEachIndexed { index, bankAccount ->
                            println("${index + 1}. ${bankAccount.name}")
                        }
                        intInputReader("Please select a bank account")?.let { bankIndex ->
                            if (bankIndex < bankAccounts.size)
                            {
                                val isWithdrawComplete = bankWallet.withdrawMoney(bankAccounts[bankIndex - 1], it)
                                if(isWithdrawComplete)
                                {
                                   println(transferSuccessful)
                                    println("Your ${bankAccounts[bankIndex - 1].name} balance is \$ ${bankAccounts[bankIndex - 1].amount}")
                                   println("Your wallet balance is \$ ${bankWallet.getBalance()}")
                                }
                                else{
                                    println("Transaction not possible")
                                }
                            }
                            else{
                                println(invalidResponseMsg)
                            }
                        }
                    } else {
                        println("Transaction not possible as user doesn't have a bank account")
                    }
                }
                else{
                    println("Low balance transaction not possible")
                }
            }
    } else {
        println("Your bank wallet is empty, can't withdraw money")
    }
}

fun checkBalanceAndTransferMoney(bankWallet: BankWallet) {
    val bankAccounts = bankWallet.getBankAccounts()
    if (!bankAccounts.isNullOrEmpty()) {
        printBankAccounts(bankAccounts)
        intInputReader(selectBankAccount)?.let { bankIndex ->
            if (bankIndex <= bankAccounts.size
                && bankIndex > 0
            ) {
                val currentBank = bankAccounts[bankIndex - 1]
                longInputReader(amountToTransfer)?.let { enteredAmount ->
                    if (enteredAmount <= currentBank.amount
                        && enteredAmount > 0
                    ) {
                        val bankAccount = bankWallet.transferMoney(currentBank, enteredAmount)
                        if (bankAccount != null) {
                            println(transferSuccessful)
                            println("Balance in your ${bankAccount.name} is ${bankAccount.amount}")
                            println("Your wallet balance is ${bankWallet.getBalance()}")
                        } else {
                            println(transactionFailed)
                        }
                    } else {
                        println(amountShouldBeLessThanBank)
                    }
                }
            }
        }
    } else {
        println(noBankAccounts)
    }
}

fun takeRequiredDetailsAndBlockAFamilyMember(bankWallet: BankWallet) {
    val usersToBlock = bankWallet.getFamilyMembersToBlock()
    if (usersToBlock.isNotEmpty()) {
        usersToBlock.forEachIndexed { index, user ->
            println("${index + 1} ${user.userName}")
        }
        intInputReader(blockSelectMsg)?.let { userIndex ->
            if (userIndex < usersToBlock.size) {
                bankWallet.blockUser(usersToBlock[userIndex - 1])
                println(blockedUserSuccessfully)
            } else {
                println(invalidResponseMsg)
            }
        }
    } else {
        println(noUsersToBlock)
    }
}

fun showTransactions(transactions: List<Transaction>) {
    if (transactions.isNotEmpty()) {
        println(
            String.format(
                "%5s %10s %10s %10s %10s %20s %15s",
                "S.no",
                "Withdrawn",
                "Deposit",
                "From",
                "To",
                "Done by",
                "Date"
            )
        )
        transactions.forEachIndexed { index, transaction ->
            printTransaction(transaction, withTitle = false, index + 1)
        }
    } else {
        println(noTransactionsFound)
    }
}

fun takeRequiredDetailsAndSpendMoney(bankWallet: BankWallet) {
    if (bankWallet.canTransferMoney()) {
        longInputReader(amountToSend)?.let { amountToSpend ->
            if (amountToSpend > kidTransactionLimit
                || bankWallet.getCurrentUser() is Parent
            ) {
                if (bankWallet.canTransferMoreThanLimit()) {
                    askForOtherDetailsAndInitiateTransaction(amountToSpend, bankWallet)
                } else {
                    raiseRequestForGraterThanKidLimit(bankWallet)
                }
            } else {
                askForOtherDetailsAndInitiateTransaction(amountToSpend, bankWallet)
            }
        }
    } else {
        raiseRequestForMoreTransactions(bankWallet)
    }
}

fun askForOtherDetailsAndInitiateTransaction(amountToSpend: Long, bankWallet: BankWallet) {
    println(whereToSpend)
    val to = readLine()
    to?.let {
        val transaction = bankWallet.spendMoney(amountToSpend, to)
        if (transaction != null) {
            println(transferSuccessful)
            printTransaction(transaction, withTitle = true)
            println("Your wallet balance is ${bankWallet.getBalance()}")
        } else {
            if (bankWallet.getCurrentUser() is Parent) {
                booleanInputReader(lowBalanceTransferMoney)
                    ?.let {
                        if (it) {
                            Session.context.menuSelected = TRANSFER_MONEY
                            shouldResetContext = false
                        } else {
                            println(skippingThatForNow)
                        }
                    }
            } else {
                println(transactionFailedOFLowBalance)
            }
        }
    }
}

fun raiseRequestForGraterThanKidLimit(bankWallet: BankWallet) {
    if (bankWallet.raiseKidRequestForLimitMoneyTransfer()) {
        println(requestForOverLimitTransaction)
    } else {
        println(invalidState)
    }
}

fun raiseRequestForMoreTransactions(bankWallet: BankWallet) {
    booleanInputReader(kidTransactionLimiterMessage)?.let { shouldRaiseRequest ->
        if (shouldRaiseRequest) {
            if (bankWallet.sendKidTransactionRequest()) {
                println(transactionRequestToMother)
            } else {
                println(invalidState)
            }
        } else {
            println(skippingThatForNow)
        }
    }
}

fun showNotifications(currentUserNotifications: List<Notification>, bankWallet: BankWallet) {
    if (currentUserNotifications.isNotEmpty()) {
        currentUserNotifications.forEachIndexed { index, notification ->
            println("${index + 1}. ${notification.message}")
        }
        askForNotificationInteraction(currentUserNotifications, bankWallet)
    } else {
        println(noNotifications)
    }
}

fun askForNotificationInteraction(currentUserNotifications: List<Notification>, bankWallet: BankWallet) {
    println(notificationActionMsg)
    val action = readLine()
    if (!action.isNullOrEmpty()) {
        if (action == "#") {
            Session.context = Context()
        } else {
            try {
                val notificationIndex = action.toInt() - 1
                val notification = currentUserNotifications[notificationIndex]
                when (notification.action) {
                    NotificationAction.NORMAL -> {
                        shouldMarkNotificationAsRead(notification, bankWallet)
                    }

                    NotificationAction.LOW_WALLET_BALANCE -> {
                        shouldTransferMoney(bankWallet)
                    }

                    NotificationAction.OVER_LIMIT_PERMISSION -> {
                        shouldGivePermissionForOverLimit(bankWallet, notification)
                    }

                    NotificationAction.PERMISSION_WALLET_USE_TWICE -> {
                        shouldProvidePermissionToUseWalletTwice(bankWallet, notification)
                    }

                    else -> println(invalidState)
                }
            } catch (e: Exception) {
                println(invalidResponseMsg)
            }
        }
    }
}

fun shouldProvidePermissionToUseWalletTwice(bankWallet: BankWallet, notification: Notification) {
    booleanInputReader(providePermissionForTwiceWallet)?.let { response ->
        if (response) {
            if (bankWallet.providePermissionToUseWalletTwice(notification)) {
                println(providedPermissionForUsingWallet)
            } else {
                println(skippingThatForNow)
            }
        }
    }
}

fun shouldGivePermissionForOverLimit(bankWallet: BankWallet, notification: Notification) {
    booleanInputReader("Do you want to provide permission for ${notification.sentBy} to spend more than ${kidTransactionLimit - 1}")?.let {
        if (it) {
            if (bankWallet.providePermissionForKidLimitTransaction(notification)) {
                println(providedPermissionForOverLimitTransaction)
            } else {
                println(skippingThatForNow)
            }
        } else {
            booleanInputReader("Do you want to transfer this to kid father")?.let { transferToFather ->
                if (transferToFather) {
                    bankWallet.transferOverLimitPermissionToFather(notification)
                    println(transferredRequestToFather)
                }
            }
        }
    }
}

fun shouldTransferMoney(bankWallet: BankWallet) {
    booleanInputReader(transferMoneyConsent)?.let {
        if (it) {
            checkBalanceAndTransferMoney(bankWallet)
        }
    }
}

fun shouldMarkNotificationAsRead(notification: Notification, bankWallet: BankWallet) {
    booleanInputReader(shouldDiscardNotification)?.let {
        if (it) {
            bankWallet.markNotificationsAsRead(notification)
            println(markedNotificationAsMarked)
        } else {
            println(skippingThatForNow)
        }
    }
}


fun showMenu(menu: Map<Int, String>) {
    println()
    printAndSelectMenuHelper(menu)
}

fun login(bankWallet: BankWallet) {
    println(loginFallBackMessage)
    println(loginUserMsg)
    val userName = readLine()
    if (!userName.isNullOrEmpty()
        && !bankWallet.isUserBlocked(userName)
    ) {
        println(loginPasswordMsg)
        val password = readLine()
        if (!password.isNullOrEmpty()
        ) {
            val isLoggedIn = bankWallet.login(userName, password)
            if (isLoggedIn) {
                println(loginSuccessful)
                showNotifications(bankWallet.getCurrentUserNotifications(), bankWallet)
            } else {
                println(loginFailed)
            }
        }
    } else {
        println(blockedMsg)
    }
}

fun printAndSelectMenuHelper(menu: Map<Int, String>) {
    val menuMap = mutableListOf<Int>()
    var index = 0
    for (menuItem in menu) {
        menuMap.add(menuItem.key)
        println("${index + 1}. ${menuItem.value}")
        index++
    }
    println(selectMenu)
    intInputReader()?.let {
        if (it > 0
            && it <= menuMap.size
        ) {
            Session.context.menuSelected = menuMap[it - 1]
        } else {
            println(wrongMenuSelected)
        }
    }
}