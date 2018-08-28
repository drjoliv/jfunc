package drjoliv.jfunc.eq;

import drjoliv.jfunc.contorl.either.Either;
import drjoliv.jfunc.contorl.maybe.Maybe;
import drjoliv.jfunc.data.list.FList;

public interface EQ<A> {

  public static EQ INSTANCE = (e, e1) -> e.equals(e1);
  public boolean eq(A a, A a1);

  public static <A> EQ<Maybe<A>> eqMaybe(EQ<A> e) {
    return new EQ<Maybe<A>>() {
      public boolean eq(Maybe<A> m, Maybe<A> m1) {
        return m.visit( n -> m1.visit( n1 -> true , s -> false)
                      , s -> m1.visit( n  -> false, s1 -> e.eq(s,s1)));
      }
    };
  }

  public static <A,L,R> EQ<Either<L,R>> eqEither(EQ<L> el, EQ<R> er) {
    return new EQ<Either<L,R>>() {
      public boolean eq(Either<L,R> e, Either<L,R> e1) {
        return e.visit( l -> e1.visit( l1 -> el.eq(l,l1) , s -> false)
                      , r -> e1.visit( n  -> false, r1 -> er.eq(r,r1)));
      }
    };
  }

  public static <A> EQ<FList<A>> eqFList(EQ<A> eq) {
    return new EQ<FList<A>> () {
      @Override
      public boolean eq(FList<A> a, FList<A> a1) {
        if(a.isEmpty() != a1.isEmpty())
          return false;
        while(!a.isEmpty() || !a.isEmpty()) {
          if(a.isEmpty() != a1.isEmpty())
            return false;
          if(!eq.eq(a.head(), a1.head()))
            return false;
          a  = a.tail();
          a1 = a1.tail();
        }
        return true;
      }
    };
  }

  public static <A> EQ<A> defEq() {
    return INSTANCE;
  }

  public static EQ<Integer> intEq() {
    return defEq();
  }

  public static EQ<Character> charEq() {
    return defEq();
  }

}
