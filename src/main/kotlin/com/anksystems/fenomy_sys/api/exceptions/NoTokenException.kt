package com.anksystems.fenomy_sys.api.exceptions

class NoTokenException(token: String): Exception("Token not found: $token") {
}