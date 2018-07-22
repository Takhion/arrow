package arrow.kindedj

import arrow.KindType
import arrow.Kind as HK_K

interface ArrowShow<in F: KindType> {
  fun <A> show(hk: HK_K<F, A>): String
}
