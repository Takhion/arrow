package arrow.optics.instances

import arrow.Kind
import arrow.KindType
import arrow.core.Try
import arrow.instance
import arrow.instances.traverse
import arrow.optics.Traversal
import arrow.optics.typeclasses.Each
import arrow.typeclasses.Applicative

/**
 * [Traversal] for [Try] that has focus in each [Try.Success].
 *
 * @receiver [Try.Companion] to make it statically available.
 * @return [Traversal] with source [Try] and focus in every [Try.Success] of the source.
 */
fun <A> Try.Companion.traversal(): Traversal<Try<A>, A> = object : Traversal<Try<A>, A>() {
  override fun <F: KindType> modifyF(FA: Applicative<F>, s: Try<A>, f: (A) -> Kind<F, A>): Kind<F, Try<A>> =
    s.traverse(FA, f)
}

/**
 * [Each] instance definition for [Try].
 */
@instance(Try::class)
interface TryEachInstance<A> : Each<Try<A>, A> {
  override fun each(): Traversal<Try<A>, A> =
    Try.traversal()
}
