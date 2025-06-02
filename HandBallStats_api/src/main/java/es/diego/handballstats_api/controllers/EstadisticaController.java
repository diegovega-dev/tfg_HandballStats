package es.diego.handballstats_api.controllers;

import java.util.List;

import es.diego.handballstats_api.models.estadisticas.Estadistica;
import es.diego.handballstats_api.security.JWT;
import es.diego.handballstats_api.services.GenericService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.diego.handballstats_api.services.JugadorService;

@RestController
@RequestMapping("/HandballStats/estadisticas")
public class EstadisticaController {

    private final JugadorService jugadorSer;
    private final GenericService genericSer;

    public EstadisticaController(JugadorService jugadorSer, GenericService genericSer) {
        this.jugadorSer = jugadorSer;
        this.genericSer = genericSer;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearEstadistica (@RequestHeader("Authorization") String authHeader, @RequestBody List<Estadistica> entity) {
        String token = authHeader.replace("Bearer ", "");

        try {
            if (comprobar(token, entity.get(0))) {
                return ResponseEntity.ok(jugadorSer.crearEstadistica(entity));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error con la autorizacion");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en la base de datos");
        }
    }

    @GetMapping("/buscar-jugador")
    public ResponseEntity<?> buscarEstadistica (@RequestHeader("Authorization") String authHeader, @RequestParam int id_jugador) {
        String token = authHeader.replace("Bearer ", "");

        try {
            if (JWT.validarToken(token) && jugadorSer.buscarJugadorId(id_jugador).getEquipo().getEntrenador().getEmail().equals(JWT.obtenerEmailDesdeToken(token))) {
                return ResponseEntity.ok(jugadorSer.buscarEstadisticas(id_jugador));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error con la autorizacion");
            }

        } catch (Exception e) {return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en la base de datos");}


    }

    @GetMapping("/buscar-equipo")
    public ResponseEntity<?> buscarEstadisticaEquipo (@RequestHeader("Authorization") String authHeader, @RequestParam int id_equipo) {
        String token = authHeader.replace("Bearer ", "");

        try {
            if (JWT.validarToken(token) && genericSer.buscarEquipoId(id_equipo).getEntrenador().getEmail().equals(JWT.obtenerEmailDesdeToken(token))) {
                return ResponseEntity.ok(jugadorSer.buscarEstadisticasEquipo(id_equipo));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error con la autorizacion");
            }

        } catch (Exception e) {return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en la base de datos");}
    }


    //metodo para que solo los usuarios puedan alterar los datos de sus equipos y no de todos
    private boolean comprobar(String token, Estadistica a) {
        String emailEntrenador = a.getJugador().getEquipo().getEntrenador().getEmail();

        return JWT.validarToken(token) && JWT.obtenerEmailDesdeToken(token).equals(emailEntrenador);
    }
}
