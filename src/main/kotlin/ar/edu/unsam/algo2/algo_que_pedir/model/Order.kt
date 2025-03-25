package ar.edu.unsam.algo2.algo_que_pedir.model

import ar.edu.unsam.algo2.algo_que_pedir.config.EnviromentVariables
import ar.edu.unsam.algo2.algo_que_pedir.exceptions.BusinessException
import ar.edu.unsam.algo2.algo_que_pedir.model.enums.OrderStatus
import ar.edu.unsam.algo2.algo_que_pedir.model.enums.PaymentTypes
import org.uqbar.geodds.Point
import java.time.LocalDate
import java.time.LocalTime


class Order(
    val client: User,
    val store: Store,
    val deliberyTime: LocalTime
) {
    var status: OrderStatus = OrderStatus.PENDING
    private var paimentType: PaymentTypes = store.paymentTypes.first()
    private val dishes: MutableList<Dish> = mutableListOf()
    private var delivery: Delivery? = null

    val pickUpLocation = store.address.location
    var deliveryLocation = client.address.location
    var score: Int = 1
        set(value) {
            require(value in 1..5) { throw BusinessException("La puntuación debe estar entre 1 y 5") }
            field = value
        }
    val isCertified: Boolean
        get() {
            return oldClient() and isConfiableStore()
        }

    fun addDish(dish: Dish) {
        validateClientIngredientPreferences(dish)
        validateClientDishPreferences(dish)
        dishes.add(dish)
    }

    fun removeDish(dish: Dish) {
        dishes.remove(dish)
    }

    fun changePaymentType(paymentType: PaymentTypes) {
        if (!store.paymentTypes.contains(paymentType)) {
            throw BusinessException("El tipo de pago no es válido para esta tienda")
        }
        paimentType = paymentType
    }
    fun assingDelivery(delivery: Delivery) {
        delivery.validateOrder(this)
        this.delivery = delivery
    }

    fun removeDelivery() {
        this.delivery = null
    }

    fun sendOrder () {

    }

    fun price(): Double = (totalDishesPrice() + deliveryCost()) * paymentComission()

    private fun isConfiableStore() = store.score > EnviromentVariables.MIN_CONFIABLE_STORE_SCORE

    private fun deliveryCost(): Double = baseDishesPrice() * delivery.let { it?.comision ?: 1.0 }

    private fun oldClient(): Boolean =
        client.registeredDate.isBefore(LocalDate.now().minusYears(EnviromentVariables.MIN_CLIENT_YEARS_OLD))

    private fun paymentComission(): Double = if (paimentType == PaymentTypes.CASH) 0.0 else 0.05
    private fun baseDishesPrice(): Double = dishes.sumOf { it.basePrice() }
    private fun totalDishesPrice(): Double = dishes.sumOf { it.price() }
    private fun validateClientIngredientPreferences(dish: Dish) {
        if (dish.ingredients.any { it in client.avoidedIngredients })
            throw BusinessException("Uno de los ingredientes del plato no está permitido para el cliente")
    }
    private fun validateClientDishPreferences(dish: Dish) {
        if (!client.dishPreferenceStrategy.fitsCriteria(dish)) {
            throw BusinessException("El plato no cumple las expectativas del cliente")
        }
    }
}
