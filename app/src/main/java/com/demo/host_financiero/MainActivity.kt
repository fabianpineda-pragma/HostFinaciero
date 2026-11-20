package com.demo.host_financiero

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doAfterTextChanged
import com.demo.host_financiero.databinding.ActivityMainBinding
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.Locale

/**
 * Pantalla nativa de "Mi Banco": el formulario con las condiciones del
 * crédito que más adelante recibirá el módulo Flutter.
 *
 * Hoy esta app **no conoce a Flutter**: es una app nativa completa por sí
 * sola. El botón valida el formulario y muestra los parámetros que viajarían
 * por el canal. Conectar el módulo es trabajo de los pasos A1 a A6 de la
 * parte práctica, y se hace a mano.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var plazo = PLAZO_INICIAL
    private val miles = NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-CO"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aplicarBordeABorde()

        configurarEtiquetas()
        configurarPlazos()
        actualizarAlEscribir()

        binding.botonAbrir.setOnClickListener { abrirSimulador() }
        binding.botonLimpiar.setOnClickListener { restablecer() }

        mostrarParametros(leerFormulario())
    }

    /**
     * Con targetSdk 35+ Android dibuja de borde a borde: el banner ocupa la
     * zona de la barra de estado y el contenido deja libre la de navegación.
     */
    private fun aplicarBordeABorde() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.raiz) { _, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.banner.root.updatePadding(top = barras.top)
            binding.scroll.updatePadding(bottom = barras.bottom)
            insets
        }
    }

    private fun configurarEtiquetas() {
        binding.etiquetaMonto.etiqueta.setText(R.string.label_monto)
        binding.etiquetaTasa.etiqueta.setText(R.string.label_tasa)
        binding.etiquetaPlazo.etiqueta.setText(R.string.label_plazo)
        binding.etiquetaMoneda.etiqueta.setText(R.string.label_moneda)
        binding.etiquetaMoneda.valor.text = MONEDA
    }

    /** Chips de plazo, iguales a los del simulador Flutter. */
    private fun configurarPlazos() {
        for (meses in PLAZOS) {
            val chip = Chip(this, null, 0).apply {
                setChipDrawable(
                    com.google.android.material.chip.ChipDrawable.createFromAttributes(
                        context, null, 0, R.style.ChipPlazoPragma
                    )
                )
                setTextAppearance(R.style.TextoChipPragma)
                text = meses.toString()
                id = meses
                isCheckable = true
                isChecked = meses == plazo
            }
            binding.grupoPlazos.addView(chip)
        }
        binding.grupoPlazos.setOnCheckedStateChangeListener { _, ids ->
            plazo = ids.firstOrNull() ?: plazo
            mostrarParametros(leerFormulario())
        }
    }

    /** La vista previa del canal se actualiza mientras se edita el formulario. */
    private fun actualizarAlEscribir() {
        binding.inputMonto.doAfterTextChanged { mostrarParametros(leerFormulario()) }
        binding.inputTasa.doAfterTextChanged { mostrarParametros(leerFormulario()) }
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

        binding.etiquetaMonto.valor.text = monto?.let { "$" + miles.format(it) } ?: "—"
        binding.etiquetaTasa.valor.text = tasa?.let { "$it% mensual" } ?: "—"
        binding.etiquetaPlazo.valor.text = getString(R.string.valor_plazo, plazo)

        if (!valido) return null

        return ParametrosCredito(
            amount = monto!!,
            monthlyInterestRate = tasa!!,
            termMonths = plazo,
            currency = MONEDA,
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

        // El paso A6 de la parte práctica agrega aquí el envío de los
        // parámetros por el canal y la apertura de la pantalla Flutter.
    }

    private fun restablecer() {
        binding.inputMonto.setText(MONTO_INICIAL.toString())
        binding.inputTasa.setText(TASA_INICIAL.toString())
        binding.grupoPlazos.check(PLAZO_INICIAL)
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
        const val MONEDA = "COP"
    }
}
