package arrow.recursion

import arrow.Kind
import arrow.KindType
import arrow.core.Eval
import arrow.typeclasses.Functor

fun <F: KindType, A> Algebra(it: Algebra<F, A>) = it

fun <F: KindType, A> Coalgebra(it: Coalgebra<F, A>) = it

/**
 * Fold over a kind.
 */
typealias Algebra<F, A> = (Kind<F, A>) -> A

/**
 * Unfold over a kind.
 */
typealias Coalgebra<F, A> = (A) -> Kind<F, A>

/**
 * The composition of cata and ana.
 */
fun <F: KindType, A, B> Functor<F>.hylo(
  alg: Algebra<F, Eval<B>>,
  coalg: Coalgebra<F, A>,
  a: A
): B {
  fun h(a: A): Eval<B> = alg(coalg(a).map { Eval.defer { h(it) } })
  return h(a).value()
}
