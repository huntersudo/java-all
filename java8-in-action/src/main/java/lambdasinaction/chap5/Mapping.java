package lambdasinaction.chap5;

import lambdasinaction.chap4.*;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static lambdasinaction.chap4.Dish.menu;

public class Mapping {

    public static void main(String... args) {

        // map
        List<String> dishNames = menu.stream()
                .map(Dish::getName)
                .collect(toList());
        System.out.println(dishNames);

        // map
        List<String> words = Arrays.asList("Hello", "World");
        List<Integer> wordLengths = words.stream()
                .map(String::length)
                .collect(toList());
        System.out.println(wordLengths);

        List<String> words1 = Arrays.asList("Hello", "World");
        // Arrays.stream ,接受一个数组，产生一个流
        words1.stream()
                .flatMap((String line) -> Arrays.stream(line.split("")))
                .distinct()
                .forEach(System.out::println);  //列出各不相同的字符

        System.out.printf("here");

        // 返回可以被3整除的数对
        List<Integer> numbers1 = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> numbers2 = Arrays.asList(6, 7, 8);
        List<int[]> pairs =
                numbers1.stream()
                        .flatMap((Integer i) -> numbers2.stream()
                                .map(j -> new int[]{i, j})
                        )
                        .filter(pair -> (pair[0] + pair[1]) % 3 == 0)
                        .collect(toList());


        //只返回和能被3整除的数对
        List<Integer> numbers11 = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> numbers22 = Arrays.asList(6, 7, 8);
        List<int[]> pairs1 =
                numbers11.stream()
                        .flatMap(i -> numbers22.stream()
                                .filter(j -> (i + j) % 3 == 0)
                                .map(j -> new int[]{i, j})
                        )
                        .collect(toList());  //Stream<Integer[]>


        pairs.forEach(pair -> System.out.println("(" + pair[0] + ", " + pair[1] + ")"));
    }
}
