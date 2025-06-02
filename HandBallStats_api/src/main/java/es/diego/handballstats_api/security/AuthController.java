package es.diego.handballstats_api.security;

import org.springframework.web.bind.annotation.*;

import es.diego.handballstats_api.models.Entrenador;
import es.diego.handballstats_api.services.GenericService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;


@RestController
@RequestMapping("/HandballStats/auth")
public class AuthController {

    private final GenericService genericSer;

    public AuthController(GenericService genericSer) {
        this.genericSer = genericSer;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String pass) {
        
        Entrenador e = genericSer.buscarEntrenador(email);


        
        if (e == null || !encriptar(pass).equals(e.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Datos invalidos, revisa la contraseña y el correo");
        }

        //devuelve el token generado con el email si todo esta correcto

        return ResponseEntity.ok(JWT.generarToken(email));
    }

    @PostMapping("/register")
    public ResponseEntity<?> crearEntrenador(@RequestBody Entrenador entity) {

        if (entity == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear usuario, no hay datos especificados");
        }
        try {
            entity.setPassword(encriptar(entity.getPassword()));
            genericSer.crearEntrenador(entity);
            return ResponseEntity.ok(JWT.generarToken(entity.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al intentar crear el usuario");
        }
    }
    
    private String encriptar (String a){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(a.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error al encriptar la contraseña", ex);
        }
    }

}
