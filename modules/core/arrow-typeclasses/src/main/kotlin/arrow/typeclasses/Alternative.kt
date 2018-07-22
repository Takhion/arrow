package arrow.typeclasses

import arrow.KindType

interface Alternative<F: KindType> : Applicative<F>, MonoidK<F>
