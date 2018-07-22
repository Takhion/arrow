package arrow.mtl.instances

import arrow.Kind
import arrow.KindType
import arrow.data.Kleisli
import arrow.data.KleisliPartialOf
import arrow.data.fix
import arrow.instance
import arrow.instances.KleisliMonadErrorInstance
import arrow.instances.KleisliMonadInstance
import arrow.mtl.typeclasses.MonadReader
import arrow.typeclasses.MonadError

@instance(Kleisli::class)
interface KleisliMonadReaderInstance<F: KindType, D> : KleisliMonadInstance<F, D>, MonadReader<KleisliPartialOf<F, D>, D> {

  override fun ask(): Kleisli<F, D, D> = Kleisli({ FF().just(it) })

  override fun <A> Kind<KleisliPartialOf<F, D>, A>.local(f: (D) -> D): Kleisli<F, D, A> = fix().local(f)

}

class KleisliMtlContext<F: KindType, D, E>(val MF: MonadError<F, E>) : KleisliMonadReaderInstance<F, D>, KleisliMonadErrorInstance<F, D, E> {
  override fun FF(): MonadError<F, E> = MF
}

class KleisliMtlContextPartiallyApplied<F: KindType, D, E>(val MF: MonadError<F, E>) {
  infix fun <A> extensions(f: KleisliMtlContext<F, D, E>.() -> A): A =
    f(KleisliMtlContext(MF))
}

fun <F: KindType, D, E> ForKleisli(MF: MonadError<F, E>): KleisliMtlContextPartiallyApplied<F, D, E> =
  KleisliMtlContextPartiallyApplied(MF)