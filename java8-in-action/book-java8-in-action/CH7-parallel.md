
CH7-并行数据处理与性能

前面 3 章 Stream 接口使得我们以声明的方式去处理数据集,流水线方式处理,
流是如何在幕后应用java7引入的分支/合并框架的。

java7之前：并行处理：
- 明确地处理包含数据的数据结构分成若干子部分；
- 每个子部分分配一个独立得线程；
- 恰当的时候对线程进行同步来避免不希望出现的竞争条件，等待所有线程完成，最后把这部分结果合并起来。

### 7.1并行流:

并行流就是一个把内容分成多个数据块，并用不同的线程分别处理每个数据块的流。

```
 
Stream.iterate(1L, i -> i + 1)
       .limit(n)
       .parallel() // 顺序--》并行
       .reduce(Long::sum)
       .get()
       
```

对顺序流调用parallel方法并不意味着流本身有任何实际变化
 
实际上，只要对并行流 调用 sequential方法就可以变成顺序流，但最后一次parallel 或者 sequential 方法的调用会影响整个流水线。


#### 配置并行流使用的线程池:
 内部实际就是ForkJoinPool,
 System.setProperty("java.util.current.ForkJoinPool.common.parallelism","12");
 // 默认是 Runtime.getRuntime().available-Processors()
 
 这是一个全局的
 

#### 三种(迭代、顺序、并行)测量性能

```
 @Benchmark
    public long iterativeSum() {  // 2 msecs 
        long result = 0;
        for (long i = 1L; i <= N; i++) {
            result += i;
        }
        return result;
    }
    
  @Benchmark
  public long sequentialSum() {   // 97 msecs
     return Stream.iterate( 1L, i -> i + 1 ).limit(N).reduce( 0L, Long::sum );
  }

  @Benchmark
  public long parallelSum() { // 92 msecs
      return Stream.iterate(1L, i -> i + 1).limit(N).parallel().reduce( 0L, Long::sum);
  }
  
// 并行反而比 顺序 慢
// 1. iterate生成的是装箱的对象，必须拆箱成数字才能求和；
// 2. 很难把iterate分成多个独立快来并行执行，因为每次应用这个iterate函数都依赖前一次应用的结果

  @Benchmark
  public long rangedSum() {  // 17 msecs
     return LongStream.rangeClosed( 1, N ).reduce( 0L, Long::sum );
  }
    
    
  @Benchmark
  public long rangedSum() {  // 1msesc 
            return LongStream.rangeClosed( 1, N ).reduce( 0L, Long::sum );
  }
    
```

best practices：
- 留意装箱,自动装箱和拆箱会大大降低操作性能，尽量使用java8的原始类型流
- 把不适合并行的给并行了，实际上会给顺序执行增加了开销，耗时反而更长
- 使用正确的数据结构然后使得其并行工作能够保证最佳的性能
- 并行，不要有数据共享,考虑流背后的数据结构是否易于分解 



### 7.2 ForkJoin

分支合并框架的目的是以递归方式将可以并行的任务拆分成更小的任务，然后将每个子任务的结果合并起来生成整体结果。
它是ExecutorService接口的一个实现，把子任务分配给线程池(称为ForkJoinPool)中的工作线程，

