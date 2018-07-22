package arrow.mtl.typeclasses

import arrow.Kind
import arrow.KindType
import arrow.core.Option
import arrow.typeclasses.Monad
import kotlin.coroutines.experimental.startCoroutine

interface MonadFilter<F: KindType> : Monad<F>, FunctorFilter<F> {

  fun <A> empty(): Kind<F, A>

  override fun <A, B> Kind<F, A>.mapFilter(f: (A) -> Option<B>): Kind<F, B> =
    this.flatMap({ a -> f(a).fold({ empty<B>() }, { just(it) }) })
}

/**
 * Entry point for monad bindings which enables for comprehension. The underlying impl is based on coroutines.
 * A coroutine is initiated and inside [MonadContinuation] suspended yielding to [flatMap]. Once all the flatMap binds are completed
 * the underlying monad is returned from the act of executing the coroutine
 */
fun <F: KindType, B> MonadFilter<F>.bindingFilter(c: suspend MonadFilterContinuation<F, *>.() -> B): Kind<F, B> {
  val continuation = MonadFilterContinuation<F, B>(this)
  val wrapReturn: suspend MonadFilterContinuation<F, *>.() -> Kind<F, B> = { just(c()) }
  wrapReturn.startCoroutine(continuation, continuation)
  return continuation.returnedMonad()
}
