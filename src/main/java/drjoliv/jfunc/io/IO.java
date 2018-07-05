package drjoliv.jfunc.io;

import java.io.FileReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import drjoliv.jfunc.collection.Unit;
import drjoliv.jfunc.contorl.Try;
import drjoliv.jfunc.function.F1;

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

  public static Try<String> now = Try.with(() -> {
    return System.currentTimeMillis() + "";
  });


  public static Try<String> getLine = Try.with(() -> {
    Scanner in = new Scanner(System.in);
    String s = in.nextLine();
    in.close();
    return s;
  });
}
