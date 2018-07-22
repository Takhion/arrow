package arrow.kindedj

import arrow.Kind
import arrow.Kind2
import arrow.KindType
import arrow.Witness
import io.kindedj.HkJ2
import io.kindedj.Hk as HK_J

abstract class ForConvert : Witness<Convert>()

@Suppress("UNCHECKED_CAST")
fun <F, A> Kind2<ForConvert, F, A>.fromArrow(): HK_J<F, A> = (this as Convert.FromKindedJToArrow<F, A>).toKindedJ()

fun <F: KindType, A> HkJ2<ForConvert, F, A>.toArrow(): Kind<F, A> = (this as Convert.FromArrowToKindedJ<F, A>).toArrow()

fun <F, A> HK_J<F, A>.fromKindedJ(): Kind2<ForConvert, F, A> = Convert.fromKindedJ(this)

fun <F: KindType, A> Kind<F, A>.toKindedJ(): HkJ2<ForConvert, F, A> = Convert.toKindedJ(this)
