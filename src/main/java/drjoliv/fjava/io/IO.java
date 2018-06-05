package drjoliv.fjava.io;

import java.io.FileReader;
import java.net.URL;
import java.util.Scanner;

import drjoliv.fjava.adt.Try;
import drjoliv.fjava.adt.Unit;
import drjoliv.fjava.functions.F1;

public class IO {

  public static F1<String,Try<Unit>> putStrLn = s -> Try.with(() -> {
    System.out.println(s);
    return Unit.unit;
  });

  public static F1<String,Try<URL>> url =  s -> Try.with(() -> {
    return new URL(s);
  });


  public static F1<String,Try<String>> readFile = s -> Try.with(() -> {
    Scanner in = new Scanner(new FileReader(s));
    StringBuffer buffer = new StringBuffer();
    while(in.hasNextLine())
      buffer.append(in.nextLine());
    in.close();
    return buffer.toString();
  });


  public static Try<String> getLine = Try.with(() -> {
    Scanner in = new Scanner(System.in);
    String s = in.nextLine();
    in.close();
    return s;
  });

}
