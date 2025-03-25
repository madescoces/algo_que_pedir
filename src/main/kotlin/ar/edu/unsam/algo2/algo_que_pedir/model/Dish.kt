package ar.edu.unsam.algo2.algo_que_pedir.model

import ar.edu.unsam.algo2.algo_que_pedir.exceptions.BusinessException
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class Dish(
    val name: String,
    val description: String,
    val ingredients: List<Ingredient>,
    val store: Store
) {
    var chef: Chef? = null
    var releaseDate: LocalDate = LocalDate.now()
    private val basePrice: Double = 0.0
    private val discounts: MutableSet<Discount> = mutableSetOf(NewDishDiscount())

    fun addDiscount(discount: Discount) {
        discounts.add(discount)
    }

    fun removeDiscount(discount: Discount) {
        discounts.remove(discount)
    }

    val productionCost: Double
        get() = ingredients.sumOf { it.marketCost }
    val isNew: Boolean
        get() = releaseDate.isAfter(LocalDate.now().minusDays(30))

    fun basePrice() = productionCost + basePrice + appComission(store) + regalyPrice()

    fun price() = basePrice() - discounts()

    private fun discounts() = basePrice() * discounts.sumOf { it.calculate(this) }
    private fun appComission(store: Store) = basePrice * store.appComission
    private fun regalyPrice() = chef?.let { basePrice * it.comission } ?: 0.0
}

abstract class Discount {
    abstract fun calculate(dish: Dish): Double
}

class NewDishDiscount : Discount() {
    override fun calculate(dish: Dish): Double {
        val daysOld = ChronoUnit.DAYS.between(LocalDate.now(), dish.releaseDate)
        return when {
            daysOld <= 20 -> 0.3 - (0.1 * daysOld)
            daysOld in 21..30 -> 0.1
            else -> 0.0
        }
    }
}

class PromotionDiscount(private val percentage: Int) : Discount() {
    init {
        require(percentage in 0..100) { throw BusinessException("El porcentaje de descuento debe estar entre 0 y 100") }
    }

    override fun calculate(dish: Dish): Double {
        return if (dish.isNew) 0.0 else percentage / 100.0
    }

}