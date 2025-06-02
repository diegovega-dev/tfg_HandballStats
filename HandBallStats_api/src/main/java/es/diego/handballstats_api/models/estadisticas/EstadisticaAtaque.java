package es.diego.handballstats_api.models.estadisticas;

import es.diego.handballstats_api.models.enums.Distancia;
import es.diego.handballstats_api.models.enums.PosicionAtaque;
import es.diego.handballstats_api.models.enums.TipoEstadisticaAtaque;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name ="estadistica_ataque")
public class EstadisticaAtaque extends Estadistica {

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    private TipoEstadisticaAtaque tipo;

    @Column(name = "posicion")
    @Enumerated(EnumType.STRING)
    private PosicionAtaque posicion;

    @Column(name="contrataque", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean contrataque = false;

    @Column(name="distancia")
    @Enumerated(EnumType.STRING)
    private Distancia distancia;

    @Override
    public String getTipoEstadistica() {
        return "ataque";
    }

}
