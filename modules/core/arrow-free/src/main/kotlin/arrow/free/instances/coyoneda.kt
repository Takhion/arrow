package arrow.free.instances

import arrow.*
import arrow.free.*
import arrow.typeclasses.Functor

@instance(Coyoneda::class)
interface CoyonedaFunctorInstance<F: KindType, G> : Functor<CoyonedaPartialOf<F, G>> {
  override fun <A, B> Kind<CoyonedaPartialOf<F, G>, A>.map(f: (A) -> B): Coyoneda<F, G, B> = fix().map(f)
}

class CoyonedaContext<F: KindType, G> : CoyonedaFunctorInstance<F, G>

class CoyonedaContextPartiallyApplied<F: KindType, G> {
  infix fun <A> extensions(f: CoyonedaContext<F, G>.() -> A): A =
    f(CoyonedaContext())
}

fun <F: KindType, G> ForCoyoneda(): CoyonedaContextPartiallyApplied<F, G> =
  CoyonedaContextPartiallyApplied()