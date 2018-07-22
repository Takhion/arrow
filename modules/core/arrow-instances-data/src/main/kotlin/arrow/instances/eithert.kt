package arrow.instances

import arrow.Kind
import arrow.KindType
import arrow.core.*
import arrow.data.EitherT
import arrow.data.EitherTOf
import arrow.data.EitherTPartialOf
import arrow.data.fix
import arrow.typeclasses.*

interface EitherTFunctorInstance<F: KindType, L> : Functor<EitherTPartialOf<F, L>> {

  fun FF(): Functor<F>

  override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.map(f: (A) -> B): EitherT<F, L, B> = fix().map(FF(), { f(it) })
}

interface EitherTApplicativeInstance<F: KindType, L> : EitherTFunctorInstance<F, L>, Applicative<EitherTPartialOf<F, L>> {

  fun MF(): Monad<F>

  override fun <A> just(a: A): EitherT<F, L, A> = EitherT.just(MF(), a)

  override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.map(f: (A) -> B): EitherT<F, L, B> = fix().map(MF(), { f(it) })

  override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.ap(ff: Kind<EitherTPartialOf<F, L>, (A) -> B>): EitherT<F, L, B> =
    fix().ap(MF(), ff)
}

interface EitherTMonadInstance<F: KindType, L> : EitherTApplicativeInstance<F, L>, Monad<EitherTPartialOf<F, L>> {

  override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.map(f: (A) -> B): EitherT<F, L, B> = fix().map(MF(), { f(it) })

  override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.ap(ff: Kind<EitherTPartialOf<F, L>, (A) -> B>): EitherT<F, L, B> =
    fix().ap(MF(), ff)

  override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.flatMap(f: (A) -> Kind<EitherTPartialOf<F, L>, B>): EitherT<F, L, B> = fix().flatMap(MF(), { f(it).fix() })

  override fun <A, B> tailRecM(a: A, f: (A) -> EitherTOf<F, L, Either<A, B>>): EitherT<F, L, B> =
    EitherT.tailRecM(MF(), a, f)
}

interface EitherTApplicativeErrorInstance<F: KindType, L> : EitherTApplicativeInstance<F, L>, ApplicativeError<EitherTPartialOf<F, L>, L> {

  override fun <A> Kind<EitherTPartialOf<F, L>, A>.handleErrorWith(f: (L) -> Kind<EitherTPartialOf<F, L>, A>): EitherT<F, L, A> = MF().run {
    EitherT(fix().value.flatMap({
      when (it) {
        is Either.Left -> f(it.a).fix().value
        is Either.Right -> just(it)
      }
    }))
  }

  override fun <A> raiseError(e: L): EitherT<F, L, A> = EitherT(MF().just(Left(e)))
}

interface EitherTMonadErrorInstance<F: KindType, L> : EitherTApplicativeErrorInstance<F, L>, EitherTMonadInstance<F, L>, MonadError<EitherTPartialOf<F, L>, L>

interface EitherTFoldableInstance<F: KindType, L> : Foldable<EitherTPartialOf<F, L>> {

  fun FFF(): Foldable<F>

  override fun <B, C> Kind<EitherTPartialOf<F, L>, B>.foldLeft(b: C, f: (C, B) -> C): C = fix().foldLeft(FFF(), b, f)

  override fun <B, C> Kind<EitherTPartialOf<F, L>, B>.foldRight(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fix().foldRight(FFF(), lb, f)
}

interface EitherTTraverseInstance<F: KindType, L> : EitherTFunctorInstance<F, L>, EitherTFoldableInstance<F, L>, Traverse<EitherTPartialOf<F, L>> {

  fun TF(): Traverse<F>

  override fun <A, B> Kind<EitherTPartialOf<F, L>, A>.map(f: (A) -> B): EitherT<F, L, B> = fix().map(TF(), { f(it) })

  override fun <G: KindType, B, C> Kind<EitherTPartialOf<F, L>, B>.traverse(AP: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, EitherT<F, L, C>> =
    fix().traverse(TF(), AP, f)
}

interface EitherTSemigroupKInstance<F: KindType, L> : SemigroupK<EitherTPartialOf<F, L>> {
  fun MF(): Monad<F>

