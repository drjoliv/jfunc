package drjoliv.jfunc.monad;

import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;

/**
 * Repeats monad action forever.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public class Forever<M, A, B> implements F2<Monad<M,A>, A, Monad<M,B>> {

  private static Forever INSTANCE = new Forever();

  /**
   *
   * @return a function that repeats a monad action forever.
   */
  public static <M, A, B> Forever<M,A,B> forever() {
    return INSTANCE;
  }

  @Override
  public Monad<M, B> call(Monad<M, A> a, A b) {
    F1<A, Monad<M, B>> f = Forever.<M,A,B>forever()
      .call(a);
    return a.bind(f);
  }
}
