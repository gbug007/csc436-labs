package com.zybooks.ai4sarapp.data

import com.google.gson.JsonElement

data class IncidentDocument(
    val id: String,
    val data: IncidentData
)

data class IncidentData(
    val viewableBy: List<String> = emptyList(),

    val incidentName: String? = null,
    val incidentNumber: String? = null,
    val incidentDate: String? = null,
    val oesIncidentNumber: String? = null,

    val sheriffName: String? = null,
    val sheriffPhoneNumber: String? = null,

    val commandPostTelephone: String? = null,
    val initialRadioChannel: String? = null,
    val commandPostLocation: String? = null,

    // 👇 Mixed types in your JSON: sometimes "" (string), sometimes -90 / 180 (numbers)
    val commandPostLatitude: JsonElement? = null,
    val commandPostLongitude: JsonElement? = null,

    val missingPersonName: String? = null,
    val missingPersonAge: String? = null,
    val missingPersonSex: String? = null,
    val missingPersonPls: String? = null,
    val missingPersonPlsLatitude: String? = null,
    val missingPersonPlsLongitude: String? = null,
    val missingPersonAlert: String? = null,

    val reportingPersonName: String? = null,
    val reportingPersonPhone: String? = null,
    val reportingPersonAddress: String? = null,

    val incidentPreparedBy: String? = null,
    val incidentDatePrepared: String? = null,
    val incidentTimePrepared: String? = null,
    val incidentPreparedData: String? = null,

    val createdBy: String? = null,
    val submitted: Boolean? = null,
    val uid: String? = null,
    val timestamp: String? = null,

    val author: List<String> = emptyList(),

    val photoURL: String? = null
)

data class FormDocument(
    val id: String,
    val data: Map<String, JsonElement> // or a strongly-typed FormData if you know fields
)
