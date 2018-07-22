package arrow.core

import arrow.Kind
import arrow.KindType

interface FunctionK<F: KindType, G: KindType> {

  /**
   * Applies this functor transformation from `F` to `G`
   */
  operator fun <A> invoke(fa: Kind<F, A>): Kind<G, A>

  companion object {
    fun <F: KindType> id(): FunctionK<F, F> = object : FunctionK<F, F> {
      override fun <A> invoke(fa: Kind<F, A>): Kind<F, A> = fa
    }
  }
}
