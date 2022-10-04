package models

import java.util.*

enum class NotificationAction{
    PERMISSION_WALLET_USE_TWICE,
    LOW_WALLET_BALANCE,
    OVER_LIMIT_PERMISSION,
    NORMAL
}

data class Notification(
    val message: String,
    val action: NotificationAction? = NotificationAction.NORMAL,
    var time: Date = Date(),
    var markAsRead: Boolean = false,
    var belongsTo: String,
    var sentBy: String
)