  override fun <A> Kind<EitherTPartialOf<F, L>, A>.combineK(y: Kind<EitherTPartialOf<F, L>, A>): EitherT<F, L, A> =
    fix().combineK(MF(), y)
}

fun <F: KindType, A, B, C> EitherTOf<F, A, B>.foldLeft(FF: Foldable<F>, b: C, f: (C, B) -> C): C =
  FF.compose(Either.foldable<A>()).foldLC(fix().value, b, f)

fun <F: KindType, A, B, C> EitherTOf<F, A, B>.foldRight(FF: Foldable<F>, lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> = FF.compose(Either.foldable<A>()).run {
  fix().value.foldRC(lb, f)
}

fun <F: KindType, A, B, G: KindType, C> EitherTOf<F, A, B>.traverse(FF: Traverse<F>, GA: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, EitherT<F, A, C>> {
  val fa: Kind<G, Kind<Nested<F, EitherPartialOf<A>>, C>> = ComposedTraverse(FF, Either.traverse(), Either.monad<A>()).traverseC(fix().value, f, GA)
  return GA.run { fa.map({ EitherT(FF.run { it.unnest().map({ it.fix() }) }) }) }
}

fun <F: KindType, G: KindType, A, B> EitherTOf<F, A, Kind<G, B>>.sequence(FF: Traverse<F>, GA: Applicative<G>): Kind<G, EitherT<F, A, B>> =
  traverse(FF, GA, ::identity)

fun <F: KindType, L> EitherT.Companion.functor(FF: Functor<F>): Functor<EitherTPartialOf<F, L>> =
  object : EitherTFunctorInstance<F, L> {
    override fun FF(): Functor<F> = FF

  }

fun <F: KindType, L> EitherT.Companion.applicative(MF: Monad<F>): Applicative<EitherTPartialOf<F, L>> =
  object : EitherTApplicativeInstance<F, L> {
    override fun FF(): Functor<F> = MF

    override fun MF(): Monad<F> = MF
  }

fun <F: KindType, L> EitherT.Companion.monad(MF: Monad<F>): Monad<EitherTPartialOf<F, L>> =
  object : EitherTMonadInstance<F, L> {
    override fun FF(): Functor<F> = MF

    override fun MF(): Monad<F> = MF
  }

fun <F: KindType, L> EitherT.Companion.applicativeError(MF: Monad<F>): ApplicativeError<EitherTPartialOf<F, L>, L> =
  object : EitherTApplicativeErrorInstance<F, L> {
    override fun FF(): Functor<F> = MF

    override fun MF(): Monad<F> = MF
  }

fun <F: KindType, L> EitherT.Companion.monadError(MF: Monad<F>): MonadError<EitherTPartialOf<F, L>, L> =
  object : EitherTMonadErrorInstance<F, L> {
    override fun FF(): Functor<F> = MF

    override fun MF(): Monad<F> = MF
  }

fun <F: KindType, A> EitherT.Companion.traverse(FF: Traverse<F>): Traverse<EitherTPartialOf<F, A>> =
  object : EitherTTraverseInstance<F, A> {
    override fun FF(): Functor<F> = FF

    override fun FFF(): Foldable<F> = FF

    override fun TF(): Traverse<F> = FF
  }

fun <F: KindType, A> EitherT.Companion.foldable(FF: Traverse<F>): Foldable<EitherTPartialOf<F, A>> =
  object : EitherTFoldableInstance<F, A> {
    override fun FFF(): Foldable<F> = FF
  }

fun <F: KindType, L> EitherT.Companion.semigroupK(MF: Monad<F>): SemigroupK<EitherTPartialOf<F, L>> =
  object : EitherTSemigroupKInstance<F, L> {
    override fun MF(): Monad<F> = MF
  }

class EitherTContext<F: KindType, E>(val MF: Monad<F>) : EitherTMonadErrorInstance<F, E>, EitherTSemigroupKInstance<F, E> {
  override fun FF(): Functor<F> = MF
  override fun MF(): Monad<F> = MF
}

class EitherTContextPartiallyApplied<F: KindType, E>(val MF: Monad<F>) {
  infix fun <A> extensions(f: EitherTContext<F, E>.() -> A): A =
    f(EitherTContext(MF))
}

fun <F: KindType, E> ForEitherT(MF: Monad<F>): EitherTContextPartiallyApplied<F, E> =
  EitherTContextPartiallyApplied(MF)
