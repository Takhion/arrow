package arrow.free.instances

import arrow.Kind
import arrow.KindType
import arrow.free.Cofree
import arrow.free.CofreeOf
import arrow.free.CofreePartialOf
import arrow.free.fix
import arrow.instance
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor

@instance(Cofree::class)
interface CofreeFunctorInstance<S: KindType> : Functor<CofreePartialOf<S>> {
  override fun <A, B> Kind<CofreePartialOf<S>, A>.map(f: (A) -> B): Cofree<S, B> = fix().map(f)
}

@instance(Cofree::class)
interface CofreeComonadInstance<S: KindType> : CofreeFunctorInstance<S>, Comonad<CofreePartialOf<S>> {
  override fun <A, B> Kind<CofreePartialOf<S>, A>.coflatMap(f: (Kind<CofreePartialOf<S>, A>) -> B): Cofree<S, B> = fix().coflatMap(f)

  override fun <A> CofreeOf<S, A>.extract(): A = fix().extract()

  override fun <A> Kind<CofreePartialOf<S>, A>.duplicate(): Kind<CofreePartialOf<S>, Cofree<S, A>> = fix().duplicate()
}

class CofreeContext<S: KindType> : CofreeComonadInstance<S>

class CofreeContextPartiallyApplied<S: KindType> {
  infix fun <A> extensions(f: CofreeContext<S>.() -> A): A =
    f(CofreeContext())
}

fun <S: KindType> ForCofree(): CofreeContextPartiallyApplied<S> =
  CofreeContextPartiallyApplied()