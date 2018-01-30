package lambdasinaction.chap5;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by SML on 2018/1/30.
 *
 * @author SML
 */
public class Test {
    public static void main(String[] args) {

        // random stream of doubles with Stream.generate
        Stream.generate(Math::random)
                .limit(10)
                .forEach(System.out::println);

        // stream of 1s with Stream.generate
        IntStream.generate(() -> 1)
                .limit(5)
                .forEach(System.out::println);

    }

}
