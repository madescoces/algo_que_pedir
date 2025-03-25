package ar.edu.unsam.algo2.algo_que_pedir.model

import ar.edu.unsam.algo2.algo_que_pedir.exceptions.BusinessException
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows
import org.uqbar.geodds.Point
import java.time.LocalDate

class UserTest : DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest
    describe("User") {
        val birthDate = LocalDate.of(2000, 1, 1)
        val user = User("Pablo", "Perez", "pablo", Address("Calle", 1, Point(0.0, 0.0)), "1234", 10, birthDate)
        val tomato = Ingredient("Tomate", 1.0, FoodGroup.FRUTAS_Y_VERDURAS, false)

        it("should calculate age correctly") {
            val expectedAge = LocalDate.now().year - 2000
            user.age shouldBe expectedAge
        }

        describe("ingredient management") {
            it("should prevent adding preferred ingredient to avoided") {
                user.addPreferredIngredient(tomato)
                assertThrows<BusinessException> {
                    user.addAvoidedIngredient(tomato)
                }
            }

            it("should prevent adding avoided ingredient to preferred") {
                user.addAvoidedIngredient(tomato)
                assertThrows<BusinessException> {
                    user.addPreferredIngredient(tomato)
                }
            }
        }
    }
})