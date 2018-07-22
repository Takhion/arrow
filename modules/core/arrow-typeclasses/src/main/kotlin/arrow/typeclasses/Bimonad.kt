package arrow.typeclasses

import arrow.KindType

interface Bimonad<F: KindType> : Monad<F>, Comonad<F>