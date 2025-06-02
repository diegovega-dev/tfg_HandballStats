package es.diego.handballstats_api.services;

import java.util.List;

import es.diego.handballstats_api.models.estadisticas.Estadistica;
import org.springframework.stereotype.Service;

import es.diego.handballstats_api.models.Jugador;
import es.diego.handballstats_api.repository.EstadisticaRepository;
import es.diego.handballstats_api.repository.JugadorRepository;

@Service
public class JugadorService {

    private final JugadorRepository jugadorRepo;

    private final EstadisticaRepository estadisticaRepo;

    public JugadorService(JugadorRepository jugadorRepo, EstadisticaRepository estadisticaRepo) {
        this.jugadorRepo = jugadorRepo;
        this.estadisticaRepo = estadisticaRepo;
    }

    //metodos para los jugadores

    public Jugador crearJugador (Jugador jugador) {
        return jugadorRepo.save(jugador);
    }

    public List<Jugador> buscarJugadores (int id_equipo) {
        return jugadorRepo.findByEquipo(id_equipo);
    }

    public void borrarJugador (int id) {
        jugadorRepo.deleteById(id);
    }

    public Jugador buscarJugadorId (int id) {return jugadorRepo.findById(id).get();}

    //metodos para las estadisticas

    public List<Estadistica> crearEstadistica (List<Estadistica> estadisticas) {
        return estadisticaRepo.saveAll(estadisticas);
    }

    public List<Estadistica> buscarEstadisticas (int id_jugador) {
        return estadisticaRepo.findByJugador(id_jugador);
    }

    public List<Estadistica> buscarEstadisticasEquipo (int id_equipo) {
        return estadisticaRepo.findByEquipo(id_equipo);
    }

    public Estadistica buscarStatId (int id) {return estadisticaRepo.findById(id).get();}
}