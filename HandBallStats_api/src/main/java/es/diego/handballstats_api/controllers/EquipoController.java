package es.diego.handballstats_api.controllers;

import es.diego.handballstats_api.security.JWT;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.diego.handballstats_api.models.Equipo;
import es.diego.handballstats_api.services.GenericService;

@RestController
@RequestMapping("/HandballStats/equipos")
public class EquipoController {

    private final GenericService genericSer;

    public EquipoController(GenericService genericSer) {
        this.genericSer = genericSer;
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearEquipo (@RequestHeader("Authorization") String authHeader, @RequestBody Equipo entity) {
        String token = authHeader.replace("Bearer ", "");

        try {
            if (comprobar(token, entity)) {
                return ResponseEntity.ok(genericSer.crearEquipo(entity));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error con la autorizacion");
            }

        } catch (Exception e) {return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en la base de datos");}
    }
    
    @DeleteMapping("/borrar")
    public ResponseEntity<?> borrarEquipo (@RequestHeader("Authorization") String authHeader, @RequestParam int id_equipo) {
        String token = authHeader.replace("Bearer ", "");

        try {
            if (comprobar(token, genericSer.buscarEquipoId(id_equipo))) {
                genericSer.borrarEquipo(id_equipo);
                return ResponseEntity.ok("Equipo borrado con exito");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error con la autorizacion");
            }

        } catch (Exception e) {return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en la base de datos");}
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarEquipo (@RequestHeader("Authorization") String authHeader, @RequestParam int id_entrenador) {
        String token = authHeader.replace("Bearer ", "");

        if (JWT.validarToken(token) && JWT.obtenerEmailDesdeToken(token).equals(genericSer.buscarEntrenadorId(id_entrenador).getEmail())) {
            return ResponseEntity.ok(genericSer.buscarEquipos(id_entrenador));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error con la autorizacion");
        }
    }

    @GetMapping("/id")
    public ResponseEntity<?> GetById (@RequestHeader("Authorization") String authHeader, @RequestParam int id_equipo) {
        String token = authHeader.replace("Bearer ", "");

        Equipo a = genericSer.buscarEquipoId(id_equipo);

        if (JWT.validarToken(token) && JWT.obtenerEmailDesdeToken(token).equals(a.getEntrenador().getEmail())) {
            return ResponseEntity.ok(a);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error con la autorizacion");
        }
    }

    @PutMapping("/actualizar")
    public ResponseEntity<?> actualizarEquipo (@RequestHeader("Authorization") String authHeader, @RequestBody Equipo entity) {
        String token = authHeader.replace("Bearer ", "");

        try {
            Equipo a = genericSer.buscarEquipoId(entity.getId());

            if (comprobar(token, a)) {


                a.setCategoria(entity.getCategoria());
                a.setFoto(entity.getFoto());
                a.setNombre(entity.getNombre());

                return ResponseEntity.ok(genericSer.crearEquipo(a));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error con la autorizacion");
            }

        } catch (Exception e) {return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en la base de datos");}
    }


    //metodo para que solo los usuarios puedan alterar los datos de sus equipos y no de todos
    private boolean comprobar(String token, Equipo a) {
        String emailEntrenador = a.getEntrenador().getEmail();

        return JWT.validarToken(token) && JWT.obtenerEmailDesdeToken(token).equals(emailEntrenador);
    }
    
}
