package com.emirgasic.forecastfm.core.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatIsoDateTime(iso: String): String {
    return try {
        val instant = Instant.parse(iso)
        DateTimeFormatter
            .ofPattern("d MMM yyyy, HH:mm")
            .withZone(ZoneId.systemDefault())
            .format(instant)
    } catch (e: Exception) {
        iso
    }
}

fun formatIsoDate(iso: String): String {
    return try {
        val instant = Instant.parse(iso)
        DateTimeFormatter
            .ofPattern("d MMM yyyy")
            .withZone(ZoneId.systemDefault())
            .format(instant)
    } catch (e: Exception) {
        iso
    }
}