package arrow.instances

import arrow.Kind
import arrow.KindType
import arrow.core.Either
import arrow.core.Eval
import arrow.data.*
import arrow.instance
import arrow.typeclasses.*
import arrow.data.combineK as nelCombineK

@instance(NonEmptyList::class)
interface NonEmptyListSemigroupInstance<A> : Semigroup<NonEmptyList<A>> {
  override fun NonEmptyList<A>.combine(b: NonEmptyList<A>): NonEmptyList<A> = this + b
}

@instance(NonEmptyList::class)
interface NonEmptyListEqInstance<A> : Eq<NonEmptyList<A>> {

  fun EQ(): Eq<A>

  override fun NonEmptyList<A>.eqv(b: NonEmptyList<A>): Boolean =
    all.zip(b.all) { aa, bb -> EQ().run { aa.eqv(bb) } }.fold(true) { acc, bool ->
      acc && bool
    }
}

@instance(NonEmptyList::class)
interface NonEmptyListShowInstance<A> : Show<NonEmptyList<A>> {
  override fun NonEmptyList<A>.show(): String =
    toString()
}

@instance(NonEmptyList::class)
interface NonEmptyListFunctorInstance : Functor<ForNonEmptyList> {
  override fun <A, B> Kind<ForNonEmptyList, A>.map(f: (A) -> B): NonEmptyList<B> =
    fix().map(f)
}

@instance(NonEmptyList::class)
interface NonEmptyListApplicativeInstance : Applicative<ForNonEmptyList> {
  override fun <A, B> Kind<ForNonEmptyList, A>.ap(ff: Kind<ForNonEmptyList, (A) -> B>): NonEmptyList<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForNonEmptyList, A>.map(f: (A) -> B): NonEmptyList<B> =
    fix().map(f)

  override fun <A> just(a: A): NonEmptyList<A> =
    NonEmptyList.just(a)
}

@instance(NonEmptyList::class)
interface NonEmptyListMonadInstance : Monad<ForNonEmptyList> {
  override fun <A, B> Kind<ForNonEmptyList, A>.ap(ff: Kind<ForNonEmptyList, (A) -> B>): NonEmptyList<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForNonEmptyList, A>.flatMap(f: (A) -> Kind<ForNonEmptyList, B>): NonEmptyList<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, NonEmptyListOf<Either<A, B>>>): NonEmptyList<B> =
    NonEmptyList.tailRecM(a, f)

  override fun <A, B> Kind<ForNonEmptyList, A>.map(f: (A) -> B): NonEmptyList<B> =
    fix().map(f)

  override fun <A> just(a: A): NonEmptyList<A> =
    NonEmptyList.just(a)
}

@instance(NonEmptyList::class)
interface NonEmptyListComonadInstance : Comonad<ForNonEmptyList> {
  override fun <A, B> Kind<ForNonEmptyList, A>.coflatMap(f: (Kind<ForNonEmptyList, A>) -> B): NonEmptyList<B> =
    fix().coflatMap(f)

  override fun <A> Kind<ForNonEmptyList, A>.extract(): A =
    fix().extract()

  override fun <A, B> Kind<ForNonEmptyList, A>.map(f: (A) -> B): NonEmptyList<B> =
    fix().map(f)
}

@instance(NonEmptyList::class)
interface NonEmptyListBimonadInstance : Bimonad<ForNonEmptyList> {
  override fun <A, B> Kind<ForNonEmptyList, A>.ap(ff: Kind<ForNonEmptyList, (A) -> B>): NonEmptyList<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForNonEmptyList, A>.flatMap(f: (A) -> Kind<ForNonEmptyList, B>): NonEmptyList<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, NonEmptyListOf<Either<A, B>>>): NonEmptyList<B> =
    NonEmptyList.tailRecM(a, f)

  override fun <A, B> Kind<ForNonEmptyList, A>.map(f: (A) -> B): NonEmptyList<B> =
    fix().map(f)

  override fun <A> just(a: A): NonEmptyList<A> =
    NonEmptyList.just(a)

  override fun <A, B> Kind<ForNonEmptyList, A>.coflatMap(f: (Kind<ForNonEmptyList, A>) -> B): NonEmptyList<B> =
    fix().coflatMap(f)

  override fun <A> Kind<ForNonEmptyList, A>.extract(): A =
    fix().extract()
}

@instance(NonEmptyList::class)
interface NonEmptyListFoldableInstance : Foldable<ForNonEmptyList> {
  override fun <A, B> Kind<ForNonEmptyList, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> arrow.Kind<arrow.data.ForNonEmptyList, A>.foldRight(lb: arrow.core.Eval<B>, f: (A, arrow.core.Eval<B>) -> arrow.core.Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> Kind<ForNonEmptyList, A>.isEmpty(): kotlin.Boolean =
    fix().isEmpty()
}

@instance(NonEmptyList::class)
interface NonEmptyListTraverseInstance : Traverse<ForNonEmptyList> {
  override fun <A, B> Kind<ForNonEmptyList, A>.map(f: (A) -> B): NonEmptyList<B> =
    fix().map(f)

  override fun <G: KindType, A, B> Kind<ForNonEmptyList, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, NonEmptyList<B>> =
    fix().traverse(AP, f)

  override fun <A, B> Kind<ForNonEmptyList, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> arrow.Kind<arrow.data.ForNonEmptyList, A>.foldRight(lb: arrow.core.Eval<B>, f: (A, arrow.core.Eval<B>) -> arrow.core.Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> Kind<ForNonEmptyList, A>.isEmpty(): kotlin.Boolean =
    fix().isEmpty()
}

@instance(NonEmptyList::class)
interface NonEmptyListSemigroupKInstance : SemigroupK<ForNonEmptyList> {
  override fun <A> Kind<ForNonEmptyList, A>.combineK(y: Kind<ForNonEmptyList, A>): NonEmptyList<A> =
    fix().nelCombineK(y)
}

fun <F: KindType, A> Reducible<F>.toNonEmptyList(fa: Kind<F, A>): NonEmptyList<A> =
  fa.reduceRightTo({ a -> NonEmptyList.of(a) }, { a, lnel ->
    lnel.map { nonEmptyList -> NonEmptyList(a, listOf(nonEmptyList.head) + nonEmptyList.tail) }
  }).value()

object NonEmptyListContext : NonEmptyListBimonadInstance, NonEmptyListTraverseInstance, NonEmptyListSemigroupKInstance {
  override fun <A, B> Kind<ForNonEmptyList, A>.map(f: (A) -> B): NonEmptyList<B> =
    fix().map(f)
}

infix fun <A> ForNonEmptyList.extensions(f: NonEmptyListContext.() -> A): A =
  f(NonEmptyListContext)