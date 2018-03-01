package lambdasinaction.chap3;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 方法引用
 *
 */
public class MethodReference {
    /**
     * inventory.sort((Apple a1,Apple a2)-> a1.getWeight().compareTo(a2.getWeight())
     * inventory.sort(comparing(Apple:getWeight))
     *
     * 方法引用，可以看做仅仅调用特定方法的Lambda的一种快捷写法：如果一个Lambda代表的只是“直接调用这个方法”，最好还是用名称调用而不是去描述如何调用。
     * 将 方法引用看做就是仅仅涉及单一方法的lambda语法糖
     *
     * 如何构建方法引用：
     * (1)指向静态方法的引用,Integer:parseInt
     * (2)指向任意实例方法的方法引用，String:length
     * (3)指向现有方对象的实例方法的方法引用，
     * 以及其他针对构造函数、数组构造函数和父类调用(super-call)的一些特殊形式的方法引用
     *
     * 构造函数引用：
     *
     *
     */
    public static void main(String[] args) {
        // example
        Function<String,Integer> stingToInteger1=(String s)-> Integer.parseInt(s);
        Function<String,Integer> stingToInteger2=Integer::parseInt;

        BiPredicate<List<String>,String> contains1=(list,element)->list.contains(element);
        BiPredicate<List<String>,String> contains2=List::contains;


        //ClassName::new
        Supplier<Apple> c1=Apple::new;  // ()->new Apple()
        Apple a1=c1.get(); //产生一个新的Apple

        Function<Integer,Apple> c2=Apple::new; //  (weight)->new Apple(weight)
        Apple a2=c2.apply(10);

        BiFunction<Integer,String,Apple> c3=Apple::new;// (color,weight)->new Apple(color,weight);
        Apple a3=c3.apply(10,"green");

        /**
         * 不将构造函数实例化却能够引用它
         */

         List<Integer> weights= Arrays.asList(1,2,3,486,54);
         List<Apple> apples=map(weights,Apple::new);



    }

    public static List<Apple> map(List<Integer> list,Function<Integer,Apple> f){
        List<Apple> result=new ArrayList<>();
        for(Integer e:list){
            result.add(f.apply(e));
        }
        return result;
    }
}
