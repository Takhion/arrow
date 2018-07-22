package arrow.recursion.typeclasses

import arrow.KindType

/**
 * Typeclass for types that can be generically folded and unfolded with algebras and coalgebras.
 */
interface Birecursive<F: KindType> : Recursive<F>, Corecursive<F>
