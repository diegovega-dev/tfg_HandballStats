package es.diego.handballstats_api.models.estadisticas;

import es.diego.handballstats_api.models.enums.Distancia;
import es.diego.handballstats_api.models.enums.LadoPorteria;
import es.diego.handballstats_api.models.enums.PosicionAtaque;
import es.diego.handballstats_api.models.enums.TipoEstadisticaPortero;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name ="estadistica_portero")
public class EstadisticaPortero extends Estadistica {

    @Column(name = "tipo")
    private TipoEstadisticaPortero tipo;

    @Column(name = "posicion")
    private PosicionAtaque posicion;

    @Column(name = "distancia")
    private Distancia distancia;

    @Column(name = "lado")
    private LadoPorteria lado;

    @Override
    public String getTipoEstadistica() {
        return "portero";
    }
}
