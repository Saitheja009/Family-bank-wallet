import dataLayer.BankUserDb
import models.*
import utils.*
import utils.kidMenu
import utils.kidTransactionLimit
import java.util.Date

class BankWallet {
    private val bankUserDb = BankUserDb()

    fun login(userName: String, password: String): Boolean {
        val user = bankUserDb.users.filter {
            it.userName == userName && it.password == password
        }
        return if (user.isNotEmpty()) {
            Session.user = user[0]
            Session.isLoggedIn = true
            true
        } else {
            false
        }
    }

    fun getCurrentUserNotifications(): List<Notification> {
        return bankUserDb.notifications.filter { it.belongsTo == Session.user?.userName && !it.markAsRead }
    }

    fun getMenu(): Map<Int, String> {
        return when (Session.user) {
            is Father -> {
                fatherMenu
            }

            is Mother -> {
                motherMenu
            }

            else -> {
                kidMenu
            }
        }
    }

    fun markNotificationsAsRead(notification: Notification) {
        notification.markAsRead = true
    }

    fun canTransferMoney(): Boolean {
        return if (Session.user is Kid) {
            val kid = Session.user as Kid
            if (kid.lastTransactionTime == null) {
                true
            } else kid.lastTransactionTime!! < Date()
                    && kid.lastTransactionTime!!.day < Date().day
        } else {
            // Assuming parent can always transfer money
            true
        }
    }

    fun canTransferMoreThanLimit(): Boolean {
        return if (Session.user is Kid) {
            val kid = Session.user as Kid
            kid.canTransferMoreThenLimit
        } else {
            // Assuming parent can always transfer money
            true
        }
    }

    fun sendKidTransactionRequest(): Boolean {
        if (Session.user is Kid) {
            val kid = Session.user as Kid
            bankUserDb.notifications.add(
                Notification(
                    message = "Transaction request from ${kid.userName}",
                    belongsTo = kid.mother,
                    sentBy = kid.userName,
                    action = NotificationAction.PERMISSION_WALLET_USE_TWICE

                )
            )
            return true
        }
        else{
            return false
        }
    }

    fun spendMoney(amount: Long, to: String): Transaction? {
        return if (isTransactionPossible(amount)) {
            val transaction = Transaction(
                withdrawn = amount,
                from = wallet,
                to = to,
                doneBy = Session.user?.userName.toString(),
                doneOn = Date()
            )
            bankUserDb.transactions.add(
              transaction
            )
            if (Session.user is Kid) {
                for (user in bankUserDb.users) {
                    if (user.userName == Session.user?.userName
                        && user is Kid
                    ) {
                        if (amount >= kidTransactionLimit) user.canTransferMoreThenLimit = false
                        user.lastTransactionTime = Date()
                        break
                    }
                }
            }

            bankUserDb.bankAccount.amount -= amount
            if(Session.user is Kid)
            {
                sendNotificationToParentIfBalanceIsLow()
            }
            transaction
        } else {
            null
        }
    }

    fun getCurrentUser(): User? {
        return Session.user
    }

    fun getBalance(): Long {
        return bankUserDb.bankAccount.amount
    }

    fun getAllTransactions(): List<Transaction> {
        return if (Session.user is Parent)
            bankUserDb.transactions
        else
            bankUserDb.transactions.filter {
                it.doneBy == Session.user?.userName
            }
    }

    fun getFamilyMembersToBlock(): List<User> {
        return bankUserDb.users.filter { it !is Father }
    }

    private fun sendNotificationToParentIfBalanceIsLow()
    {
        if(bankUserDb.bankAccount.amount <= bankBalanceLowLimit)
        {
            if(Session.user is Kid)
            {
                val kid = (Session.user as Kid)
                bankUserDb.notifications.add(
                    Notification(
                        message = "Wallet balance is less than \$ 100",
                        belongsTo = kid.mother,
                        sentBy = Session.user?.userName.toString()
                    )
                )
                bankUserDb.notifications.add(
                    Notification(
                        message = "Wallet balance is less than \$ 100",
                        belongsTo = kid.father,
                        sentBy = Session.user?.userName.toString()
                    )
                )
            }
        }
    }

    private fun isTransactionPossible(amount: Long): Boolean {
        return if (bankUserDb.bankAccount.amount > amount) {
            true
        } else {
            sendNotificationToParentIfBalanceIsLow()
            false
        }
    }

    fun blockUser(user: User) {
        bankUserDb.blockedMembers.add(user)
    }

    fun getBankAccounts(): List<BankAccount>? {
        return if (Session.user is Parent) {
            val user = bankUserDb.users.filter {
                it.userName == Session.user?.userName
            }

            if (user.isNotEmpty()
                && user[0] is Parent
            ) {
                (user[0] as Parent).bankAccount
            } else {
                null
            }
        } else {
            null
        }
    }

    fun transferMoney(currentBank: BankAccount, enteredAmount: Long):BankAccount? {
        currentBank.amount.minus(enteredAmount)
        bankUserDb.transactions.add(
            Transaction(
                deposit = enteredAmount,
                from = currentBank.name,
                to = wallet,
                doneBy = Session.user?.userName.toString(),
                doneOn = Date()
            )
        )
        bankUserDb.bankAccount.amount += enteredAmount
        currentBank.amount -= enteredAmount
        return currentBank
    }

    fun raiseKidRequestForLimitMoneyTransfer(): Boolean {
        return if (Session.user is Kid) {
            val kid = (Session.user as Kid)
            bankUserDb.notifications.add(
                Notification(
                    "Permission to spend more than \$ ${kidTransactionLimit - 1}",
                    action = NotificationAction.OVER_LIMIT_PERMISSION,
                    belongsTo = kid.mother,
                    sentBy = kid.userName
                )
            )
            true
        }
        else{
            false
        }
    }

    fun providePermissionForKidLimitTransaction(notification: Notification):Boolean {
        for (user in bankUserDb.users) {
            if (user is Kid
                && user.userName == notification.sentBy
            ) {
                user.canTransferMoreThenLimit = true
                notification.markAsRead = true
                break
            }
        }
        return true
    }

    fun providePermissionToUseWalletTwice(notification: Notification): Boolean {
        for (user in bankUserDb.users) {
            if (user.userName == notification.sentBy
                && user is Kid
            ) {
                notification.markAsRead = true
                user.lastTransactionTime = null
                break
            }
        }
        return true
    }

    fun transferOverLimitPermissionToFather(notification: Notification)
    {
        val kid = bankUserDb.users.first { it.userName == notification.sentBy }
        if(kid is Kid)
        {
            notification.belongsTo = kid.father
            notification.time = Date()
        }
    }

    fun logout() {
        Session.user = null
        Session.isLoggedIn = false
        Session.context = Context()
    }

    fun isUserBlocked(userName: String): Boolean {
        return if(bankUserDb.blockedMembers.isNotEmpty())
        {
           try {
               bankUserDb.blockedMembers.first {
                   it.userName == userName
               }
               true
           }
           catch(e: Exception) {
               false
           }
        }
        else
        {
            false
        }
    }

    fun withdrawMoney(bankAccount: BankAccount, moneyToWithdraw: Long):Boolean {
        return if(Session.user is Parent) {
            bankAccount.amount += moneyToWithdraw
            bankUserDb.bankAccount.amount -= moneyToWithdraw
            bankUserDb.transactions.add(
                Transaction(
                    withdrawn = moneyToWithdraw,
                    from = "WALLET",
                    to = bankAccount.name,
                    doneOn = Date(),
                    doneBy = Session.user?.userName?:"-"
                )
            )
            true
        } else{
            false
        }
    }
}