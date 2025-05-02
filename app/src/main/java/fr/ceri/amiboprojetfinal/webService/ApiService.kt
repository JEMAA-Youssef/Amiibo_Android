package fr.ceri.amiboprojetfinal.webService

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("api/amiibo/")
    suspend fun getAmiibosBySeries(@Query("gameseries") series: String): AmiiboResponse

    @GET("api/gameseries/")
    suspend fun getGameSeries(): GameSeriesResponse
}