package drjoliv.fjava.control.bind;

import static drjoliv.fjava.data.T2.t2;

import drjoliv.fjava.control.Bind;
import drjoliv.fjava.control.BindUnit;
import drjoliv.fjava.data.Stream;
import drjoliv.fjava.data.T2;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.hkt.Hkt2;
import drjoliv.fjava.hkt.Hkt3;
import drjoliv.fjava.hkt.Witness;

public class ParserT<S,T,A> implements Bind<Hkt2<ParserT.μ,S,T>,A>,  Hkt3<ParserT.μ,A,S,T>  {

  @Override
  public BindUnit<Hkt2<μ, S, T>> yield() {
    // TODO Auto-generated method stub
    return null;
  }

  public static class μ implements Witness{}

  @Override
  public <B> ParserT<S,T,B> map(F1<? super A, B> fn) {
    return new ParserT<S,T,B>(runParser.then(pResult -> {
      if(pResult.isReuslt()) {
        T2<A,Stream<S,T>> p = pResult.result();
        return ParserResult.result(p.map1(fn));
      } else {
        return ParserResult.error(pResult.errorMessage());
      }
    }));
  }

  @Override
  public <B> ParserT<S,T,B> bind(F1<? super A, ? extends Bind<Hkt2<μ, S, T>, B>> fn) {
     return new ParserT<S,T,B>(stream -> {
       ParserResult<A,S,T> result = runParser.call(stream);
       if(result.isReuslt()) {
         T2<A,Stream<S,T>> p = result.result();
         return asParser(fn.call(p._1())).runParser.call(p._2());
       } else {
         return ParserResult.error(result.errorMessage());
       }
     });
  }

  @Override
  public <B> ParserT<S,T,B> semi(Bind<Hkt2<μ, S, T>, B> mb) {
    return bind(s -> mb);
  }


  private F1<Stream<S,T>,ParserResult<A,S,T>> runParser;

  public static <S,T,A> ParserT<S,T,A> asParser(Bind<Hkt2<ParserT.μ, S, T>, A> monad) {
    return (ParserT<S,T,A>) monad;
  }

  private ParserT(F1<Stream<S,T>,ParserResult<A,S,T>> runParser) {
    this.runParser = runParser;
  }


 public static abstract class ParserResult<A,S,T> {
   public abstract boolean isReuslt();
   public abstract String errorMessage();
   public abstract T2<A,Stream<S,T>> result();

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
    public T2<A, Stream<S, T>> result() {
          throw new UnsupportedOperationException();
    }
   }

   public static class Result<A,S,T> extends ParserResult<A,S,T> {
     private final T2<A,Stream<S,T>> result;
     private Result(T2<A,Stream<S,T>> result) {
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
    public T2<A, Stream<S, T>> result() {
      return result;
    }
  }

   public static <A,S,T> ParserResult<A,S,T> error(String error) {
    return new Error<A,S,T>(error);
   }

    public static <A, S, T> ParserResult<A, S, T> result(T2<A, Stream<S, T>> result) {
      return new Result<A, S, T>(result);
    }
      public static <A, S, T> ParserResult<A, S, T> result(A a, Stream<S, T> stream) {
      return new Result<A, S, T>(t2(a,stream));
    }
}
}
