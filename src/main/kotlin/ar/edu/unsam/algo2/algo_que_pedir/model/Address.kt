package ar.edu.unsam.algo2.algo_que_pedir.model

import org.uqbar.geodds.Point

data class Address(
    val street: String,
    val number: Int,
    val location: Point
)