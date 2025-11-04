package su.kawunprint.data.model

import su.kawunprint.utils.Constants

enum class RoleModel {
    ADMIN, EMPLOYEE ,CLIENT
}

fun String.getRoleByString(): RoleModel {
    return when(this) {
        Constants.Role.ADMIN -> RoleModel.ADMIN
        Constants.Role.EMPLOYEE -> RoleModel.EMPLOYEE
        else -> RoleModel.CLIENT
    }
}

fun RoleModel.getStringByRole(): String {
    return when(this) {
        RoleModel.CLIENT -> Constants.Role.CLIENT
        RoleModel.ADMIN -> Constants.Role.ADMIN
        RoleModel.EMPLOYEE -> Constants.Role.EMPLOYEE
    }
}