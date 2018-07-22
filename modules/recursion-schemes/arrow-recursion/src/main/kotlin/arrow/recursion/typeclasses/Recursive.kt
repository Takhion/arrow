package arrow.recursion.typeclasses

import arrow.Kind
import arrow.KindType
import arrow.core.Eval
import arrow.typeclasses.Functor
import arrow.recursion.Algebra
import arrow.recursion.Coalgebra
import arrow.recursion.hylo

/**
 * Typeclass for types that can be generically folded with algebras.
 */
interface Recursive<T: KindType> {
  /**
   * Implementation for project.
   */
  fun <F: KindType> Functor<F>.projectT(tf: Kind<T, F>): Kind<F, Kind<T, F>>

  /**
   * Creates a coalgebra given a functor.
   */
  fun <F: KindType> Functor<F>.project(): Coalgebra<F, Kind<T, F>> = { projectT(it) }

  /**
   * Fold generalized over any recursive type.
   */
  fun <F: KindType, A> Functor<F>.cata(tf: Kind<T, F>, alg: Algebra<F, Eval<A>>): A =
    hylo(alg, project(), tf)
}
