package es.diego.handballstats_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.diego.handballstats_api.models.Entrenador;


@Repository
public interface EntrenadorRepository extends JpaRepository<Entrenador, Integer> {

    Entrenador findByEmail(String email);

    Boolean existsByEmail(String email);
}
