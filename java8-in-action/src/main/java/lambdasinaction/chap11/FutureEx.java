/*
package lambdasinaction.chap11;

import java.util.concurrent.*;

*/
/**
 * Created by SML on 2018/2/24.
 *
 * @author SML
 *//*

public class FutureEx {
    public static void main(String[] args) {

        // 异步执行，Future比更底层的Thread易用
        ExecutorService executorService= Executors.newCachedThreadPool();
        Future<Double> future= executorService.submit(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                //以异步方式在新的线程中执行耗时操作
                return doSomeThingLong();
            }
        });
        //继续做其他事
        doSomethingElse();

        // 去拿执行结果
        try{
            //获取异步操作的结果，如果最终被阻塞，无法得到结果，那么最多等待1s后退出
            Double result= future.get(1, TimeUnit.SECONDS);
        }catch (ExecutionException e){
            // 计算抛出一个异常
        }catch (InterruptedException e){
            // 当前线程在待过程中被中断
        } catch (TimeoutException e){
            // 在Future对象完成之前 已过期
        }

    }
}
*/