创建RecursiveTask<R> 的一个子类，其中R是并行化任务(以及所有子任务)产生的结果类型，
实现唯一的抽象方法compute，这个方法同时定义了将任务拆分成子任务的逻辑，以及无法再拆分或不方便再拆分时，生成单个子任务结果的逻辑。
```
if(任务足够小或不可分){
  顺序执行
}else {
  将任务分成两个子任务
  递归调用本方法，拆封成每个子任务，得带所有子任务完成
  合并每个子任务的结果
}

```
示例：Long[] 数组求和
```
public class ForkJoinSumCalculator extends RecursiveTask<Long> {

    public static final long THRESHOLD = 10_000;  // 不在将任务分解为子任务对的数组大小

    private final long[] numbers;  
    private final int start;  // 子任务处理的数组起始和终止位置
    private final int end;

    public ForkJoinSumCalculator(long[] numbers) {  //公共函数用于创建主任务
        this(numbers, 0, numbers.length);
    }

    private ForkJoinSumCalculator(long[] numbers, int start, int end) {  // 私有构造函数用于以递归方式为主任务创建子任务
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        int length = end - start;
        if (length <= THRESHOLD) {
            return computeSequentially();
        }
        
        // 新子任务为前一半求和 
        ForkJoinSumCalculator leftTask = new ForkJoinSumCalculator(numbers, start, start + length/2);
        leftTask.fork(); // 利用一个forkjoinpool线程异步执行新创建的子任务
        
        ForkJoinSumCalculator rightTask = new ForkJoinSumCalculator(numbers, start + length/2, end);
        Long rightResult = rightTask.compute();  // 同步执行第二个子任务,有可能进行进一步划分
        
        Long leftResult = leftTask.join();  // 读取第一个子任务的结果，如果未完成，则等待
   
        return leftResult + rightResult;   
    }

    private long computeSequentially() {
        long sum = 0;
        for (int i = start; i < end; i++) {
            sum += numbers[i];
        }
        return sum;
    }
}
```
使用:
```
 public static long forkJoinSum(long n) {
        long[] numbers = LongStream.rangeClosed(1, n).toArray();
        ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);
        return FORK_JOIN_POOL.invoke(task);
    }
    
  public static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool();  //  one is enough 
 
  System.out.println("ForkJoin sum done in: " + measurePerf(ForkJoinSumCalculator::forkJoinSum, 10_000_000L) + " msecs" );
      
  public static <T, R> long measurePerf(Function<T, R> f, T input) {
         long fastest = Long.MAX_VALUE;
         for (int i = 0; i < 10; i++) {
             long start = System.nanoTime();
             R result = f.apply(input);
             long duration = (System.nanoTime() - start) / 1_000_000;
             System.out.println("Result: " + result);
             if (duration < fastest) fastest = duration;
         }
         return fastest;
  }   
```
best practices:
- 调用join方法会阻塞调用方,fork是异步调用
- invoke方法不应该在RecursiveTask内部使用，只有顺序代码才应该用invoke来启动并行计算
- 性能会比并行流的版本差，毕竟要放到long[] 数组里
- 分支合并使用一种 工作窃取(work stealing)来解决各个线程负载不均的问题，从别的线程任务队列的尾巴上，偷走一个任务


### 7.3 Spliterator

自动机制拆分流.

Spliterator 是JAVA8新增的一个接口，“可分迭代器”，是为了并行而设计的.

JAVA8已经为集合框架中包含的所有数据结构提供了一个默认的Spliteraotr实现， 下面了解下原理

```

public interface Spliterator<T>{
  boolean tryAdvance(Consumer<? super T> action);
  Spliterator<T> trySplit();
  long estimateSize();
  int characteristics(); 
}
- tryAdvance 类似于普通的iterator,他会按顺序一个个使用Spliterator中的元素，如果还有其他元素，则返回true
- trySplit，划分元素给第二个Spliterator(由该方法返回),让它们两个并行处理，
- estimateSize 方法 估计还剩下多少个元素要遍历
- charcateristics 本身特性集的编码，类似收集器的特性

```

拆分过程 
将Stream拆分后成多个部分的算法是一个递归过程。

示例： wordcount
(1)普通
```
 public static final String SENTENCE =
            " Nel   mezzo del cammin  di nostra  vita " +
            "mi  ritrovai in una  selva oscura" +
            " che la  dritta via era   smarrita ";
            
public static int countWordsIteratively(String s) {  //逐个遍历String中的所有字符,
        int counter = 0;
        boolean lastSpace = true;
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(c)) {
                lastSpace = true;
            } else {
                if (lastSpace) counter++;   // 上一个字符是空格，当前字符不是空格，计数器+1 
                lastSpace = Character.isWhitespace(c);
            }
        }
        return counter;
}

```

