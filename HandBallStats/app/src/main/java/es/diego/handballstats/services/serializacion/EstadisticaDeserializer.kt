package es.diego.handballstats.services.serializacion

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import es.diego.handballstats.models.Estadistica
import es.diego.handballstats.models.EstadisticaAtaque
import es.diego.handballstats.models.EstadisticaDefensa
import es.diego.handballstats.models.EstadisticaPortero
import java.lang.reflect.Type

class EstadisticaDeserializer : JsonDeserializer<Estadistica> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Estadistica {
        val jsonObj = json.asJsonObject
        val tipo = jsonObj.get("tipo_estadistica")?.asString

        //elimino este campo para evitar conflictos
        jsonObj.remove("tipo_estadistica")

        return when (tipo) {
            "ataque" -> context.deserialize<EstadisticaAtaque>(jsonObj, EstadisticaAtaque::class.java)
            "defensa" -> context.deserialize<EstadisticaDefensa>(jsonObj, EstadisticaDefensa::class.java)
            "portero" -> context.deserialize<EstadisticaPortero>(jsonObj, EstadisticaPortero::class.java)
            else -> throw JsonParseException("Tipo de estadística desconocido: $tipo")
        }
    }
}

