package ar.edu.unsam.algo2.algo_que_pedir.model

import ar.edu.unsam.algo2.algo_que_pedir.config.EnviromentVariables
import ar.edu.unsam.algo2.algo_que_pedir.exceptions.BusinessException
import ar.edu.unsam.algo2.algo_que_pedir.model.enums.OrderStatus
import org.uqbar.geodds.Polygon
import java.time.LocalTime

class Delivery(
    val name: String,
    val userName: String,
    private val password: String,
    private val deliberyZone: Polygon
) {
    var comision = 0.1
        set(value) {
            value in 0.0..1.0 || throw BusinessException("La comisión debe estar entre 0 y 1")
            field = value
        }

    private val additionalDeliveryConditions: MutableSet<DeliveryCondition> = mutableSetOf()

    fun addDeliveryCondition(condition: DeliveryCondition) {
        additionalDeliveryConditions.add(condition)
    }

    fun removeDeliveryCondition(condition: DeliveryCondition) {
        additionalDeliveryConditions.remove(condition)
    }

    fun validateOrder(order: Order) {
        IsCloseEnough(deliberyZone).isEligibleForDelivery(order)
        IsOrderReadyToDelivery().isEligibleForDelivery(order)
        additionalDeliveryConditions.forEach { it.isEligibleForDelivery(order) }
    }

    fun calculateDeliveryPrice(price: Double) = price * comision
}

interface DeliveryCondition {
    fun isEligibleForDelivery(order: Order)
}

class BussinessHours(
    startTime: LocalTime,
    endTime: LocalTime
) {
    private val workingHours = startTime..endTime

    init {
        if (startTime > endTime) {
            throw BusinessException("La hora de inicio no puede ser mayor que la hora de fin")
        }
    }

    fun isInBussinessHours(time: LocalTime): Boolean = time in workingHours
}

class IsOrderReadyToDelivery() : DeliveryCondition {
    override fun isEligibleForDelivery(order: Order) {
        if (order.status != OrderStatus.PREPARED) {
            throw BusinessException("El pedido no está listo")
        }
    }
}


class IsCloseEnough(private val zone: Polygon) : DeliveryCondition {
    override fun isEligibleForDelivery(order: Order) {
        if (!zone.isInside(order.pickUpLocation)) {
            throw BusinessException("La tienda esta fuera del área de cobertura")
        }
        if (!zone.isInside(order.deliveryLocation)) {
            throw BusinessException("La zona de entrega esta fuera del área de cobertura")
        }
    }
}

class SafeDeliberyHours(private val workingHours: BussinessHours) : DeliveryCondition {
    override fun isEligibleForDelivery(order: Order) {
        if (!workingHours.isInBussinessHours(order.deliberyTime)) {
            throw BusinessException("La hora de entrega no está dentro de las horas de entrega posibles")
        }
    }
}

class MinimunDeliberyPrice(private var minPrice: Double = EnviromentVariables.MIN_PRICE) : DeliveryCondition {
    override fun isEligibleForDelivery(order: Order) {
        if (order.price() < minPrice) {
            throw BusinessException("El precio de entrega no es suficiente, el precio mínimo es de $minPrice")
        }
    }
}

class IsValidStore(private val stores: List<Store>) : DeliveryCondition {
    override fun isEligibleForDelivery(order: Order) {
        if (!stores.contains(order.store)) {
            throw BusinessException("El pedido no pertenece a ninguna tienda permitida")
        }
    }
}

class IsOrderCertified() : DeliveryCondition {
    override fun isEligibleForDelivery(order: Order) {
        if (!order.isCertified) {
            throw BusinessException("El pedido no ha sido certificado")
        }
    }
}