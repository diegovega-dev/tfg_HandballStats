package es.diego.handballstats_api.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name ="entrenador", uniqueConstraints = {@UniqueConstraint(columnNames = {"email", "foto"})})
public class Entrenador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "foto")
    private String foto;

    @OneToMany(mappedBy = "entrenador", cascade = CascadeType.ALL)
    @JsonManagedReference("a")
    private List<Equipo> equipos;
}
