package lambdasinaction.chap3;

import com.sun.javafx.stage.FocusUngrabEvent;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 lambda复合
 */
public class LambdaComposite {
    /**
     *
     *
     */
    public static void main(String[] args) {

        // Filtering with lambdas
        List<Apple> inventory = Arrays.asList(new Apple(80,"green"), new Apple(155, "green"), new Apple(120, "red"));

        /**
         * 比较器复合
         */
        //使用静态方法，Comaprator.comparing
        Comparator<Apple> c= Comparator.comparing(Apple::getWeight);

        //逆序
        inventory.sort(c.reversed());
        //比较器链
        inventory.sort(c
                .reversed()
                .thenComparing(Apple::getColor));

        /**
         * 谓词复合
         * 使用 negate ,and,or
         */
        Predicate<Apple> redApple=(a)->"red".equals(a.getColor());
        Predicate<Apple> notRedApple =redApple.negate();// 当前谓词的 非，不是红色的苹果

        Predicate<Apple> redAndHeavy=redApple.and(a->a.getWeight()>150); //红色且比较重

        Predicate<Apple> redAndHeavyOrGreenApple=redApple.and(a->a.getWeight()>150).or(a->"green".equals(a.getColor())); //红色且比较重或者是绿色的

        /**
         * 函数复合
         * Function 的andThen和compose默认方法
         */
        Function<Integer,Integer> f=x->x+1;
        Function<Integer,Integer> g=x->x*2;

        Function<Integer,Integer> h=f.andThen(g);  //g(f(x)) 流水线
        System.out.println(h.apply(10)); //22

        Function<Integer,Integer> hh=f.compose(g);  //f(g(x))
        System.out.println(hh.apply(10));  //21


        /**
         * 积分
         * f(x)=x+10
         * ∫3,7 f(x)dx
         * integrate(x+10,3,7) ：错误的，
         * dx： 以x为自变量，结果是x+10的那个函数
         * 必须写成： integrate((double x)->x+10,3,7)
         * or      integrate((C::f,3,7)
         */

        System.out.println(integrate((double x)->x+10,3,7));

    }
    /*//错误的方法
    public double integerate((double -> double)f,double a,double b){
        return (f(a)+f(b))*(b-a)/2.0
    }*/
    public static double integrate(DoubleFunction<Double> f,double a,double b){
        return (f.apply(a)+f.apply(b))*(b-a)/2.0;
    }

    public interface DoubleFunction<R> {
        R apply(double value);
    }

}
