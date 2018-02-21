package me.functional.data;

public class Pair<A,B> {
  public final A fst;
  public final B snd;

  private Pair(A a, B b) {
    this.fst = a;
    this.snd = b;
  }

  public static <A,B> Pair<A,B> of(A a, B b) {
    return new Pair<A,B>(a,b);
  }

  public String toString() {
    return "<" + fst.toString() + "," + snd.toString() + ">";
  }
}
