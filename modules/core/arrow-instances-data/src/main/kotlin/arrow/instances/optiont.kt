package arrow.instances

import arrow.Kind
import arrow.KindType
import arrow.core.*
import arrow.data.OptionT
import arrow.data.OptionTOf
import arrow.data.OptionTPartialOf
import arrow.data.fix
import arrow.instance
import arrow.typeclasses.*

@instance(OptionT::class)
interface OptionTFunctorInstance<F: KindType> : Functor<OptionTPartialOf<F>> {

  fun FF(): Functor<F>

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.map(f: (A) -> B): OptionT<F, B> = fix().map(FF(), f)

}

@instance(OptionT::class)
interface OptionTApplicativeInstance<F: KindType> : OptionTFunctorInstance<F>, Applicative<OptionTPartialOf<F>> {

  override fun FF(): Monad<F>

  override fun <A> just(a: A): OptionT<F, A> = OptionT(FF().just(Option(a)))

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.map(f: (A) -> B): OptionT<F, B> = fix().map(FF(), f)

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.ap(ff: Kind<OptionTPartialOf<F>, (A) -> B>): OptionT<F, B> =
    fix().ap(FF(), ff)
}

@instance(OptionT::class)
interface OptionTMonadInstance<F: KindType> : OptionTApplicativeInstance<F>, Monad<OptionTPartialOf<F>> {

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.map(f: (A) -> B): OptionT<F, B> = fix().map(FF(), f)

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.flatMap(f: (A) -> Kind<OptionTPartialOf<F>, B>): OptionT<F, B> = fix().flatMap(FF(), { f(it).fix() })

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.ap(ff: Kind<OptionTPartialOf<F>, (A) -> B>): OptionT<F, B> =
    fix().ap(FF(), ff)

  override fun <A, B> tailRecM(a: A, f: (A) -> OptionTOf<F, Either<A, B>>): OptionT<F, B> =
    OptionT.tailRecM(FF(), a, f)

}

fun <F: KindType, A, B> OptionTOf<F, A>.foldLeft(FF: Foldable<F>, b: B, f: (B, A) -> B): B = FF.compose(Option.foldable()).foldLC(fix().value, b, f)

fun <F: KindType, A, B> OptionTOf<F, A>.foldRight(FF: Foldable<F>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> = FF.compose(Option.foldable()).run {
  fix().value.foldRC(lb, f)
}

fun <F: KindType, G: KindType, A, B> OptionTOf<F, A>.traverse(FF: Traverse<F>, GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, OptionT<F, B>> {
  val fa = ComposedTraverse(FF, Option.traverse(), Option.applicative()).traverseC(fix().value, f, GA)
  return GA.run { fa.map({ OptionT(FF.run { it.unnest().map({ it.fix() }) }) }) }
}

fun <F: KindType, G: KindType, A> OptionTOf<F, Kind<G, A>>.sequence(FF: Traverse<F>, GA: Applicative<G>): Kind<G, OptionT<F, A>> =
  traverse(FF, GA, ::identity)

@instance(OptionT::class)
interface OptionTFoldableInstance<F: KindType> : Foldable<OptionTPartialOf<F>> {

  fun FFF(): Foldable<F>

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(FFF(), b, f)

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(FFF(), lb, f)

}

@instance(OptionT::class)
interface OptionTTraverseInstance<F: KindType> : OptionTFoldableInstance<F>, Traverse<OptionTPartialOf<F>> {

  override fun FFF(): Traverse<F>

  override fun <G: KindType, A, B> Kind<OptionTPartialOf<F>, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, OptionT<F, B>> =
    fix().traverse(FFF(), AP, f)

}

@instance(OptionT::class)
interface OptionTSemigroupKInstance<F: KindType> : SemigroupK<OptionTPartialOf<F>> {

  fun FF(): Monad<F>

  override fun <A> Kind<OptionTPartialOf<F>, A>.combineK(y: Kind<OptionTPartialOf<F>, A>): OptionT<F, A> = fix().orElse(FF(), { y.fix() })
}

@instance(OptionT::class)
interface OptionTMonoidKInstance<F: KindType> : MonoidK<OptionTPartialOf<F>>, OptionTSemigroupKInstance<F> {
  override fun <A> empty(): OptionT<F, A> = OptionT(FF().just(None))
}

class OptionTContext<F: KindType>(val MF: Monad<F>) : OptionTMonadInstance<F>, OptionTMonoidKInstance<F> {

  override fun FF(): Monad<F> = MF

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.map(f: (A) -> B): OptionT<F, B> =
    fix().map(f)
}

class OptionTContextPartiallyApplied<F: KindType>(val MF: Monad<F>) {
  infix fun <A> extensions(f: OptionTContext<F>.() -> A): A =
    f(OptionTContext(MF))
}

fun <F: KindType> ForOptionT(MF: Monad<F>): OptionTContextPartiallyApplied<F> =
  OptionTContextPartiallyApplied(MF)