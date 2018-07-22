package arrow.effects.typeclasses

import arrow.Kind
import arrow.KindType
import arrow.core.Either

interface Effect<F: KindType> : Async<F> {
  fun <A> Kind<F, A>.runAsync(cb: (Either<Throwable, A>) -> Kind<F, Unit>): Kind<F, Unit>
}
