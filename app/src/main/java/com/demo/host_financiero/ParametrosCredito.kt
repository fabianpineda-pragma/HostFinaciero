package com.demo.host_financiero

/**
 * Las condiciones del crédito que la app nativa le pasará al módulo Flutter.
 *
 * Los nombres de los campos son los del contrato del canal
 * (`initializeSimulator`), así que [comoMapa] se puede enviar tal cual cuando
 * se conecte el módulo.
 */
data class ParametrosCredito(
    val amount: Long,
    val monthlyInterestRate: Double,
    val termMonths: Int,
    val currency: String,
) {

    /** El mapa que espera Dart. */
    fun comoMapa(): Map<String, Any> = mapOf(
        "amount" to amount,
        "monthlyInterestRate" to monthlyInterestRate,
        "termMonths" to termMonths,
        "currency" to currency,
    )

    /** El mismo mapa, formateado para mostrarlo en pantalla. */
    fun comoTextoLegible(): String = comoMapa()
        .entries
        .joinToString(
            separator = ",\n",
            prefix = "{\n",
            postfix = "\n}",
        ) { (clave, valor) ->
            val formateado = if (valor is String) "\"$valor\"" else valor.toString()
            "  \"$clave\": $formateado"
        }
}
