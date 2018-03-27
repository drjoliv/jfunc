package me.functional.transformers;

import java.util.function.Function;
import java.util.function.Predicate;

import static me.functional.data.Pair.*;

import me.functional.data.Pair;
import me.functional.hkt.Hkt2;
import me.functional.hkt.Hkt3;
import me.functional.hkt.Witness;
import me.functional.transformers.ParserT.μ;
import me.functional.type.Monad;
import me.functional.type.MonadUnit;
import me.functional.type.Stream;

public class ParserT<S,T,A> implements Monad<Hkt2<ParserT.μ,S,T>,A>,  Hkt3<ParserT.μ,A,S,T>  {

  @Override
  public MonadUnit<Hkt2<μ, S, T>> yield() {
    // TODO Auto-generated method stub
    return null;
  }

  public static class μ implements Witness{}

  @Override
  public <B> ParserT<S,T,B> fmap(Function<? super A, B> fn) {
    return new ParserT<S,T,B>(runParser.andThen(pResult -> {
      if(pResult.isReuslt()) {
        Pair<A,Stream<S,T>> p = pResult.result();
        return ParserResult.result(pair(fn.apply(p.fst),p.snd));
      } else {
        return ParserResult.error(pResult.errorMessage());
      }
    }));
  }

  @Override
  public <B> ParserT<S,T,B> mBind(Function<? super A, ? extends Monad<Hkt2<μ, S, T>, B>> fn) {
     return new ParserT<S,T,B>(stream -> {
       ParserResult<A,S,T> result = runParser.apply(stream);
       if(result.isReuslt()) {
         Pair<A,Stream<S,T>> p = result.result();
         return asParser(fn.apply(p.fst)).runParser.apply(p.snd);
       } else {
         return ParserResult.error(result.errorMessage());
       }
     });
  }

  @Override
  public <B> ParserT<S,T,B> semi(Monad<Hkt2<μ, S, T>, B> mb) {
    return mBind(s -> mb);
  }


  private Function<Stream<S,T>,ParserResult<A,S,T>> runParser;

  public static <S,T,A> ParserT<S,T,A> asParser(Monad<Hkt2<ParserT.μ, S, T>, A> monad) {
    return (ParserT<S,T,A>) monad;
  }

  private ParserT(Function<Stream<S,T>,ParserResult<A,S,T>> runParser) {
    this.runParser = runParser;
  }


 public static abstract class ParserResult<A,S,T> {
   public abstract boolean isReuslt();
   public abstract String errorMessage();
   public abstract Pair<A,Stream<S,T>> result();

   public static class Error<A,S,T> extends ParserResult<A,S,T> {
     private final String errorMessage;
     private Error(String errorMessage) {
       this.errorMessage = errorMessage;
     }
    @Override
    public String errorMessage() {
      return errorMessage;
    }
    @Override
    public boolean isReuslt() {
      return false;
    }
    @Override
    public Pair<A, Stream<S, T>> result() {
          throw new UnsupportedOperationException();
    }
   }

   public static class Result<A,S,T> extends ParserResult<A,S,T> {
     private final Pair<A,Stream<S,T>> result;
     private Result(Pair<A,Stream<S,T>> result) {
       this.result = result;
     }

  @Override
  public String errorMessage() {
          throw new UnsupportedOperationException();
  }

  @Override
  public boolean isReuslt() {
    return true;
  }

  @Override
  public Pair<A, Stream<S, T>> result() {
    return result;
  }
  }

   public static <A,S,T> ParserResult<A,S,T> error(String error) {
    return new Error<A,S,T>(error);
   }

    public static <A, S, T> ParserResult<A, S, T> result(Pair<A, Stream<S, T>> result) {
      return new Result<A, S, T>(result);
    }
      public static <A, S, T> ParserResult<A, S, T> result(A a, Stream<S, T> stream) {
      return new Result<A, S, T>(pair(a,stream));
    }
}
  //public static Parser<S,T,A> token(Function<T,Maybe<A>>)
//

//public static <S,T,A> ParserT<S,T,A> character(Charcter c) {
//  return statisfy(p -> p == c);
//}
//
//public static <S> ParserT<S,Character,Character> statisfy(Predicate<Character> p)
//  return 
}
