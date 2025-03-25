package ar.edu.unsam.algo2.algo_que_pedir.model

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.uqbar.geodds.Point
import java.time.LocalDate

class DishSpec : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    describe("Dist Tests") {
        val store = Store("Tienda", Address("Calle", 1, Point(0.0, 0.0)), mutableSetOf())
        it("costo de producción es suma de ingredientes") {
            val ingredients = listOf(
                Ingredient("Arroz", 2.5, FoodGroup.CEREALES_Y_TUBERCULOS, false),
                Ingredient("Pollo", 5.0, FoodGroup.PROTEINAS, true)
            )
            val dish = Dish("Arroz con Pollo", "Plato tradicional", ingredients, store)
            dish.productionCost shouldBe 7.5
        }

        it("Un plato es nuevo") {
            val dish = Dish("Arroz con Pollo", "Plato tradicional", listOf(), store)
            dish.isNew shouldBe true
        }

        it("Nuevo dish no es nuevo") {
            val dish = Dish("Arroz con Pollo", "Plato tradicional", listOf(), store)
            dish.releaseDate = LocalDate.now().minusDays(31)
            dish.isNew shouldBe false
        }
    }
})