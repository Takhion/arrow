package arrow.optics.instances

import arrow.Kind
import arrow.KindType
import arrow.core.Either
import arrow.core.fix
import arrow.core.traverse
import arrow.instance
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each
import arrow.typeclasses.Applicative

/**
 * [Traversal] for [Either] that has focus in each [Either.Right].
 *
 * @receiver [Either.Companion] to make it statically available.
 * @return [Traversal] with source [Either] and focus every [Either.Right] of the source.
 */
fun <L, R> Either.Companion.traversal(): Traversal<Either<L, R>, R> = object : Traversal<Either<L, R>, R>() {
  override fun <F: KindType> modifyF(FA: Applicative<F>, s: Either<L, R>, f: (R) -> Kind<F, R>): Kind<F, Either<L, R>> = with(Either.traverse<L>()) {
    FA.run { s.traverse(FA, f).map { it.fix() } }
  }
}

/**
 * [Each] instance for [Either] that has focus in each [Either.Right].
 */
@instance(Either::class)
interface EitherEachInstance<L, R> : Each<Either<L, R>, R> {
  override fun each(): Traversal<Either<L, R>, R> =
    Either.traversal()
}
