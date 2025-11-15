
package com.angelcabrera.proyecto.models




data class User(
    val uid: String = "",
    val name: String = "",
    val languages: List<String> = emptyList(),
    val level: String = "",
    val rating: Double = 0.0,
    val available: Boolean = false
)

data class Testimonial(
    val id: String,
    val author: String,
    val text: String,
    val stars: Int
)

data class ProgressInfo(
    val sessionsCompleted: Int = 0,
    val mentorsReceived: Int = 0,
    val minutes: Int = 0
)