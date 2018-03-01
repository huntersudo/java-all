package lambdasinaction.chap8;

import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;

/**
 * Created by SML on 2018/2/26.
 *
 * @author SML
 */
public class Runn {

    public static void main(String[] args) {

        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("HELLO");
            }
        };

        Runnable r2 = () -> System.out.println("HELLO");


    }
}
