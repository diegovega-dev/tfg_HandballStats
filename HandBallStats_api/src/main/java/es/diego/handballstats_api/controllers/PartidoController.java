package es.diego.handballstats_api.controllers;

import es.diego.handballstats_api.security.JWT;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.diego.handballstats_api.models.Equipo;
import es.diego.handballstats_api.models.Partido;
import es.diego.handballstats_api.services.GenericService;

@RestController
@RequestMapping("/HandballStats/partidos")
public class PartidoController {

    private final GenericService genericSer;

    public PartidoController(GenericService genericSer) {
        this.genericSer = genericSer;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearPartido (@RequestHeader("Authorization") String authHeader, @RequestBody Partido entity) {
        String token = authHeader.replace("Bearer ", "");

        try {
            if (comprobar(token, entity)) {
                return ResponseEntity.ok(genericSer.crearPartido(entity));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error con la autorizacion");
            }

        } catch (Exception e) {return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en la base de datos");}

    }
    
    @DeleteMapping("/borrar")
    public ResponseEntity<?> borrarPartido (@RequestHeader("Authorization") String authHeader, @RequestParam int id_partido) {
        String token = authHeader.replace("Bearer ", "");

        try {
            if (comprobar(token, genericSer.buscarPartidoId(id_partido))) {
                genericSer.borrarPartido(id_partido);
                return ResponseEntity.ok("Partido borrado con exito");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error con la autorizacion");
            }

        } catch (Exception e) {return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en la base de datos");}
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPartido (@RequestHeader("Authorization") String authHeader, @RequestParam int id_equipo) {
        String token = authHeader.replace("Bearer ", "");
        Equipo entity = genericSer.buscarEquipoId(id_equipo);

        try {
            if (JWT.validarToken(token) && entity.getEntrenador().getEmail().equals(JWT.obtenerEmailDesdeToken(token))) {
                return ResponseEntity.ok(genericSer.buscarPartidos(entity));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error con la autorizacion");
            }

        } catch (Exception e) {return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en la base de datos");}

    }

    //metodo para que solo los usuarios puedan alterar los datos de sus equipos y no de todos
    private boolean comprobar(String token, Partido a) {
        String emailEntrenador = a.getEquipo().getEntrenador().getEmail();

        return JWT.validarToken(token) && JWT.obtenerEmailDesdeToken(token).equals(emailEntrenador);
    }
}
