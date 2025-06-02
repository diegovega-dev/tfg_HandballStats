package es.diego.handballstats_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.diego.handballstats_api.models.Equipo;
import es.diego.handballstats_api.models.Partido;
import java.util.List;


@Repository
public interface PartidoRepository extends JpaRepository<Partido, Integer>{
    
    List<Partido> findByEquipo(Equipo equipo);
}
