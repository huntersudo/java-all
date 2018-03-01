package lambdasinaction.chap3;

import java.util.function.Predicate;

/**
 * Created by SML on 2018/1/26.
 *
 * @author SML
 */
public class BoxUnbox {
    public static void main(String[] args) {

        IntPredicate evenNumbers = (int i) -> i % 2 == 0;
        System.out.println(evenNumbers.test(100));   // no box

        Predicate<Integer> odds = (Integer i) -> i % 2 == 1;
        System.out.println(odds.test(1000)); //box

    }

    public interface IntPredicate {
        boolean test(int t);
    }
}
