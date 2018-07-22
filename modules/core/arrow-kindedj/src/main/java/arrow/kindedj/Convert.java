package arrow.kindedj;

import org.jetbrains.annotations.NotNull;

import arrow.Kind;
import arrow.KindType;

public class Convert extends ForConvert {
    @NotNull
    public static <F, A> FromKindedJToArrow<F, A> fromKindedJ(@NotNull io.kindedj.Hk<F, A> hk) {
        return new FromKindedJToArrow<>(hk);
    }

    @NotNull
    public static <F extends KindType, A> FromArrowToKindedJ<F, A> toKindedJ(@NotNull Kind<F, A> hk) {
        return new FromArrowToKindedJ<>(hk);
    }

    public static class FromKindedJToArrow<F, A> extends Kind<Kind<ForConvert, F>, A> {

        @NotNull
        private final io.kindedj.Hk<F, A> hk;

        FromKindedJToArrow(@NotNull io.kindedj.Hk<F, A> hk) {
            this.hk = hk;
        }

        @NotNull
        public Kind<Kind<ForConvert, F>, A> toArrow() {
            return this;
        }

        @NotNull
        public io.kindedj.Hk<F, A> toKindedJ() {
            return hk;
        }
    }

    public static class FromArrowToKindedJ<F extends KindType, A> implements io.kindedj.Hk<io.kindedj.Hk<ForConvert, F>, A> {

        @NotNull
        private final Kind<F, A> hk;

        FromArrowToKindedJ(@NotNull Kind<F, A> hk) {
            this.hk = hk;
        }

        @NotNull
        public Kind<F, A> toArrow() {
            return hk;
        }

        @NotNull
        public io.kindedj.Hk<io.kindedj.Hk<ForConvert, F>, A> toKindedJ() {
            return this;
        }
    }
}
