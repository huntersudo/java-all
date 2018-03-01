package lambdasinaction.chap11;

<<<<<<< HEAD
import java.util.concurrent.Future;
=======
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;
>>>>>>> develop

public class AsyncShopClient {

    public static void main(String[] args) {
        AsyncShop shop = new AsyncShop("BestShop");
<<<<<<< HEAD
        long start = System.nanoTime();
        Future<Double> futurePrice = shop.getPrice("myPhone");
        long incocationTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Invocation returned after " + incocationTime + " msecs");
        try {
            System.out.println("Price is " + futurePrice.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        long retrivalTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Price returned after " + retrivalTime + " msecs");
    }
=======
        Future<Double> futurePrice = shop.getPrice("myPhone");
        try {
            System.out.println("Price is " + futurePrice.get());  // 读取价格，若位置，则阻塞
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


>>>>>>> develop
}