package ar.edu.unsam.algo2.algo_que_pedir.model

import ar.edu.unsam.algo2.algo_que_pedir.config.EnviromentVariables
import ar.edu.unsam.algo2.algo_que_pedir.exceptions.BusinessException
import org.uqbar.geodds.Point
import java.time.LocalDate
import java.time.Period

class User(
    val nombre: String,
    val apellido: String,
    val username: String,
    var address: Address,
    private val password: String,
    private var maxDistance: Int,
    private val fechaNacimiento: LocalDate
) {
    var dishPreferenceStrategy: UserDishPreferenceStrategy = NormalStrategy()
    val preferredIngredients = mutableSetOf<Ingredient>()
    val avoidedIngredients = mutableSetOf<Ingredient>()
    val registeredDate: LocalDate = LocalDate.now()
    private val storesForCalificate = mutableMapOf<Store, LocalDate>()

    val age: Int
        get() = Period.between(fechaNacimiento, LocalDate.now()).years

    fun changeDishPreferenceStrategy(strategy: UserDishPreferenceStrategy) {
        dishPreferenceStrategy = strategy
    }

    fun addPreferredIngredient(ingredient: Ingredient) {
        if (ingredient in avoidedIngredients)
            throw BusinessException("Ingrediente ya está en evitados")
        preferredIngredients.add(ingredient)
    }

    fun addAvoidedIngredient(ingredient: Ingredient) {
        if (ingredient in preferredIngredients)
            throw BusinessException("Ingrediente ya está en preferidos")
        avoidedIngredients.add(ingredient)
    }

    fun removePreferredIngredient(ingredient: Ingredient) {
        preferredIngredients.remove(ingredient)
    }

    fun removeAvoidedIngredient(ingredient: Ingredient) {
        avoidedIngredients.remove(ingredient)
    }

    fun confirmOrder(order: Order) {
        if (canCalificate(order.store)) {
            storesForCalificate[order.store] = LocalDate.now()
        }
    }

    fun isCloseToMe(point: Point) = maxDistance >= distanceToMe(point)
    private fun canCalificate(store: Store) =
        storesForCalificate[store]?.isBefore(LocalDate.now().minusDays(EnviromentVariables.MAX_DAYS_TO_CALIFICATE))
            ?: false

    private fun distanceToMe(point: Point) = point.distance(this.address.location)
}

interface UserDishPreferenceStrategy {
    fun fitsCriteria(dish: Dish): Boolean
}

class NormalStrategy : UserDishPreferenceStrategy {
    override fun fitsCriteria(dish: Dish): Boolean = true
}

class VeganStrategy : UserDishPreferenceStrategy {
    override fun fitsCriteria(dish: Dish): Boolean = dish.ingredients.any { !it.isAnimalBased }
}

class RefinedStrategy : UserDishPreferenceStrategy {
    override fun fitsCriteria(dish: Dish): Boolean = dish.chef?.let { true } ?: false
}

class CoservatorStrategy(private val user: User) : UserDishPreferenceStrategy {
    override fun fitsCriteria(dish: Dish): Boolean {
        return dish.ingredients.all { it in user.preferredIngredients }
    }
}

class LoyalStrategy(private val preferedStores: Set<Store>) : UserDishPreferenceStrategy {
    override fun fitsCriteria(dish: Dish): Boolean {
        return dish.store in preferedStores
    }
}

class MarketingStrategy(private val frases: List<String>) : UserDishPreferenceStrategy {
    override fun fitsCriteria(dish: Dish): Boolean {
        return frases.any { dish.description.contains(it) }
    }
}

class ImpatientStrategy(private val user: User) : UserDishPreferenceStrategy {
    override fun fitsCriteria(dish: Dish): Boolean {
        return user.isCloseToMe(dish.store.address.location)
    }
}