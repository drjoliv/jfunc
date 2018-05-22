package drjoliv.fjava.hkt;

/**
 * A Higher Kinded Type with two type parameters.
 * @param <M> The witness type.
 * @param <a> A type parameter.
 * @param <b> A type parameter.
 * @param <c> A type parameter.
 * @author drjoliv@gmail.com
 */
public interface Hkt3<M extends Witness,a,b,c> extends Hkt<Hkt2<M,a,b>, c> {}
