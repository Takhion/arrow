package arrow.instances

import arrow.Kind
import arrow.KindType
import arrow.core.Eval
import arrow.core.fix
import arrow.data.Coproduct
import arrow.data.CoproductOf
import arrow.data.CoproductPartialOf
import arrow.data.fix
import arrow.instance
import arrow.typeclasses.*

@instance(Coproduct::class)
interface CoproductFunctorInstance<F: KindType, G: KindType> : Functor<CoproductPartialOf<F, G>> {

  fun FF(): Functor<F>

  fun FG(): Functor<G>

  override fun <A, B> Kind<CoproductPartialOf<F, G>, A>.map(f: (A) -> B): Coproduct<F, G, B> = fix().map(FF(), FG(), f)
}

@instance(Coproduct::class)
interface CoproductComonadInstance<F: KindType, G: KindType> : Comonad<CoproductPartialOf<F, G>> {

  fun CF(): Comonad<F>

  fun CG(): Comonad<G>

  override fun <A, B> Kind<CoproductPartialOf<F, G>, A>.coflatMap(f: (Kind<CoproductPartialOf<F, G>, A>) -> B): Coproduct<F, G, B> = fix().coflatMap(CF(), CG(), f)

  override fun <A> Kind<CoproductPartialOf<F, G>, A>.extract(): A = fix().extract(CF(), CG())

  override fun <A, B> Kind<CoproductPartialOf<F, G>, A>.map(f: (A) -> B): Coproduct<F, G, B> = fix().map(CF(), CG(), f)

}

@instance(Coproduct::class)
interface CoproductFoldableInstance<F: KindType, G: KindType> : Foldable<CoproductPartialOf<F, G>> {

  fun FF(): Foldable<F>

  fun FG(): Foldable<G>

  override fun <A, B> Kind<CoproductPartialOf<F, G>, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f, FF(), FG())

  override fun <A, B> Kind<CoproductPartialOf<F, G>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f, FF(), FG())

}

@instance(Coproduct::class)
interface CoproductTraverseInstance<F: KindType, G: KindType> : Traverse<CoproductPartialOf<F, G>> {

  fun TF(): Traverse<F>

  fun TG(): Traverse<G>

  override fun <H: KindType, A, B> CoproductOf<F, G, A>.traverse(AP: Applicative<H>, f: (A) -> Kind<H, B>): Kind<H, Coproduct<F, G, B>> =
    fix().traverse(AP, TF(), TG(), f)

  override fun <A, B> Kind<CoproductPartialOf<F, G>, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f, TF(), TG())

  override fun <A, B> Kind<CoproductPartialOf<F, G>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f, TF(), TG())
}

class CoproductContext<F: KindType, G: KindType>(val TF: Traverse<F>, val TG: Traverse<G>) : CoproductTraverseInstance<F, G> {
  override fun TF(): Traverse<F> = TF
  override fun TG(): Traverse<G> = TG
}

class CoproductContextPartiallyApplied<F: KindType, G: KindType>(val TF: Traverse<F>, val TG: Traverse<G>) {
  infix fun <A> extensions(f: CoproductContext<F, G>.() -> A): A =
    f(CoproductContext(TF, TG))
}

fun <F: KindType, G: KindType> ForCoproduct(TF: Traverse<F>, TG: Traverse<G>): CoproductContextPartiallyApplied<F, G> =
  CoproductContextPartiallyApplied(TF, TG)