package es.diego.handballstats.services.serializacion

import com.google.gson.Gson
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

class DualGsonConverterFactory private constructor(
    private val requestGson: Gson,
    private val responseGson: Gson
) : Converter.Factory() {

    companion object {
        @JvmStatic
        fun create(requestGson: Gson, responseGson: Gson) =
            DualGsonConverterFactory(requestGson, responseGson)
    }

    // PARA RESPUESTAS: deserializar con responseGson
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return GsonConverterFactory
            .create(responseGson)
            .responseBodyConverter(type, annotations, retrofit)
    }

    // PARA PETICIONES: serializar con requestGson
    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        return GsonConverterFactory
            .create(requestGson)
            .requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit)
    }
}