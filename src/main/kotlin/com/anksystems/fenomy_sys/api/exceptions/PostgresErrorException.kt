package com.anksystems.fenomy_sys.api.exceptions

class PostgresErrorException(error: String):
    Exception("Postgresql error: $error") {
}