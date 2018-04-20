package me.functional.data;

public class T2<A,B> {
  public final A fst;
  public final B snd;

  private T2(A a, B b) {
    this.fst = a;
    this.snd = b;
  }

  public static <A,B> T2<A,B> t2(A a, B b) {
    return new T2<A,B>(a,b);
  }

  public String toString() {
    return "<" + fst.toString() + "," + snd.toString() + ">";
  }
}
