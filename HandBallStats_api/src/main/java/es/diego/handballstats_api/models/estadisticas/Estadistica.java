package es.diego.handballstats_api.models.estadisticas;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import es.diego.handballstats_api.models.Jugador;
import es.diego.handballstats_api.models.Partido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipo_estadistica",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EstadisticaAtaque.class, name = "ataque"),
        @JsonSubTypes.Type(value = EstadisticaDefensa.class, name = "defensa"),
        @JsonSubTypes.Type(value = EstadisticaPortero.class, name = "portero")
})
public abstract class Estadistica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "minuto")
    private int minuto;

    @ManyToOne
    @JoinColumn(name = "id_jugador")
    @JsonBackReference("e")
    private Jugador jugador;

    @ManyToOne
    @JoinColumn(name = "id_partido")
    @JsonBackReference("d")
    private Partido partido;

    @JsonProperty("tipo_estadistica")
    public abstract String getTipoEstadistica();
}
