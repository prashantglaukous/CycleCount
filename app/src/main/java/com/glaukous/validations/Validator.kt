package com.glaukous.validations

interface Validator {
    fun isValid(): Boolean
    fun message(): String?
}