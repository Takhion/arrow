package arrow.recursion.typeclasses

import arrow.Kind
import arrow.KindType
import arrow.core.Eval
import arrow.typeclasses.Functor
import arrow.recursion.Algebra
import arrow.recursion.Coalgebra
import arrow.recursion.hylo

/**
 * Typeclass for types that can be generically unfolded with coalgebras.
 */
interface Corecursive<T: KindType> {
  /**
   * Implementation for embed.
   */
  fun <F: KindType> Functor<F>.embedT(tf: Kind<F, Eval<Kind<T, F>>>): Eval<Kind<T, F>>

  /**
   * Creates a algebra given a functor.
   */
  fun <F: KindType> Functor<F>.embed(): Algebra<F, Eval<Kind<T, F>>> = { embedT(it) }

  /**
   * Unfold into any recursive type.
   */
  fun <F: KindType, A> Functor<F>.ana(a: A, coalg: Coalgebra<F, A>): Kind<T, F> =
    hylo(embed(), coalg, a)
}
