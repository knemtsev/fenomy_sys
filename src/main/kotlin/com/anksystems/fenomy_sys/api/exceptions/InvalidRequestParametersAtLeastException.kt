package com.anksystems.fenomy_sys.api.exceptions

class InvalidRequestParametersAtLeastException(fields: String):
    Exception("At least one of the fields $fields must be specified") {
}