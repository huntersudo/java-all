package lambdasinaction.chap2;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by SML on 2018/1/26.
 *
 * @author SML
 */
public class OtherExamples {
    public static void main(String[] args) {

        /**
         * 排序的例子
         * java.util.Comparator
         */

        List<Apple> inventory = Arrays.asList(new Apple(80,"green"),
                new Apple(155, "green"),
                new Apple(120, "red"));

        inventory.sort(new Comparator<Apple>() {
            @Override
            public int compare(Apple o1, Apple o2) {
                return o1.getWeight().compareTo(o2.getWeight());
            }
        });
        // lambda
        inventory.sort(
                (Apple o1, Apple o2)-> o1.getWeight().compareTo(o2.getWeight())
        );

        /**
         * Runnable
         */

        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello world");
            }
        });
        // lambda
        //public Thread(Runnable target)
        Thread t1=new Thread(() -> System.out.println("hello"));

        /**
         * GUI
         */


    }
}
