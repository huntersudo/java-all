package main.java;

import com.sun.javafx.image.IntPixelGetter;
import org.omg.PortableInterceptor.INACTIVE;
import org.omg.PortableInterceptor.ServerRequestInfo;

import javax.management.relation.RoleUnresolved;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by SML on 2018/2/28.
 *
 * @author SML
 */
public class Hello {
    public static void main(String[] args) {
        IntStream.rangeClosed(2, 6)
                .forEach(x -> System.out.println("hello " + x + " bottles of beer"));

    }

}

class Foo{
    public static void main(String[] args) {
        Set<Integer> numbers=new HashSet<>();
        Set<Integer> newNumbers = Collections.unmodifiableSet(numbers); // 不可变集合



        Function<String,Boolean> isLongTweet =(String s) -> s.length()>60;
        boolean log= isLongTweet.apply("A very short tweet");

    }

/*
    public String getCarInsuranceName(Optional<Person> person,int minAge){
        return person.filter(p->p.getAge() >= minAge)
                .flatMap(Person::getCar)
                .flatMap(Car::getInsurence)
                .map(Insurence::getName)
                .orElse("Unknown");
    }*/

}
/*
class Foo2{
    public static void main(String[] args) {
        int count =0;
        Runnable inc= ()-> count+=1;  // error,count必须为final
        inc.run();
        System.out.println(count);
        inc.run();
    }
}*/

class Foo3{
    static int multiply(int x,int y){
        return x*y;
    }

    static Function<Integer,Integer> multiplyCurry(int x){
        return (Integer y)->x*y;
    }

    public static void main(String[] args) {
        int r=multiply(2,10);

        Stream.of(1,3,5,7)
                .map(multiplyCurry(2))   // map期望一个函数，而multiCurry返回的就是一个函数 2y
                .forEach(System.out::println);
    }


}
