package com.efremovkirill.securedchat.data

data class ChatOptions(
    var id: String,
    var password: String,

    var capacity: String,
    var timer: String,

    var onlyInvite: Boolean = false,
    var saveAfterClear: Boolean,
    var screenshotsAllowed: Boolean,
    var copyAllowed: Boolean
)
