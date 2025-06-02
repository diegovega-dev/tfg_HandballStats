package es.diego.handballstats_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import es.diego.handballstats_api.models.Jugador;
import java.util.List;


@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Integer>{
    
    @Query("SELECT a FROM Jugador a WHERE a.equipo.id = :id")
    List<Jugador> findByEquipo(int id);
}
