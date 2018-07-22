package arrow.dagger.instances

import arrow.KindType
import arrow.data.OptionTPartialOf
import arrow.instances.*
import arrow.typeclasses.*
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
abstract class OptionTInstances<F: KindType> {

  @Provides
  fun optionTFunctor(ev: DaggerOptionTFunctorInstance<F>): Functor<OptionTPartialOf<F>> = ev

  @Provides
  fun optionTApplicative(ev: DaggerOptionTApplicativeInstance<F>): Applicative<OptionTPartialOf<F>> = ev

  @Provides
  fun optionTMonad(ev: DaggerOptionTMonadInstance<F>): Monad<OptionTPartialOf<F>> = ev

  @Provides
  fun optionTFoldable(ev: DaggerOptionTFoldableInstance<F>): Foldable<OptionTPartialOf<F>> = ev

  @Provides
  fun optionTTraverse(ev: DaggerOptionTTraverseInstance<F>): Traverse<OptionTPartialOf<F>> = ev

  @Provides
  fun optionTSemigroupK(ev: DaggerOptionTSemigroupKInstance<F>): SemigroupK<OptionTPartialOf<F>> = ev

  @Provides
  fun optionTMonoidK(ev: DaggerOptionTMonoidKInstance<F>): MonoidK<OptionTPartialOf<F>> = ev

}

class DaggerOptionTFunctorInstance<F: KindType> @Inject constructor(val FF: Functor<F>) : OptionTFunctorInstance<F> {
  override fun FF(): Functor<F> = FF
}

class DaggerOptionTApplicativeInstance<F: KindType> @Inject constructor(val FF: Monad<F>) : OptionTApplicativeInstance<F> {
  override fun FF(): Monad<F> = FF
}

class DaggerOptionTMonadInstance<F: KindType> @Inject constructor(val FF: Monad<F>) : OptionTMonadInstance<F> {
  override fun FF(): Monad<F> = FF
}

class DaggerOptionTFoldableInstance<F: KindType> @Inject constructor(val FFF: Foldable<F>) : OptionTFoldableInstance<F> {
  override fun FFF(): Foldable<F> = FFF
}

class DaggerOptionTTraverseInstance<F: KindType> @Inject constructor(val FFF: Traverse<F>) : OptionTTraverseInstance<F> {
  override fun FFF(): Traverse<F> = FFF
}

class DaggerOptionTSemigroupKInstance<F: KindType> @Inject constructor(val FF: Monad<F>) : OptionTSemigroupKInstance<F> {
  override fun FF(): Monad<F> = FF
}

class DaggerOptionTMonoidKInstance<F: KindType> @Inject constructor(val FF: Monad<F>) : OptionTMonoidKInstance<F> {
  override fun FF(): Monad<F> = FF
}