package com.anksystems.fenomy_sys.api.request

import com.anksystems.fenomy_sys.api.exceptions.InvalidRequestParameterException
import java.time.ZonedDateTime

@kotlinx.serialization.Serializable
abstract class BaseRequest(
    val limit: Int? = null,
    val offset: Int? = null,
    val after: String? = null
) {
    fun afterToTime(): ZonedDateTime? = toTime(after)

    companion object {
        fun toTime(time: String?): ZonedDateTime? =
            time?.let {
                try {
                    ZonedDateTime.parse(it)
                } catch (e: Exception) {
                    throw InvalidRequestParameterException("after", e.message)
                }
            }
    }
}