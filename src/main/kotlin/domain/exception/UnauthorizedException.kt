package su.kawunprint.domain.exception

import su.kawunprint.utils.Constants

class UnauthorizedException : Exception {
    constructor() : super(Constants.ErrorMessages.UNAUTHORIZED)
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}