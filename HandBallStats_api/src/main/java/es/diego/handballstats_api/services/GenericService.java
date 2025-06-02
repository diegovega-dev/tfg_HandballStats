package es.diego.handballstats_api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import es.diego.handballstats_api.models.Entrenador;
import es.diego.handballstats_api.models.Equipo;
import es.diego.handballstats_api.models.Partido;
import es.diego.handballstats_api.repository.EntrenadorRepository;
import es.diego.handballstats_api.repository.EquipoRepository;
import es.diego.handballstats_api.repository.PartidoRepository;

@Service
public class GenericService {

    private final EntrenadorRepository entrenadorRepo;

    private final EquipoRepository equipoRepo;

    private final PartidoRepository partidoRepo;

    public GenericService(EntrenadorRepository entrenadorRepo, EquipoRepository equipoRepo, PartidoRepository partidoRepo) {
        this.entrenadorRepo = entrenadorRepo;
        this.equipoRepo = equipoRepo;
        this.partidoRepo = partidoRepo;
    }

    //metodos para los entrenadores

    public Entrenador buscarEntrenador (String email) {
        return entrenadorRepo.findByEmail(email);
    }

    public void borrarEntrenador (int id) {
        entrenadorRepo.deleteById(id);
    }

    public Entrenador crearEntrenador (Entrenador entrenador) {
        return entrenadorRepo.save(entrenador);
    }

    public Entrenador buscarEntrenadorId(int id) {return entrenadorRepo.findById(id).get();}

    public Boolean existeEntrenadorEmail (String email) {return entrenadorRepo.existsByEmail(email);}

    //metodos para los equipos

    public List<Equipo> buscarEquipos (int id) {
        return equipoRepo.findByEntrenador(id);
    }

    public void borrarEquipo (int id) {
        equipoRepo.deleteById(id);
    }

    public Equipo crearEquipo (Equipo equipo) {
        return equipoRepo.save(equipo);
    }

    public Equipo buscarEquipoId(int id) {return equipoRepo.findById(id).get();}

    //metodos para los partidos

    public List<Partido> buscarPartidos (Equipo equipo) {
        return partidoRepo.findByEquipo(equipo);
    }

    public void borrarPartido (int id) {
        partidoRepo.deleteById(id);
    }

    public Partido crearPartido (Partido partido) {
        return partidoRepo.save(partido);
    }

    public Partido buscarPartidoId(int id) {return partidoRepo.findById(id).get();}
}
