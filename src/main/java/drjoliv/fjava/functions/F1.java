package drjoliv.fjava.functions;

import java.util.ArrayList;
import java.util.function.Function;

import drjoliv.fjava.data.DList;
import drjoliv.fjava.data.Dequeue;
import static drjoliv.fjava.data.Dequeue.*;
import drjoliv.fjava.data.FList;
import static drjoliv.fjava.data.FList.*;
import drjoliv.fjava.data.Stack;
import static drjoliv.fjava.functions.ComposedFunc.*;

public interface F1<A,B>{

  public B call(A a);

  public default F1<A,F0<B>> curry() {
    return a -> () -> call(a);
  }

  public default Function<A,B> toJFunc() {
    return a -> call(a);
  }


  public default <C> F1<A,C> then(F1< ? super B, C> fn) {
    return fn.before(this);
  }

  public default <C> F1<C,B> before(F1<C ,? extends A> fn) {
    return isComposedFunc(fn)
      ? fn.then(this)
      : new ComposedFunc<C,B>(dequeue(fn,this));
  }

  public static <A> F1<A, A> identity() {
    return a -> a;
  }

  public static <A, B> F1<A, B> fromJFunction(Function<A, B> fn) {
    return a -> fn.apply(a);
  }

  public static void main(String[] args) {


    long then = 0;
    //DList<Integer> d1 = DList.empty();
    //then = System.currentTimeMillis();
    //System.out.println("DList");
    //for(int i = 0; i < 10; i++) {
    //  d1 = d1.concat(i);
    //}
    //d1 = d1.map(i -> 2 * i);
    //print(d1.toList());
    //System.out.println(System.currentTimeMillis() - then);


    FList<Integer> d2 = FList.empty();
    System.out.println("FLIST");
    then = System.currentTimeMillis();
    for(int i = 100000; i > 0 ; i--) {
      d2 = d2.add(i);
    }
    //d2 = d2.reverse();
    System.out.println(d2.last());
    System.out.println(System.currentTimeMillis() - then);

    //System.out.println("ARRAY");
    //ArrayList<Integer> list = new ArrayList<>();;
    //then = System.currentTimeMillis();
    //for(int i = 0; i < 100000; i++) {
    //  list.add(0,i);
    //}
    //System.out.println(System.currentTimeMillis() - then);




    //F2<Integer,Integer,Integer> add = (i1,i2) -> i1 + i2;
    //F1<Integer,Integer> nop = i -> i;

    //then = System.currentTimeMillis();
    //for(int i = 0; i < 1000; i++) {
    //  nop = nop.then(add.call(i));
    //}

    //Integer sum1 = nop.call(0);
    //System.out.println(sum1);
    //System.out.println(System.currentTimeMillis() - then);
  }
}
