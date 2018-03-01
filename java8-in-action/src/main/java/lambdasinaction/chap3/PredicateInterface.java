package lambdasinaction.chap3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by SML on 2018/1/26.
 *
 * @author SML
 */
public class PredicateInterface {
    public static void main(String[] args) {

        List<String> listOfStrings = Arrays.asList("aa", "", "bb", "");

        Predicate<String> emPredicate = (String s) -> !s.isEmpty();

        List<String> noEmpty = filter(listOfStrings, emPredicate);
        System.out.println(noEmpty);


    }

    /**
     * @param list
     * @param p
     * @param <T>
     * @return
     */
    public static <T> List<T> filter(List<T> list, Predicate<T> p) {
        List<T> result = new ArrayList<>();
        for (T e : list) {
            if (p.test(e)) {
                result.add(e);
            }
        }
        return result;
    }


    /**
     * java.util.function.Predicate<T>
     *  布尔表达式
     * @param <T>
     */
    public interface Predicate<T> {
        boolean test(T t);
    }
}
