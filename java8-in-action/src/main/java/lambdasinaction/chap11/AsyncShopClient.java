package lambdasinaction.chap11;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;

public class AsyncShopClient {

    public static void main(String[] args) {
        AsyncShop shop = new AsyncShop("BestShop");
        Future<Double> futurePrice = shop.getPrice("myPhone");
        try {
            System.out.println("Price is " + futurePrice.get());  // 读取价格，若位置，则阻塞
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}