(2)函数式风格重写  
String转换成一个流，原始类型流只有int,long,double, 所以只能用Stream<Character>
```
Stream<Character> stream = IntStream.range(0, s.length())
                                    .mapToObj(SENTENCE::charAt).parallel();
```
可以对这个流做规约来计算字数，规约流时由两个变量组成的状态:一个int 用来计算到目前为止数过的字数，还有一个boolean用来记得上一个遇到的Character是不是空格。      
因为java没有 tuple(用来表示由异类元素组成的有序列表的结构，不需要包装对象),创建一个类来封装。

```
 //遍历Character流时计数的类
    private static class WordCounter {
        private final int counter;
        private final boolean lastSpace;

        public WordCounter(int counter, boolean lastSpace) {
            this.counter = counter;
            this.lastSpace = lastSpace;
        }

        public WordCounter accumulate(Character c) {
            if (Character.isWhitespace(c)) {
                return lastSpace ? this : new WordCounter(counter, true);
            } else {
                return lastSpace ? new WordCounter(counter+1, false) : this;
            }
        }

        public WordCounter combine(WordCounter wordCounter) {  // 合并两个wordCounter，把计数器加起来
            return new WordCounter(counter + wordCounter.counter, wordCounter.lastSpace);
        }

        public int getCounter() {
            return counter;
        }
    }
```
使用:
```
private static int countWords(Stream<Character> stream) {
        WordCounter wordCounter = stream.reduce(new WordCounter(0, true),
                                                WordCounter::accumulate,
                                                WordCounter::combine);
        return wordCounter.getCounter();
    }

Stream<Character> stream = IntStream.range(0, s.length())
                                    .mapToObj(SENTENCE::charAt).parallel();
                                    
System.out.println("Found " + countWords(stream) + " words");   // 19                           

```

(3) 让wordcounter并行

  System.out.println("Found " + countWords(stream.parallel（）) + " words");  //25

因为原始的String在任意位置拆分, 所以有时一个词会被分为两个词，然后数了2次
解决：需要确保String 不是在任意位置拆分，而只能在词尾拆分，故需要为Character实现一个Spliterator

```
private static class WordCounterSpliterator implements Spliterator<Character> {

        private final String string;
        private int currentChar = 0;  // 初始化

        private WordCounterSpliterator(String string) {
            this.string = string;
        }

        @Override
        public boolean tryAdvance(Consumer<? super Character> action) {  // 处理当前字符
            action.accept(string.charAt(currentChar++));
            return currentChar < string.length();  // 如果还有字符要处理，返回true
        }

        @Override
        public Spliterator<Character> trySplit() {
            int currentSize = string.length() - currentChar;
            if (currentSize < 10) {
                return null;   // 返回null表示要处理的字符足够小，可以顺序处理
            }

            //设置试探拆分位置
            for (int splitPos = currentSize / 2 + currentChar; splitPos < string.length(); splitPos++) {

                //让拆分位置前进直到下一个空格,
                if (Character.isWhitespace(string.charAt(splitPos))) {
                    // 新 WordCounterSpliterator 来解析从开始  到 拆分位置 的部分
                    Spliterator<Character> spliterator = new WordCounterSpliterator(string.substring(currentChar, splitPos));
                    // 将当前的WordCounterSpliterator 的起始位置设置为拆分位置
                    currentChar = splitPos;
                    return spliterator;
                }

            }
            return null;
        }

        @Override
        public long estimateSize() {
            return string.length() - currentChar;
        }

        @Override
        public int characteristics() {
            return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
        }
    }

```

使用：
```
public static int countWords(String s) {
       
   Spliterator<Character> spliterator = new WordCounterSpliterator(s);
   Stream<Character> stream = StreamSupport.stream(spliterator, true);
   // StreamSupport.stream工厂方法创建并行流
   return countWords(stream);
}
```
Spliterator 控制拆分数据结构的策略  
Spliterator 还可以在第一次遍历、第一次拆分、或者第一次查询估计大小时绑定数据源，----利用此在同一个流上执行多个操作




