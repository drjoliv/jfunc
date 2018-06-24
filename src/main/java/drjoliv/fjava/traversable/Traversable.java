package drjoliv.fjava.traversable;

import drjoliv.fjava.adt.FList;
import static drjoliv.fjava.adt.Eval.*;

import java.util.Scanner;

import drjoliv.fjava.adt.Eval;
import drjoliv.fjava.adt.Try;
import static drjoliv.fjava.adt.Try.*;
import drjoliv.fjava.adt.Unit;
import drjoliv.fjava.applicative.Applicative;
import drjoliv.fjava.applicative.ApplicativePure;
import drjoliv.fjava.foldable.Foldable;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.hkt.Witness;
import drjoliv.fjava.io.IO;
import drjoliv.fjava.monad.Monad;
import drjoliv.fjava.monad.MonadUnit;

/**
 * A structure that can be traversed from left to right.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public interface Traversable<M extends Witness, A> extends Foldable<M,A> {

  /**
   * Maps each element of this traversable to an applicative, the applicatives are evaluteded from left to right to collect the result.
   * @param fn a function from {@code A} to {@code Applicative<N,B>}.
   * @param pure a strategy for creating applicatives.
   * @return an applicative cotaining a traversable.
   */
  public  <N extends Witness, F, B> Applicative<N,? extends Traversable<M,B>> traverse(F1<A, ? extends Applicative<N,B>> fn, ApplicativePure<N> pure);

  /**
   * Maps each element of this traverable to a monad, the monads are then evaluated from left to rigt to collect the result.
   *
   * @param fn a function from {@code A} to {@code Monad<N,B>}.
   * @param ret a strategy for creating monads.
   * @return a monad containing a monad.
   */
  public default <N extends Witness,B> Monad<N,? extends Traversable<M,B>> mapM(F1<A, ? extends Monad<N,B>> fn, MonadUnit<N> ret) {
    return (Monad<N,Traversable<M,B>>)traverse(fn, new ApplicativePure<N>(){
      @Override
      public <W> Applicative<N, W> pure(W a) {
        return ret.unit(a);
      }
    });
  }

  /**
   * Evaluates the applicatives within the given traversable from left to right returning a applicative containing a traverable.
   * @param tma a traverable containing applicatives.
   * @param pure a strategy to for lifting values into a applicative.
   * @return an applicative cotaining a traversable.
   */
  public static <N extends Witness, M extends Witness, A, B> Applicative<N, ? extends Traversable<M,B>> sequenceA(Traversable<M, ? extends Applicative<N,B>> tma, ApplicativePure<N> pure) {
    return (Applicative<N, Traversable<M,B>>)tma.traverse(F1.identity(), pure); 
  }

  /**
   *
   * Evaluates the monads within the given traversable from left to right returning a monad containing a traverable.
   * @param tma a traverable containing monads.
   * @param ret a stregty for lifting values into a monad.
   * @return a monad containing a monad.
   */
  public static <N extends Witness, M extends Witness, A, B> Monad<N, ? extends Traversable<M,B>> sequence(Traversable<M,? extends Monad<N,B>> tma, MonadUnit<N> ret) {
    return (Monad<N, Traversable<M,B>>)tma.mapM(F1.identity(), ret);
  }
}
