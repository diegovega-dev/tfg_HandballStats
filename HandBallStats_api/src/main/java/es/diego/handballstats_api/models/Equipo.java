package es.diego.handballstats_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name ="equipo", uniqueConstraints = {@UniqueConstraint(columnNames = {"foto"})})
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "foto")
    private String foto;

    @Column(name = "categoria")
    private String categoria;

    @ManyToOne
    @JoinColumn(name = "id_entrenador", nullable = false)
    @JsonBackReference("a")
    private Entrenador entrenador;

    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL)
    @JsonManagedReference("b")
    private List<Jugador> jugadores;

    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("c")
    private List<Partido> partidos;
}
