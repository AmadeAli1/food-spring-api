package com.amade.api.exception

data class ErrorMessage(
    val message: String? = null,
    val requestStatus: Int? = null,
    var erro: ErrorAtribute,
) {

    data class ErrorAtribute(
        val field: String? = null,
        val message: String? = null,
    )

}