
CH5- 使用流
##CH5 使用流：

1.筛选和切片
   - filter(一个返回boolean的函数)
   - distinct() ：根据hashCode和equals方法实现
   - limit(n) :返回一个不超过指定长度的流
   - skip(n): 表示跳过前n个元素，如果不去n个，则返回一个空流，和limit(n)互补
   
2.映射
- map：和转换类似，区别在于是创建一个新版本而不是修改.
- flatMap: 流的扁平化，把流中的每个值都转换成另外一个流，然后把所有的流连接起来成为一个流
   
```
    List<String> words1 = Arrays.asList("Hello", "World");
     // Arrays.stream ,接受一个数组，产生一个流
    words1.stream()
                .flatMap((String line) -> Arrays.stream(line.split("")))
                .distinct()
                .forEach(System.out::println);  //列出各不相同的字符
    
    //只返回和能被3整除的数对
          List<Integer> numbers11 = Arrays.asList(1, 2, 3, 4, 5);
          List<Integer> numbers22 = Arrays.asList(6, 7, 8);
          List<int[]> pairs1 =
                  numbers11.stream()
                          .flatMap(i -> numbers22.stream()
                                  .filter(j -> (i + j) % 3 == 0)
                                  .map(j -> new int[]{i, j})
                          )
                          .collect(toList());  //Stream<Integer[]>
```  
3.查找和匹配
 stream的api
- 匹配：

  boolean anyMatch(Predicate<? super T> predicate):至少匹配一个
  
  boolean allMatch(Predicate<? super T> predicate):
  
  boolean nonMatch(Predicate<? super T> predicate):
 
 三者均是短路操作
 
- 查找：

  Optional<T> findAny();返回当前流的任意元素，可并行

  Optional<T> findFirst(); 不可并行
```
   /**
       * java.util.Optional
       * Optional<T> 是一个容器类，代表一个值存在或者不存在，在上面的代码中，findAny可能什么都没找到，引入这个就不用返回null了
       * 几个方法：
       * ifPresent(),包含值的时候返回true，否则false
       * ifPresent(Consumer<T> block)会在值存在的时候执行给定的代码块
       * T get(),存在即返回值 ，否则抛出 NoSuchElement的异常
       * T orElse(T other),会在值存在时返回值，否则返回一个默认值
       *
       */
     menu.stream().filter(Dish::isVegetarian)
                  .findAny()
                  .ifPresent( d->System.out.print(d));
   
 ``` 
4.规约

  将数据收集起来，执行更复杂的查询
  
- T reduce(T identity, BinaryOperator<T> accumulator) 
  
   a: 初始值 0
   
   b: 一个BinaryOperator，执行具体的操作
   
   reduce返回结合每个元素，直到流被规约成为一个值
   
``` 
List<Integer> numbers = Arrays.asList(3,4,5,1,2);
int sum = numbers.stream().reduce(0, (a, b) -> a + b);
```  
- Optional<T> reduce(BinaryOperator<T> accumulator);
  
  Optional<Integer> sum2=numbers.stream().reduce((a,b)->a+b);
  long  count=menu.stream().count();
  
  最大值:  Optional<Integer> max=numbers.stream().reduce(Integer::max);
  
  规约并行化: int sum = numbers.parallelStream().reduce(0, Integer::sum);
    
#### 流操作：无状态和有状态
- 无状态：filter,map
- 有状态：reduce,sum,max等需要内部状态来累积结果
 
#### 数值流

```
 int calories = menu.stream()
                    .map(Dish::getCalories)
                    .reduce(0,Integer::sum);
  //包含装箱成本，每个Integer都必须拆箱成一个原始类型，再进行求和
  如果可以
  int calories2 = menu.stream()
                  .map(Dish::getCalories)
                  .sum();   // not support
                  
  //map()生成一个Stream<T>,但是Streams没有定义sum方法
```
  
##### 原始类型流特化：
  java8引入三个原始类型流接口来解决这个问题：IntStream，DoubleStream，LongStream,分别将流中的元素特化为int，long，double,从而避免装箱，必要时哈可以转成成对象流。
  特化的原因主要是装箱造成的效率差异
  
1.mapToInt,mapToDouble,mapToLong              
```  
  int calories2 = menu.stream()
                   .mapToInt(Dish::getCalories) //IntStream
                   .sum();   //average,min,
```
2.back to 对象流
```
   IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
   Stream<Integer> stream=intStream.boxed();
```  
3.默认值 OptionalInt， OptionalDouble， OptionalLong
   
   如何区分没有元素的流和最大值真的是0的流呢
```   
   OptionalInt max=menu.stream().mapToInt(Dish::getCalories).max();   
   int max2=max.orElse(1); //显示处理
```
#### 数值范围：
java8引入了静态方法rangeClosed 和range 来生成范围数字,range不包含结束值
   IntStream evenNumbers = IntStream.rangeClosed(1, 100).filter(n -> n % 2 == 0); 

练习 勾股数：
```
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
```

#### 构建流：

1.由数值来创建流
```
 Stream<String> stream = Stream.of("Java 8", "Lambdas", "In", "Action");
 stream.map(String::toUpperCase).forEach(System.out::println);
 //2. 空流
 Stream<String> emptyStream = Stream.empty();
```
2.数据创建流 Arrays.stream
```
int[] numbers = {2, 3, 5, 7, 11, 13};
System.out.println(Arrays.stream(numbers).sum());

```       

3.文件生成流
``` 
 /**
         * java.nio.file.Files 中很多静态方法都会返回一个流
         * Files.lines得到一个流,每个遗憾苏是是给定文件的一行
         */
        long uniqueWords = Files.lines(Paths.get("lambdasinaction/chap5/data.txt"), Charset.defaultCharset())
                                 .flatMap(line -> Arrays.stream(line.split(" "))) //将各个行拆分再合并为一个流
                                 .distinct()
                                 .count();
``` 
4. 函数生成流，创建无线流

 Stream.iterate和generate,且可以创建无限流
 
 iterate(final T seed, final UnaryOperator<T> f)： 依次生成一系列值的时候
 generate(Supplier<T> s)
 
```
Stream.iterate(0, n -> n + 2)
      .limit(10)
      .forEach(System.out::println);
```
```

// fibonnaci with iterate
Stream.iterate(new int[]{0, 1}, t -> new int[]{t[1],t[0] + t[1]})
      .limit(10)
      .forEach(t -> System.out.println("(" + t[0] + ", " + t[1] + ")"));        
 
// random stream of doubles with Stream.generate
Stream.generate(Math::random)
      .limit(10)
      .forEach(System.out::println);
  
// stream of 1s with Stream.generate
IntStream.generate(() -> 1)
         .limit(5)
         .forEach(System.out::println);
 
``` 
```
/**
 * 这是一个副作用的例子,改变状态，则不可以并行，
 * 匿名类可以通过字段定义状态，而状态又可以被修改
 *
*/
IntSupplier fib = new IntSupplier(){
                  private int previous = 0;
                  private int current = 1;
                  public int getAsInt(){
                      int nextValue = this.previous + this.current;
                      this.previous = this.current;
                      this.current = nextValue;
                      return this.previous;
                  }
              };
IntStream.generate(fib).limit(10).forEach(System.out::println);
 
public interface Supplier<T> {
         T get();
}  
```  
  