CH11 - CompleteableFuture类：异步计算
CH12 - Date&Time接口

## Future

示例：
```
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

```
Future局限性：
- 多个异步计算之间的协调，如何连续或者以一定顺序or条件连起来执行
- 异步计算完成后的通知机制，


## 实现异步API


1. CompletableFuture

示例：

```
 public Future<Double> getPrice(String product) {

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
}

```

使用：

```
 AsyncShop shop = new AsyncShop("BestShop");
 Future<Double> futurePrice = shop.getPrice("myPhone");
 try {
    System.out.println("Price is " + futurePrice.get());  // 读取价格，若位置，则阻塞
 } catch (Exception e) {
    throw new RuntimeException(e);
 }

```


2. supplyAsync 工厂方法创建 CompletableFuture

getPrice 可以用一句话代替
```
 public Future<Double> getPrice(String product) {
   return CompletableFuture.supplyAsync(() -> calculatePrice(product));
 }
```


## 并行查询

parallel

```

 public List<String> findPricesParallel(String product) {
        return shops.parallelStream()
                .map(shop -> shop.getName() + " price is " + shop.getPrice(product))
                .collect(Collectors.toList());
    }
```

CompletableFuture   
 - 得到的是 List<CompletableFuture<String>>,要求的是
 - 故 join方法类似于Future的get, 不像get,join不会抛出任务checked异常
 - 两个不同的stream流水线，因为如果都放在一个流水线，发向不同商家的请求只能以同步、顺序执行的方式，  
 因此，每个创建CompletableFuture对象只能在前一个操作结束之后，才能创建
 （流的延迟特性会引起顺序执行，以及如何避免）
 
 ![alt text](/img/yanchi1.png)  
 ![alt text](/img/yanchi2.png)
        
``` 
public List<String> findPricesFuture(String product) {

        List<CompletableFuture<String>> priceFutures =
                shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getName() + " price is "
                        + shop.getPrice(product), executor))
                .collect(Collectors.toList());
                       
        List<String> prices = priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
        return prices;
    }
```

定制线程池(执行器)  

``` 
 // 定制一个由守护线程构成的线程池
    // 守护线程，则程序退出时，也会被回收
    private final Executor executor = Executors.newFixedThreadPool(shops.size(), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true); //使用守护线程-这种方式不会阻止程序的关停
            return t;
        }
    });



```
#### 使用流 还是 CompletableFuture

- 计算密集型,没有IO,使用流的并行，因为创建多的线程没有多大意义

- 涉及IO，以及网络连接等待，使用CF,灵活性更好，需要定制线程数，不使用流的并行，  
  因为如果发生IO,流的延迟特性会让我们很难判断到底什么时候出发了等待
 
## 多个异步操作进行流水线服务
- thenApply方法不会阻塞

### 将有依赖的任务组合
- thenCompose方法,组合多个future，允许对两个异步操作进行流水线
- thenComposeAsync,通常而言，名称不带Async的和前一个任务一样，在同一个线程中执行，带Async的，回家那个后续的任务提交到一个线程池，所以每个任务是有不同的线程处理的
 如果第二个任务依赖第一个任务，则使用同步的即可
 

```  
public List<String> findPricesFuture(String product) {
        List<CompletableFuture<String>> priceFutures = findPricesStream(product)
                .collect(Collectors.<CompletableFuture<String>>toList());
                
       
        return priceFutures.stream()
                .map(CompletableFuture::join)  //等待所有异步操作结束
                .collect(Collectors.toList());
}


//Stream 底层依赖的是线程数量固定的通用线程池
public Stream<CompletableFuture<String>> findPricesStream(String product) {
        return shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product), executor)) //异步方式取得原始价格,定制化executor
                .map(future -> future.thenApply(Quote::parse))   // 转换返回的值
                //另外一个异步任务去申请折扣
                .map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), executor)));
}

```
### 将不相干的任务进行组合  

- thenCombine(CF,BIFunction)，其中BIFunction 定义了如何合并两个future  
- thenCombineAsync，合并操作提交到线程池，异步执行  

``` 
  CompletableFuture<String> futurePriceInUSD = 
                CompletableFuture.supplyAsync(() -> shop.getPrice(product))   // 第一个CF
                .thenCombine( 
                    CompletableFuture.supplyAsync(
                        () -> ExchangeService.getRate(Money.EUR, Money.USD)),   // 第二个CF
                    (price, rate) -> price * rate   // BIFunction 
                ).thenApply(price -> shop.getName() + " price is " + price);
```

## 响应 CF 的 completion 

#### JAVA 7合并两个Future
``` 
 ExecutorService executor = Executors.newCachedThreadPool();
 
 Future<Double> futureRate = executor.submit(new Callable<Double>() { 
                public Double call() {
                    return ExchangeService.getRate(Money.EUR, Money.USD);
                }
            });
            
  Future<Double> futurePriceInUSD = executor.submit(new Callable<Double>() { 
                public Double call() {
                    try {
                        double priceInEUR = shop.getPrice(product);
                        return priceInEUR * futureRate.get();
                        //在查找价格的的同一个Future中，将价格和汇率相乘返回
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
               });
            
```

缺失的一环： 需要将结果尽快展示给用户
使用get和join方法只会造成阻塞,直到CF 完成才能继续运行

#### 改进1：只要有查到价格，就在第一时间返回，不再等待哪些还未返回的商店

- thenAccept, 类似为CF注册回调函数，在Future执行完毕，或者计算结果可用时，针对性地执行一些操作
``` 

    public Stream<CompletableFuture<String>> findPricesStream(String product) {
        return shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product), executor)) 
                .map(future -> future.thenApply(Quote::parse))  
                .map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), executor)));
    }

    public void printPricesStream(String product) {
        long start = System.nanoTime();
        CompletableFuture[] futures = findPricesStream(product)
                .map(f -> f.thenAccept(s -> System.out.println(s)))   // java8 CF通过thenAccpet来响应completation

                .toArray(size -> new CompletableFuture[size]);  // 给慢的商店一些机会，等待所有任务完成

        CompletableFuture.allOf(futures).join();   // allOf

    }
```
- allOf 接收一个由CompletableFuture构成的数组，数组中的所有完成后，返回一个CF<void>对象，  
如果需要等待所有的CF执行完毕，对allOf返回的CF执行join操作。

- anyOf,任何一个返回了结果，接收一个CF数组，返回由第一个执行完毕的CF对象的返回值构成的CF<Obeject> 


