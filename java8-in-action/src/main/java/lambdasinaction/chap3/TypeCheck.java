package lambdasinaction.chap3;

import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.ToIntBiFunction;
/**
 * 类型检查
 * lambda类型检查是从上下文(目标类型)推断
 *
 */

public class TypeCheck {

    public static void main(String[] args) {
        // Filtering with lambdas
        List<Apple> inventory = Arrays.asList(new Apple(80, "green"), new Apple(155, "green"), new Apple(120, "red"));

        List<Apple> hevaierThan150g = filter(inventory, (Apple a) -> a.getWeight() > 150);
        /**
         *
         * filter(inventory,(Lambdas.Apple a)-> a.getWeight()>150);
         *  filter(inventory,Predicate<> p);
         * 目标类型是 Predicate<Apple>
         * T 绑定到 Apple
         * Apple -> boolean 匹配Lambda的签名
         * 类型检查过程:
         * (1) 找出filter方法的声明
         * (2) 要求它是Predicate<Apple> (目标类型) 对象的第二个正式参数
         * (3) Predicate<Apple> 是一个函数式接口，定义了一个test的抽象方法
         * (4) test方法描述了一个函数描述符，可以接受一个Apple ，并返回一个boolean
         * (5) filter 的任何实际参数都必须匹配和这个要求。
         *
         */

        /**
         * 同样的lambda ,不同的函数式接口
         * 有了目标类型的概念，同一个lambda表达式就可以与不同的函数式接口联系起来，只要它们的抽象方法签名能够兼容
         */
        Callable<Integer> c = () -> 32;
        PrivilegedAction<Integer> p = () -> 42;
        //
        Comparator<Apple> c1 = (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());

        ToIntBiFunction<Apple,Apple> c2 = (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());

        BiFunction<Apple,Apple,Integer> c3=(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());

        /**
         * 特殊的void 兼容规则
         * 如果一个lambda的主体是一个语句表达式，它就是和一个返回void的函数描述符兼容（参数列表兼容的情况下）
         * 以下都是合法的，即使add方法返回的是Boolean，而不是void
         */
        ArrayList<String> list=new ArrayList<>();
        Predicate<String> p1=  s->list.add(s);
        Consumer<String> b= s->list.add(s);  //

        /**
         *
         */
//        Object o2=() -> {System.out.println("hello fuck");};  //object不是一个函数式接口，故无法通过编译
        Runnable o1=() -> {System.out.println("hello fuck");};  //函数描述符 () -> void ;




    }

    public static <T> List<T> filter(List<T> list, Predicate<T> p) {
        List<T> result = new ArrayList<>();
        for (T e : list) {
            if (p.test(e)) {
                result.add(e);
            }
        }
        return result;
    }

    public interface Predicate<T> {
        boolean test(T t);
    }
}
