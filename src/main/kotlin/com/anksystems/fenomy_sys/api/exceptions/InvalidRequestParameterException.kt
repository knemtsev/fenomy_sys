package com.anksystems.fenomy_sys.api.exceptions

class InvalidRequestParameterException(parameter: String, override val message: String?):
    Exception("Parameter $parameter error: $message") {
}