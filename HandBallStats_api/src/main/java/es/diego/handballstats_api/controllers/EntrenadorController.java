package es.diego.handballstats_api.controllers;

import es.diego.handballstats_api.models.Entrenador;
import es.diego.handballstats_api.security.JWT;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.diego.handballstats_api.services.GenericService;



@RestController
@RequestMapping("/HandballStats/entrenadores")
public class EntrenadorController {

    private final GenericService genericSer;

    public EntrenadorController(GenericService genericSer) {
        this.genericSer = genericSer;
    }

    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizarEntrenador(@RequestBody Entrenador entrenador) {
        if (entrenador == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear usuario, no hay datos especificados");
        }
        try {
            genericSer.crearEntrenador(entrenador);
            return ResponseEntity.ok(entrenador);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al intentar actualizar el usuario");
        }
    }

    @DeleteMapping("/borrar")
    public ResponseEntity<?> borrarEntrenador (@RequestHeader("Authorization") String authHeader, @RequestParam int id_entrenador) {
        try {
            String token = authHeader.replace("Bearer ", "");

            if (JWT.obtenerEmailDesdeToken(token).equals(genericSer.buscarEntrenadorId(id_entrenador).getEmail())) {
                genericSer.borrarEntrenador(id_entrenador);
                return ResponseEntity.ok("Entrenador eliminado con exito");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes autorizacion");
            }

        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en al base de datos");
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarEntrenador (@RequestHeader("Authorization") String authHeader, @RequestParam String email_entrenador) {

        String token = authHeader.replace("Bearer ", "");

        if (JWT.obtenerEmailDesdeToken(token).equals(email_entrenador)) {
            return ResponseEntity.ok(genericSer.buscarEntrenador(email_entrenador));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes autorizacion");
        }
    }

    @GetMapping("/existe")
    public ResponseEntity<?> ExisteEmail (@RequestParam String email_entrenador) {
        return ResponseEntity.ok(genericSer.existeEntrenadorEmail(email_entrenador));
    }
}
