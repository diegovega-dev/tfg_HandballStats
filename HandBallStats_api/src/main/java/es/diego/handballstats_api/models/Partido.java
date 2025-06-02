package es.diego.handballstats_api.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import es.diego.handballstats_api.models.estadisticas.Estadistica;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name ="partido")
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "goles_favor")
    private int goles_favor;

    @Column(name = "goles_contra")
    private int goles_contra;

    @Column(name = "fecha", nullable = false)
    private Date fecha;

    @ManyToOne
    @JoinColumn(name = "id_equipo")
    @JsonBackReference("c")
    private Equipo equipo;

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL)
    @JsonManagedReference("d")
    private List<Estadistica> estadisticas;
}
