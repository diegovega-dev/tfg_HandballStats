package es.diego.handballstats_api.models;

import es.diego.handballstats_api.models.estadisticas.Estadistica;
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
@Table(name ="jugador", uniqueConstraints = {@UniqueConstraint(columnNames = {"foto"})})
public class Jugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="portero", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean portero = false;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "dorsal", nullable = false)
    private int dorsal;

    @Column(name = "foto")
    private String foto;

    @Column(name = "anio")
    private int anio;

    @ManyToOne
    @JoinColumn(name = "id_equipo", nullable = false)
    @JsonBackReference("b")
    private Equipo equipo;

    @OneToMany(mappedBy = "jugador", cascade = CascadeType.ALL)
    @JsonManagedReference("e")
    private List<Estadistica> estadisticas;
}
