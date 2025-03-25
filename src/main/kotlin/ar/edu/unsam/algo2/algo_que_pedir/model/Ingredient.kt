package ar.edu.unsam.algo2.algo_que_pedir.model

enum class FoodGroup(val displayName: String) {
    CEREALES_Y_TUBERCULOS("Cereales y Tubérculos"),
    AZUCARES_Y_DULCES("Azúcares y Dulces"),
    LACTEOS("Lácteos"),
    FRUTAS_Y_VERDURAS("Frutas y Verduras"),
    GRASAS_Y_ACEITES("Grasas y Aceites"),
    PROTEINAS("Proteínas");

    override fun toString(): String = displayName
}
data class Ingredient(
    val name: String,
    val marketCost: Double,
    val foodGroup: FoodGroup,
    val isAnimalBased: Boolean
)

