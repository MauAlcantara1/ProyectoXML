package unam.mx.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import unam.mx.model.ModeloPersonaje
import unam.mx.model.RespuestaPersonajes

interface ServiceApi {
    @GET("characters")
    suspend fun getPersonajes(@Query("limit") limit: Int = 100): Response<RespuestaPersonajes>

    @GET("characters/{id}")
    suspend fun getPersonajeById(
        @Path("id") id: Int): Response<ModeloPersonaje>
}
