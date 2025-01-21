package com.mokelab.lesson.pokemon.network
import kotlinx.serialization.Serializable

@Serializable
data class Pokemon(
    val id: Int,
    val name: String
)

data class PokemonData (
    val pokemons: List<Pokemon>,
    val strings: Map<String, String>
)