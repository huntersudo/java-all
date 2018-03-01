package lambdasinaction.chap6;

<<<<<<< HEAD
=======
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

>>>>>>> develop
import static java.util.stream.Collectors.*;
import static lambdasinaction.chap6.Dish.menu;

public class Reducing {

    public static void main(String ... args) {
        System.out.println("Total calories in menu: " + calculateTotalCalories());
        System.out.println("Total calories in menu: " + calculateTotalCaloriesWithMethodReference());
        System.out.println("Total calories in menu: " + calculateTotalCaloriesWithoutCollectors());
        System.out.println("Total calories in menu: " + calculateTotalCaloriesUsingSum());
<<<<<<< HEAD
    }

    private static int calculateTotalCalories() {
        return menu.stream().collect(reducing(0, Dish::getCalories, (Integer i, Integer j) -> i + j));
=======

        Stream<Integer> stream = Arrays.asList(1,2,3,4).stream();
        List<Integer> numbers =stream.reduce(
                new ArrayList<Integer>(),
                (List<Integer> list,Integer e)->{list.add(e);return list;},
                (List<Integer> list1,List<Integer> list2)->{list1.addAll(list2);return list1;}
        );

        /**
         * 实际上counting也是利用reducing实现的
         public static <T> Collector<T, ?, Long>
         counting() {
         return reducing(0L, e -> 1L, Long::sum);
         }
         */
    }

    private static int calculateTotalCalories() {
        return menu.stream().collect(reducing(0, Dish::getCalories, (i, j) -> i + j));
>>>>>>> develop
    }

    private static int calculateTotalCaloriesWithMethodReference() {
        return menu.stream().collect(reducing(0, Dish::getCalories, Integer::sum));
    }

    private static int calculateTotalCaloriesWithoutCollectors() {
        return menu.stream().map(Dish::getCalories).reduce(Integer::sum).get();
    }

    private static int calculateTotalCaloriesUsingSum() {
        return menu.stream().mapToInt(Dish::getCalories).sum();
    }
}