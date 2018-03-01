package lambdasinaction.chap11;

import static lambdasinaction.chap11.Util.delay;
import static lambdasinaction.chap11.Util.format;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class AsyncShop {

    private final String name;
    private final Random random;

    public AsyncShop(String name) {
        this.name = name;
        random = new Random(name.charAt(0) * name.charAt(1) * name.charAt(2));
    }

    public Future<Double> getPrice(String product) {

/*
        CompletableFuture<Double> futurePrice = new CompletableFuture<>(); // 创建CompletableFuture对象,会包含计算的结果
        new Thread( () -> {
                    try {

                        double price = calculatePrice(product);
                        futurePrice.complete(price);  //需长时间计算的任务结束并得出结果时，设置Future的返回值

                    } catch (Exception ex) {
                        futurePrice.completeExceptionally(ex);
                    }
        }).start();

        return futurePrice;
*/
        return CompletableFuture.supplyAsync(() -> calculatePrice(product));

    }

    private double calculatePrice(String product) {
        delay();
        if (true) throw new RuntimeException("product not available");
        return format(random.nextDouble() * product.charAt(0) + product.charAt(1));
    }

}