package com.github.myraBot.diskord.rest.request.error

class EntityModifyException(message: String?) : Exception(message)
class BadReqException(message: String?) : Exception(message)
class MissingPermissionsException(message: String?) : Exception(message)
class RateLimitException(message: String?) : Exception(message)