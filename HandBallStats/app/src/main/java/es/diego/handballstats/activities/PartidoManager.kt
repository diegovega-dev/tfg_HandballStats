package es.diego.handballstats.activities

import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.diego.handballstats.models.Estadistica
import es.diego.handballstats.models.Jugador
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PartidoManager : ViewModel() {

    private val _extremoIzq = MutableLiveData<Jugador>()
    val extremoIzq: LiveData<Jugador> = _extremoIzq

    private val _pivote = MutableLiveData<Jugador>()
    val pivote: LiveData<Jugador> = _pivote

    private val _extremoDch = MutableLiveData<Jugador>()
    val extremoDch: LiveData<Jugador> = _extremoDch

    private val _lateralIzq = MutableLiveData<Jugador>()
    val lateralIzq: LiveData<Jugador> = _lateralIzq

    private val _central = MutableLiveData<Jugador>()
    val central: LiveData<Jugador> = _central

    private val _lateralDch = MutableLiveData<Jugador>()
    val lateralDch: LiveData<Jugador> = _lateralDch

    private val _exteriorIzq = MutableLiveData<Jugador>()
    val exteriorIzq: LiveData<Jugador> = _exteriorIzq

    private val _defLateralIzq = MutableLiveData<Jugador>()
    val defLateralIzq: LiveData<Jugador> = _defLateralIzq

    private val _posteIzq = MutableLiveData<Jugador>()
    val posteIzq: LiveData<Jugador> = _posteIzq

    private val _posteDch = MutableLiveData<Jugador>()
    val posteDch: LiveData<Jugador> = _posteDch

    private val _defLateralDch = MutableLiveData<Jugador>()
    val defLateralDch: LiveData<Jugador> = _defLateralDch

    private val _exteriorDch = MutableLiveData<Jugador>()
    val exteriorDch: LiveData<Jugador> = _exteriorDch

    // Goles
    private val _golesFavor = MutableLiveData<Int>(0)
    val golesFavor: LiveData<Int> = _golesFavor

    private val _golesContra = MutableLiveData<Int>(0)
    val golesContra: LiveData<Int> = _golesContra

    // Lista de estadísticas
    private val _estadisticas = MutableLiveData<MutableList<Estadistica>>(mutableListOf())
    val estadisticas: LiveData<MutableList<Estadistica>> = _estadisticas

    private var temporizadorJob: Job? = null
    private val _minutos = MutableLiveData(0)
    val minutos: LiveData<Int> = _minutos

    var pausado:Boolean = true

    fun iniciarTemporizador() {
        if (temporizadorJob?.isActive == true) return

        temporizadorJob = viewModelScope.launch {
            while (isActive) {
                delay(1_000)
                if (!pausado) {
                    _minutos.postValue((_minutos.value ?: 0) + 1)
                }
            }
        }
    }

    // Métodos para actualizar jugadores
    fun setJugador(posicion: String, jugador: Jugador?) {
        when (posicion) {
            "extremoIzq" -> _extremoIzq.value = jugador
            "pivote" -> _pivote.value = jugador
            "extremoDch" -> _extremoDch.value = jugador
            "lateralIzq" -> _lateralIzq.value = jugador
            "central" -> _central.value = jugador
            "lateralDch" -> _lateralDch.value = jugador
            "exteriorIzq" -> _exteriorIzq.value = jugador
            "defLateralIzq" -> _defLateralIzq.value = jugador
            "posteIzq" -> _posteIzq.value = jugador
            "posteDch" -> _posteDch.value = jugador
            "defLateralDch" -> _defLateralDch.value = jugador
            "exteriorDch" -> _exteriorDch.value = jugador
        }
    }

    // Métodos para actualizar goles
    fun agregarGolAFavor() {
        _golesFavor.value = (_golesFavor.value ?: 0) + 1
    }

    fun restarGolAFavor() {
        _golesFavor.value = (_golesFavor.value ?: 0) - 1
    }

    fun agregarGolEnContra() {
        _golesContra.value = (_golesContra.value ?: 0) + 1
    }

    fun restarGolEnContra() {
        _golesContra.value = (_golesContra.value ?: 0) - 1
    }

    // Añadir una estadística
    fun agregarEstadistica(estadistica: Estadistica) {
        val lista = _estadisticas.value ?: mutableListOf()
        lista.add(0, estadistica)
        _estadisticas.value = lista
    }

    fun eliminarEstadistica(pos:Int) {
        val lista = _estadisticas.value ?: mutableListOf()
        lista.removeAt(pos)
        _estadisticas.value = lista
    }
}