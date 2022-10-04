package models

enum class PermissionState {
    GRANTED,
    REJECTED,
    PENDING
}

data class Permission(
    var shouldGrant: PermissionState = PermissionState.PENDING
)
