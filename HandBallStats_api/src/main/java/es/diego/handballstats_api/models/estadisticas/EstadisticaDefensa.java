package es.diego.handballstats_api.models.estadisticas;

import es.diego.handballstats_api.models.enums.PosicionDefensa;
import es.diego.handballstats_api.models.enums.TipoEstadisticaDefensa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name ="estadistica_defensa")
public class EstadisticaDefensa extends Estadistica {

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    private TipoEstadisticaDefensa tipo;

    @Column(name = "posicion")
    @Enumerated(EnumType.STRING)
    private PosicionDefensa posicion;

    @Override
    public String getTipoEstadistica() {
        return "defensa";
    }

}
