package lambdasinaction.chap5;

import org.omg.PortableInterceptor.INACTIVE;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 勾股数
 */
public class GouGu {
    public static void main(String[] args) {
        // expr %1 ==0 是否是整数

      /*  IntStream.rangeClosed(1,100)  //Stream<Integer>
                .filter(b-> Math.sqrt(a*a +b*b) %1==0)
                .boxed() //
                .map(b-> new int[]{a,b,(int)Math.sqrt(a*a+b*b)});
        这是因为 你的map会为流中的每个运算返回一个int[]，但是你需要的是一个对象流
        故使用mapToObj
        */
        /*IntStream.rangeClosed(1,100)  //Stream<Integer>
                .filter(b-> Math.sqrt(a*a +b*b) %1==0)
                .mapToObj(b-> new int[]{a,b,(int)Math.sqrt(a*a+b*b)});
        */
        //给定a的值，

        Stream<int[]> pythagoreanTriples =
                IntStream.rangeClosed(1, 100).boxed()
                        .flatMap(a ->
                                IntStream.rangeClosed(a, 100)
                                        .filter(b -> Math.sqrt(a * a + b * b) % 1 == 0)
                                        .mapToObj(b -> new int[]{a, b, (int) Math.sqrt(a * a + b * b)})

                        );

        pythagoreanTriples.limit(5).forEach(t -> System.out.println(t[0] + "," + t[1] + "," + t[2]));

        //代码更为紧凑的是，先生成，再过滤
        Stream<int[]> pythagoreanTriples2 =
                IntStream.rangeClosed(1, 100).boxed()
                        .flatMap(a ->
                                IntStream.rangeClosed(a, 100)
                                        .mapToObj(b -> new int[]{a, b, (int) Math.sqrt(a * a + b * b)})
                                        .filter(t -> t[2] % 1 == 0)

                        );


    }
}
