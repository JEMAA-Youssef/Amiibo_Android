package fr.ceri.amiboprojetfinal.model

data class GameSeriesResponse(
    val gameSeries: List<GameSeries>
)

data class GameSeries(
    val key: String,
    val name: String
)