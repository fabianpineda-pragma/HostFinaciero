package com.demo.host_financiero

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.demo.host_financiero.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

/**
 * Pantalla nativa de "Mi Banco": el formulario con las condiciones del
 * crédito que más adelante recibirá el módulo Flutter.
 *
 * Hoy esta app **no conoce a Flutter**. El botón solo valida el formulario y
 * muestra los parámetros que viajarían por el canal. Conectar el módulo es
 * trabajo de los pasos 4 y 5 de la integración, y se hace a mano.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarSelectorDePlazo()

        binding.botonAbrir.setOnClickListener { abrirSimulador() }
        binding.botonLimpiar.setOnClickListener { restablecer() }

        mostrarParametros(leerFormulario())
    }

    private fun configurarSelectorDePlazo() {
        val etiquetas = PLAZOS.map { "$it ${getString(R.string.sufijo_meses)}" }
        binding.selectorPlazo.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            etiquetas,
        )
        binding.selectorPlazo.setSelection(PLAZOS.indexOf(PLAZO_INICIAL))
    }

    /**
     * Lee y valida el formulario. Devuelve null si algo no cuadra, dejando el
     * mensaje de error en el campo correspondiente.
     */
    private fun leerFormulario(): ParametrosCredito? {
        var valido = true

        val monto = binding.inputMonto.text?.toString()?.toLongOrNull()
        if (monto == null || monto < MONTO_MINIMO || monto > MONTO_MAXIMO) {
            binding.contenedorMonto.error = getString(R.string.error_monto)
            valido = false
        } else {
            binding.contenedorMonto.error = null
        }

        val tasa = binding.inputTasa.text?.toString()
            ?.replace(',', '.')
            ?.toDoubleOrNull()
        if (tasa == null || tasa < 0.0 || tasa > TASA_MAXIMA) {
            binding.contenedorTasa.error = getString(R.string.error_tasa)
            valido = false
        } else {
            binding.contenedorTasa.error = null
        }

        if (!valido) return null

        return ParametrosCredito(
            amount = monto!!,
            monthlyInterestRate = tasa!!,
            termMonths = PLAZOS[binding.selectorPlazo.selectedItemPosition],
            currency = binding.inputMoneda.text?.toString().orEmpty(),
        )
    }

    private fun abrirSimulador() {
        val parametros = leerFormulario()
        if (parametros == null) {
            mostrarParametros(null)
            return
        }

        mostrarParametros(parametros)
        Snackbar.make(
            binding.root,
            getString(R.string.aviso_payload),
            Snackbar.LENGTH_SHORT,
        ).show()

        // El paso 5 de la integración reemplaza esta línea por la apertura de
        // la pantalla Flutter con el engine precalentado.
    }

    private fun restablecer() {
        binding.inputMonto.setText(MONTO_INICIAL.toString())
        binding.inputTasa.setText(TASA_INICIAL.toString())
        binding.selectorPlazo.setSelection(PLAZOS.indexOf(PLAZO_INICIAL))
        binding.contenedorMonto.error = null
        binding.contenedorTasa.error = null
        mostrarParametros(leerFormulario())
    }

    /**
     * Pinta el mapa tal como lo recibiría Dart, para que durante la charla se
     * vea que los dos lados hablan del mismo contrato.
     */
    private fun mostrarParametros(parametros: ParametrosCredito?) {
        binding.textoPayload.text = parametros?.comoTextoLegible()
            ?: "{\n  // corrige el formulario\n}"
    }

    private companion object {
        val PLAZOS = listOf(6, 12, 18, 24, 36, 48, 60)
        const val PLAZO_INICIAL = 24
        const val MONTO_INICIAL = 10_000_000L
        const val TASA_INICIAL = 1.5
        const val MONTO_MINIMO = 500_000L
        const val MONTO_MAXIMO = 100_000_000L
        const val TASA_MAXIMA = 10.0
    }
}
