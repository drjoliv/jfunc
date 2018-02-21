package me.functional.data;

import java.util.function.BiFunction;
import java.util.function.Function;

import me.functional.QuadFunction;
import me.functional.TriFunction;

/**
 *
 *
 * @author drjoliv@gmail.com
 */
public class State<S,A> {

  private Function<S,Pair<S,A>> runState;

  private State(final Function<S,Pair<S,A>> runState) {
    this.runState = runState;
  }

  /**
   *
   *
   * @param runState
   * @return
   */
  public static <S,A> State<S,A> of(final Function<S,Pair<S,A>> runState) {
    return new State<S,A>(runState);
  }

  /**
   *
   *
   * @param fn
   * @return
   */
  public <B> State<S,B> map(final Function<A,B> fn) {
    return of( s -> {
      final Pair<S,A> p = runState.apply(s);
      return Pair.of(s,fn.apply(p.snd));
    });
  }

  public <B> State<S,B> For(Function<A, State<S,B>> fn) {
    return bind(fn);
  } 

    public <B,C> State<S,C> For(Function<A, State<S,B>> fn, BiFunction<A, B,State<S,C>> fn2) {
    return bind(a -> {
      return fn.apply(a).bind(b -> {
        return fn2.apply(a,b); 
      });
    });
  } 

    public <B,C,D> State<S,D> For(Function<A, State<S,B>> fn, BiFunction<A, B,State<S,C>> fn2,
      TriFunction<A,B,C,State<S,D>> fn3) {
    return bind(a -> {
      return fn.apply(a).bind(b -> {
        return fn2.apply(a,b).bind( c -> {
         return fn3.apply(a,b,c); 
        }); 
      });
    });
  }

    public <B,C,D,E> State<S,E> For(Function<A, State<S,B>> fn, BiFunction<A, B,State<S,C>> fn2,
      TriFunction<A,B,C,State<S,D>> fn3, QuadFunction<A,B,C,D,State<S,E>> fn4) {
    return bind(a -> {
      return fn.apply(a).bind(b -> {
        return fn2.apply(a,b).bind( c -> {
         return fn3.apply(a,b,c).bind( d -> {
          return fn4.apply(a,b,c,d); 
         }); 
        }); 
      });
    });
  }

  /**
   *
   *
   * @param state
   * @param fn
   * @return
   */
  public static <A,B,S> State<S,B> For(State<S,A> state, Function<A, State<S,B>> fn) {
    return state.bind(fn);
  } 

    /**
     *
     *
     * @param state
     * @param fn
     * @param fn2
     * @return
     */
    public static <A,B,C,S> State<S,C> For(State<S,A> state, Function<A, State<S,B>> fn, BiFunction<A, B,State<S,C>> fn2) {
    return state.bind(a -> {
      return fn.apply(a).bind(b -> {
        return fn2.apply(a,b); 
      });
    });
  } 

    /**
     *
     *
     * @param state
     * @param fn
     * @param fn2
     * @param fn3
     * @return
     */
    public static <A,B,C,D,S> State<S,D> For(State<S,A> state, Function<A, State<S,B>> fn, BiFunction<A, B,State<S,C>> fn2,
      TriFunction<A,B,C,State<S,D>> fn3) {
    return state.bind(a -> {
      return fn.apply(a).bind(b -> {
        return fn2.apply(a,b).bind( c -> {
         return fn3.apply(a,b,c); 
        }); 
      });
    });
  }

    /**
     *
     *
     * @param state
     * @param fn
     * @param fn2
     * @param fn3
     * @param fn4
     * @return
     */
    public static <A,B,C,D,E,S> State<S,E> For(State<S,A> state, Function<A, State<S,B>> fn, BiFunction<A, B,State<S,C>> fn2,
      TriFunction<A,B,C,State<S,D>> fn3, QuadFunction<A,B,C,D,State<S,E>> fn4) {
    return state.bind(a -> {
      return fn.apply(a).bind(b -> {
        return fn2.apply(a,b).bind( c -> {
         return fn3.apply(a,b,c).bind( d -> {
          return fn4.apply(a,b,c,d); 
         }); 
        }); 
      });
    });
  }

  /**
   *
   *
   * @param fn
   * @return
   */
  public <B> State<S,B> bind(final Function<A,State<S,B>> fn) {
    return of(s -> {
      final Pair<S,A> p = runState.apply(s);
      return fn.apply(p.snd)
        .runState.apply(p.fst);
    });
  }

  /**
   *
   *
   * @param fn
   * @return
   */
  public <B> State<S,B> bind(final State<S,B> state) {
    return bind(s -> state);
  }


  public static <S> State<S,S> get() {
    return of(s -> Pair.of(s,s));
  }

  public Pair<S,A> evalState(S s) {
    return runState.apply(s);
  }

  public A execute(S s) {
    return runState.apply(s).snd;
  }

  public S executeState(S s) {
    return runState.apply(s).fst;
  }


}
