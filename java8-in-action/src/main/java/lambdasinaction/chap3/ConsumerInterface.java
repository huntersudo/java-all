package lambdasinaction.chap3;

import java.util.Arrays;
import java.util.List;

/**
 * Created by SML on 2018/1/26.
 *
 * @author SML
 */
public class ConsumerInterface {

    public static void main(String[] args) {

        List<Integer> integers= Arrays.asList(1,2,3,4,5,6);
        Consumer<Integer> consumer=(Integer i)-> System.out.println(i);

        forEach(integers,consumer);

    }
    public static<T> void forEach(List<T> list,Consumer<T> c){
        for (T i:list){
            c.accept(i);
        }
    }

    /**
     * java.util.function.Consumer<T>
     * 对某些对象执行某些操作
     * @param <T>
     */
    public interface Consumer<T>{
        void accept(T t);
    }

}




