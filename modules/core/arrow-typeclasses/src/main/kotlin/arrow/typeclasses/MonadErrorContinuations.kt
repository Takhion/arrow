package arrow.typeclasses

import arrow.KindType
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.RestrictsSuspension

@RestrictsSuspension
open class MonadErrorContinuation<F: KindType, A>(val ME: MonadError<F, Throwable>, override val context: CoroutineContext = EmptyCoroutineContext) :
  MonadContinuation<F, A>(ME), MonadError<F, Throwable> by ME {

  override fun resumeWithException(exception: Throwable) {
    returnedMonad = ME.raiseError(exception)
  }
}