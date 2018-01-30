package lambdasinaction.chap3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by SML on 2018/1/26.
 *
 * @author SML
 */
public class FunctionInterface {
    public static void main(String[] args) {

        List<Integer> l=map(Arrays.asList(1,2,3,4,5),(Integer i)-> i*2);
        System.out.println(l);
    }

    public static <T, R> List<R> map(List<T> list, Function<T, R> f) {
        List<R> result = new ArrayList<>();
        for (T s : list) {
            result.add(f.apply(s));
        }
        return result;
    }

    /**
     * java.util.function.Function
     * 接受T，返回 R,实现map的操作
     *
     * @param <T>
     * @param <R>
     */
    public interface Function<T, R> {
        R apply(T t);
    }
}
