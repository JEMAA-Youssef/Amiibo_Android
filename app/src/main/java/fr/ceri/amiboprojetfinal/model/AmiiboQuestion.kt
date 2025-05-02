package fr.ceri.amiboprojetfinal.model

data class AmiiboQuestion(
    val question: String,
    val correctAnswer: String,
    val options: List<String>,
    val imageUrl: String
)