package drjoliv.jfunc.io;

import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import drjoliv.jfunc.contorl.Either;
import drjoliv.jfunc.contorl.Try;
import drjoliv.jfunc.data.Unit;
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

  public static F1<InputStream,Try<String>> readFileFromStream = s -> Try.with(() -> {
    System.out.println(s == null);
    Scanner in = new Scanner(s);
    StringBuffer buffer = new StringBuffer();
    while(in.hasNextLine())
      buffer.append(in.nextLine());
    in.close();
    return buffer.toString();
  });

  public static void main(String[] args) {
    URL url = IO.class.getResource("hello.txt");
    System.out.println(url == null);
    Try<InputStream> stream = Try.with(() -> url.openStream());
    Try<String> test = stream.bind(readFileFromStream);
    Either<Exception,String> e = test.run();
    e.consume(ex -> ex.printStackTrace()
        , s -> System.out.println(s));
  }

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
