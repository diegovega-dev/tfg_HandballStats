package es.diego.handballstats_api.controllers;

import es.diego.handballstats_api.security.JWT;
import es.diego.handballstats_api.services.GenericService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.diego.handballstats_api.models.Jugador;
import es.diego.handballstats_api.services.JugadorService;

@RestController
@RequestMapping("/HandballStats/jugadores")
public class JugadorController {

    private final JugadorService jugadorSer;
    private final GenericService genericSer;

    public JugadorController(JugadorService jugadorSer, GenericService genericSer) {
        this.jugadorSer = jugadorSer;
        this.genericSer = genericSer;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearJugador (@RequestHeader("Authorization") String authHeader, @RequestBody Jugador entity) {
        String token = authHeader.replace("Bearer ", "");

        try {

            if (comprobar(token, entity)) {
                return ResponseEntity.ok(jugadorSer.crearJugador(entity));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error con la autorizacion");
            }

        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en la base de datos");}
    }
    
    @DeleteMapping("/borrar")
    public ResponseEntity<?> borrarJugador (@RequestHeader("Authorization") String authHeader, @RequestParam int id_jugador) {
        String token = authHeader.replace("Bearer ", "");

        try {

            if (comprobar(token, jugadorSer.buscarJugadorId(id_jugador))) {
                jugadorSer.borrarJugador(id_jugador);
                return ResponseEntity.ok("Jugador eliminado con exito");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error con la autorizacion");
            }

        }catch (Exception e) {return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en la base de datos");}
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarJugadores (@RequestHeader("Authorization") String authHeader, @RequestParam int id_equipo) {
        String token = authHeader.replace("Bearer ", "");

        try {

            if (JWT.validarToken(token) && JWT.obtenerEmailDesdeToken(token).equals(genericSer.buscarEquipoId(id_equipo).getEntrenador().getEmail())) {
                return ResponseEntity.ok(jugadorSer.buscarJugadores(id_equipo));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error con la autorizacion");
            }

        }catch (Exception e) {return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en la base de datos");}
    }

    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizarJugador (@RequestHeader("Authorization") String authHeader, @RequestBody Jugador entity) {
        String token = authHeader.replace("Bearer ", "");

        try {
            Jugador a = jugadorSer.buscarJugadorId(entity.getId());
            if (comprobar(token, a)) {

                a.setNombre(entity.getNombre());
                a.setAnio(entity.getAnio());
                a.setDorsal(entity.getDorsal());
                a.setFoto(entity.getFoto());

                return ResponseEntity.ok(jugadorSer.crearJugador(a));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error con la autorizacion");
            }

        }catch (Exception e) {return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());}
    }

    //metodo para que solo los usuarios puedan alterar los datos de sus equipos y no de todos
    private boolean comprobar(String token, Jugador a) {
        String emailEntrenador = a.getEquipo().getEntrenador().getEmail();

        return JWT.validarToken(token) && JWT.obtenerEmailDesdeToken(token).equals(emailEntrenador);
    }
    
}
