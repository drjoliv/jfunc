package me.functional.hkt;

/**
 * A Higher Kinded Type with two type parameters.
 * @param <M> The witness type.
 * @param <a> A type parameter.
 * @param <b> A type parameter.
 * @author drjoliv@gmail.com
 */
public interface Hkt2<M extends Witness, a, b> extends Hkt<Hkt<M, a>, b> {}
