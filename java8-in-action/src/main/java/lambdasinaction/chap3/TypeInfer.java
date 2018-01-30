package lambdasinaction.chap3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 接 类型检查，继续 类型推断
 * 局部变量的使用
 */
public class TypeInfer {
    /**
     * 还可以继续简化，java编译器可以从上下文(目标类型)推断出用什么函数式接口来配合lambda表达式
     * 意味着 编译器可以推断适合lambda的签名，因为哈数描述符可以通过目标类型来得到，这样做的好处在于，
     *
     * 编译器可以了解lambda表达式的参数类型，这样就可以在lambda语法中省去标注参数类型-----
     */
    public static void main(String[] args) {
        List<Apple> inventory = Arrays.asList(new Apple(80, "green"), new Apple(155, "green"), new Apple(120, "red"));

        // 参数 a 没有显示类型
        List<Apple> greenApples=filter(inventory,a->"green".equals(a.getColor()));
        Comparator<Apple> c= (Apple a1, Apple a2)-> a1.getWeight().compareTo(a2.getWeight());
        Comparator<Apple> c1= (a1, a2)-> a1.getWeight().compareTo(a2.getWeight()); //有类型推断，可写可不写，看是否易读

        /**
         * 局部变量的使用,lambda支持使用自由变量，必须是final的
         */
        int portNumber=1337;
        Runnable r=()-> System.out.println(portNumber);
//        portNumber=1;   // 这就报错了，因为lambda只能捕获指派给他们的局部变量一次，

        /**
         * 闭包 closure, 闭包就是一个函数的实例，且它可以无限制的访问那个函数的非本地变量，闭包可以作为参数传递给另外一个函数;
         * java8的lambda和匿名类可以做类似闭包的事情：
         *    可以作为参数传递给方法，
         *    可以访问其作用域外的变量（限制：但是不能修改局部变量的内容,变量必须是final）
         * 可以认为lambda是对值封闭的，而不是对变量封闭，
         * because:
         *    局部变量是保存在栈上的，并且隐式表示它们仅限于其所在的线程， 如果允许捕获可改变的局部变量，就会引发造成线程不安全的可能性
         *    （实例便变量就可以，因为保存在堆中，而堆是在线程之间共享的）
         *
         */


    }

    public static <T> List<T> filter(List<T> list, TypeCheck.Predicate<T> p) {
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
