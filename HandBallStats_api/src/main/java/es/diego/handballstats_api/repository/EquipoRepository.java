package es.diego.handballstats_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import es.diego.handballstats_api.models.Equipo;
import java.util.List;


@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Integer> {
    
    @Query("SELECT a FROM Equipo a WHERE a.entrenador.id = :id")
    List<Equipo> findByEntrenador(int id);
}
