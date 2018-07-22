package arrow.kindedj

import arrow.Witness
import arrow.Kind as HK_K

object ForKatDataclass : Witness<KatDataclass1<*>>()

fun <A> HK_K<ForKatDataclass, A>.show(): String = (this as KatDataclass1<A>).a.toString()

data class KatDataclass1<out A>(val a: A) : HK_K<ForKatDataclass, A>()
