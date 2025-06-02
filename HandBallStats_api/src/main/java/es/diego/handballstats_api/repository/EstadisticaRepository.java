package es.diego.handballstats_api.repository;

import es.diego.handballstats_api.models.estadisticas.Estadistica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EstadisticaRepository extends JpaRepository<Estadistica, Integer> {

    @Query("SELECT a FROM Estadistica a WHERE a.jugador.id = :id")
    List<Estadistica> findByJugador(int id);

    @Query("SELECT a FROM Estadistica a WHERE a.jugador.equipo.id = :id")
    List<Estadistica> findByEquipo (int id);
}
