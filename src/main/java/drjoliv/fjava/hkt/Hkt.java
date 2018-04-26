package me.functional.hkt;

/**
 * A Higher Kinded Type with only one type parameter.
 * @param <M> The witness type.
 * @param <a> A type parameter.
 * @author drjoliv@gmail.com
 */
public interface Hkt<M extends Witness,a> extends Witness{}
