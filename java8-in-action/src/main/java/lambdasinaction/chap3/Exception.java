package lambdasinaction.chap3;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.Function;

/**
 * Created by SML on 2018/1/26.
 *
 * @author SML
 */
public class Exception {

    /**
     * 任何函数接口都不允许抛出checked Exception,如果需要，
     * 方法1：定义一个函数式接口，并声明checked Exception.
     * 方法2： 把lambda包含在一个try/catch中
     */


    public static void main(String[] args) {


        //方法2，如果调用的已有的函数式接口,那就自己catch
        Function<BufferedReader,String> f=(BufferedReader b)->{
            try{
                return b.readLine();
            }catch (IOException e){
                throw  new RuntimeException(e);
            }
        } ;
    }


    /**方法1：定义一个函数式接口，并声明checked Exception.
     *
     */
    public interface BufferedReaderProcessor{
        public String process(BufferedReader b) throws IOException;

    }
}
