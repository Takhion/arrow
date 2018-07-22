package arrow.instances

import arrow.Kind
import arrow.KindType
import arrow.core.*
import arrow.instance
import arrow.typeclasses.*
import arrow.instances.traverse as tryTraverse

@instance(Try::class)
interface TryApplicativeErrorInstance : TryApplicativeInstance, ApplicativeError<ForTry, Throwable> {

  override fun <A> raiseError(e: Throwable): Try<A> =
    Failure(e)

  override fun <A> Kind<ForTry, A>.handleErrorWith(f: (Throwable) -> Kind<ForTry, A>): Try<A> =
    fix().recoverWith { f(it).fix() }

}

@instance(Try::class)
interface TryMonadErrorInstance : TryMonadInstance, MonadError<ForTry, Throwable> {
  override fun <A> raiseError(e: Throwable): Try<A> =
    Failure(e)

  override fun <A> Kind<ForTry, A>.handleErrorWith(f: (Throwable) -> Kind<ForTry, A>): Try<A> =
    fix().recoverWith { f(it).fix() }
}

@instance(Try::class)
interface TryEqInstance<A> : Eq<Try<A>> {

  fun EQA(): Eq<A>

  fun EQT(): Eq<Throwable>

  override fun Try<A>.eqv(b: Try<A>): Boolean = when (this) {
    is Success -> when (b) {
      is Failure -> false
      is Success -> EQA().run { value.eqv(b.value) }
    }
    is Failure -> when (b) {
      is Failure -> EQT().run { exception.eqv(b.exception) }
      is Success -> false
    }
  }

}

@instance(Try::class)
interface TryShowInstance<A> : Show<Try<A>> {
  override fun Try<A>.show(): String =
    toString()
}

@instance(Try::class)
interface TryFunctorInstance : Functor<ForTry> {
  override fun <A, B> Kind<ForTry, A>.map(f: (A) -> B): Try<B> =
    fix().map(f)
}

@instance(Try::class)
interface TryApplicativeInstance : Applicative<ForTry> {
  override fun <A, B> Kind<ForTry, A>.ap(ff: Kind<ForTry, (A) -> B>): Try<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForTry, A>.map(f: (A) -> B): Try<B> =
    fix().map(f)

  override fun <A> just(a: A): Try<A> =
    Try.just(a)
}

@instance(Try::class)
interface TryMonadInstance : Monad<ForTry> {
  override fun <A, B> Kind<ForTry, A>.ap(ff: Kind<ForTry, (A) -> B>): Try<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForTry, A>.flatMap(f: (A) -> Kind<ForTry, B>): Try<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, TryOf<Either<A, B>>>): Try<B> =
    Try.tailRecM(a, f)

  override fun <A, B> Kind<ForTry, A>.map(f: (A) -> B): Try<B> =
    fix().map(f)

  override fun <A> just(a: A): Try<A> =
    Try.just(a)
}

@instance(Try::class)
interface TryFoldableInstance : Foldable<ForTry> {
  override fun <A> TryOf<A>.exists(p: (A) -> Boolean): Boolean =
    fix().exists(p)

  override fun <A, B> Kind<ForTry, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForTry, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

fun <A, B, G: KindType> TryOf<A>.traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Try<B>> = GA.run {
  fix().fold({ just(Try.raise(it)) }, { f(it).map({ Try.just(it) }) })
}

fun <A, G: KindType> TryOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, Try<A>> =
  tryTraverse(GA, ::identity)

@instance(Try::class)
interface TryTraverseInstance : Traverse<ForTry> {
  override fun <A, B> TryOf<A>.map(f: (A) -> B): Try<B> =
    fix().map(f)

  override fun <G: KindType, A, B> TryOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Try<B>> =
    tryTraverse(AP, f)

  override fun <A> TryOf<A>.exists(p: (A) -> Boolean): kotlin.Boolean =
    fix().exists(p)

  override fun <A, B> Kind<ForTry, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForTry, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

object TryContext : TryMonadErrorInstance, TryTraverseInstance {
  override fun <A, B> Kind<ForTry, A>.map(f: (A) -> B): Try<B> =
    fix().map(f)
}

infix fun <A> ForTry.extensions(f: TryContext.() -> A): A =
  f(TryContext)