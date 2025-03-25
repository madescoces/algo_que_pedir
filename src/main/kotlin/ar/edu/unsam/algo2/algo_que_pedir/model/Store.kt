package ar.edu.unsam.algo2.algo_que_pedir.model

import ar.edu.unsam.algo2.algo_que_pedir.config.EnviromentVariables
import ar.edu.unsam.algo2.algo_que_pedir.exceptions.BusinessException
import ar.edu.unsam.algo2.algo_que_pedir.model.enums.OrderStatus
import ar.edu.unsam.algo2.algo_que_pedir.model.enums.PaymentTypes

class Store(
    val name: String,
    val address: Address,
    val paymentTypes: MutableSet<PaymentTypes>
) {
    var appComission: Int = EnviromentVariables.DEFAULT_APP_COMISION
    private val scores: MutableList<Int> = mutableListOf()

    init {
        require(appComission in 0..100) { throw BusinessException("La comisión de la tienda debe estar entre 0 y 100") }
        appComission /= 100
    }

    val score: Double
        get() = if (scores.isNotEmpty()) scores.average() else 0.0

    fun calificateOrder(order: Order) {
        if (order.status == OrderStatus.DELIVERED) {
            scores.add(order.score)
        }
    }

    fun addPaymentType(paymentType: PaymentTypes) {
        paymentTypes.add(paymentType)
    }

    fun removePaymentType(paymentType: PaymentTypes) {
        paymentTypes.remove(paymentType)
    }
}