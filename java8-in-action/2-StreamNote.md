1.StreamAPI
流 允许以声明性方式处理数据集合(通过查询语句，而不是临时的一个实现)，

1.1 StreamAPI ,支持并行操作，解决多线程使用不方便的问题，比如，避免synchronized
   java.util.stream 
  
   （1）没有共享的可变数据: for 并行，代码必须同时对不同个输入安全地执行，也就意味着，代码不可以访问共享的可变数据---这些函数被称为纯函数（无副作用函数，无状态函数）
   （2）将方法和函数传递给其他方法：
     上面是函数式编程范式的基石，对比之下，命令式范式中，程序则是一系列改变状态的指令
  
   java中的并行和无共享状态: 数据在执行时无互动
    
   List<Apple> heavyApples2_ = inventory.stream().filter((Apple a) -> a.getWeight() > 150).collect(toList());                             
   List<Apple> heavyApples3_ = inventory.parallelStream().filter((Apple a) -> a.getWeight() > 150).collect(toList());
 
   List<T>并没有 stream等方法，使用了接口的默认方法来实现，
   
    /**
        * java 8,实现的细节被放到本该归属的库里
        * (1)代码声明式方式：说明想要完成什么，而不是如何实现
        * (2)基础操作链接起来，复合
        * (3)可并行
        * @param dishes
        * @return
        */
       public static List<String> getLowCaloricDishesNamesInJava8(List<Dish> dishes){
           return dishes.stream()
                   .filter(d -> d.getCalories() < 400)
                   .sorted(comparing(Dish::getCalories))
                   .map(Dish::getName)
                   .collect(toList());
       }
       
流简介：
java8中的集合都有一个新的stream方法，返回一个流。
  Collection主要用于存储和访问数据，Stream则主要用于描述对数据的计算。
流操作有两个特点：
1.流水线,
2.内部迭代，背后进行
上面的getLowCaloricDishesNamesInJava8 中除了collect操作外，其他操作都会返回一个流： 链中的方法都在排队等待，直到调用collect

集合和流之间的差异就是什么时候计算
  不管什么时候，集合里的元素都得放在内存中，元素都得先算出来才可以成为集合的一部分
  流则是概念上固定的数据结构（不能添加和删除元素），其元素都是按需计算的，像一种延迟创建的集合，生产者-消费者的关系
  
流只能遍历一次：
 遍历完后，说这个流被消费掉了。
         List<String> names = Arrays.asList("Java8", "Lambdas", "In", "Action");
         Stream<String> s = names.stream();
         s.forEach(System.out::println);
         // uncommenting this line will result in an IllegalStateException
         // because streams can be consumed only once
         s.forEach(System.out::println);
 
流操作:
 中间操作：filter,map,limit,sorted,distinct 结果是流
 
 终端操作：collect,结果是任何不是流的值，如 List，Integer(count)，void(forEach) 
   menu.stream().forEach(System.out::println)

使用流：

（1）筛选和切片
   filter(一个返回boolean的函数)
   distinct() ：根据hashCode和equals方法实现
   limit(n) :返回一个不超过指定长度的流
   skip(n): 表示跳过前n个元素，如果不去n个，则返回一个空流，和limit(n)互补。   
（2）映射
  map：和转换类似，区别在于是创建一个新版本而不是修改
  flatMap: 流的扁平化，把流中的每个值都转换成另外一个流，然后把所有的流连接起来成为一个流。
   
    //
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
  
 (3)查找和匹配
 stream的api
 匹配：
  boolean anyMatch(Predicate<? super T> predicate):至少匹配一个
  boolean allMatch(Predicate<? super T> predicate):
  boolean nonMatch(Predicate<? super T> predicate):
 三者均是短路操作
 
 查找：
  Optional<T> findAny();返回当前流的任意元素，可并行
  Optional<T> findFirst(); 不可并行
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
   
  
 (4)规约
  将数据收集起来，执行更复杂的查询
  T reduce(T identity, BinaryOperator<T> accumulator);
   a:初始值 0
   b:一个BinaryOperator，执行具体的操作
  
  reduce返回结合每个元素，直到流被规约成为一个值
  
  示例;  List<Integer> numbers = Arrays.asList(3,4,5,1,2);
        int sum = numbers.stream().reduce(0, (a, b) -> a + b);
 
  Optional<T> reduce(BinaryOperator<T> accumulator);
   变体， Optional<Integer> sum2=numbers.stream().reduce((a,b)->a+b);
  
  long  count=menu.stream().count();
  
  最大值
   Optional<Integer> max=numbers.stream().reduce(Integer::max);
   
  规约并行化:
    int sum = numbers.parallelStream().reduce(0, Integer::sum);
    
 流操作：无状态和有状态
   无状态：filter,map
   有状态：reduce,sum,max等需要内部状态来累积结果
 
(6) 数值流

 int calories = menu.stream()
                    .map(Dish::getCalories)
                    .reduce(0,Integer::sum);
  //包含装箱成本，每个Integer都必须拆箱成一个原始类型，再进行求和
  如果可以
  int calories2 = menu.stream()
                  .map(Dish::getCalories)
                  .sum();   // not support
                  
  //map()生成一个Stream<T>,但是Streams没有定义sum方法
  
  原始类型流特化：
  java8 引入三个原始类型流接口来解决这个问题：IntStream，DoubleStream，LongStream,分别将流中的元素特化为int，long，double,从而避免装箱，必要时哈可以转成成对象流。
  特化的原因主要是装箱造成的效率差异
  
  1) mapToInt,mapToDouble,mapToLong              
  
  int calories2 = menu.stream()
                   .mapToInt(Dish::getCalories) //IntStream
                   .sum();   //average,min,
  2) back to 对象流
   IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
   Stream<Integer> stream=intStream.boxed();
   
  3) 默认值 OptionalInt， OptionalDouble， OptionalLong
   如何区分没有元素的流和最大值真的是0的流呢
   OptionalInt max=menu.stream().mapToInt(Dish::getCalories).max();   
   int max2=max.orElse(1); //显示处理
   
  数值范围：
   java8引入了静态方法rangeClosed 和range 来生成范围数字
    range不包含结束值
   
       IntStream evenNumbers = IntStream.rangeClosed(1, 100).filter(n -> n % 2 == 0); 
  勾股数：
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

构建流：

 (1)由数值来创建流
        Stream<String> stream = Stream.of("Java 8", "Lambdas", "In", "Action");
        stream.map(String::toUpperCase).forEach(System.out::println);
        //2. 空流
        Stream<String> emptyStream = Stream.empty();
(2)数据创建流 Arrays.stream
        int[] numbers = {2, 3, 5, 7, 11, 13};
        System.out.println(Arrays.stream(numbers).sum());

（3）文件生成流
 /**
         * java.nio.file.Files 中很多静态方法都会返回一个流
         * Files.lines得到一个流,每个遗憾苏是是给定文件的一行
         */
        long uniqueWords = Files.lines(Paths.get("lambdasinaction/chap5/data.txt"), Charset.defaultCharset())
                                 .flatMap(line -> Arrays.stream(line.split(" "))) //将各个行拆分再合并为一个流
                                 .distinct()
                                 .count();

(4)函数生成流，创建无线流
 Stream.iterate和generate,且可以创建无限流
 * iterate(final T seed, final UnaryOperator<T> f)： 依次生成一系列值的时候
 * generate(Supplier<T> s)
 
   Stream.iterate(0, n -> n + 2)
               .limit(10)
               .forEach(System.out::println);
 
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
  
  
6.流收集数据

把java8的流看数据迭代器
区分Collection,Collector,collector 

 Map<Currency,List<Transaction>> transactionByCurrencies=transactions.stream().collect(groupingBy(Transaction::getCurrency));
 如果用java7，需要一个fro循环，一个map等，如果是多级分组，则指令式和函数式之间的区别会更明显。
 
收集器用作高级归约，预定义收集器：将流元素规约和汇总为一个值，元素分组，元素分区
 
规约和汇总: 
需要将流项目重组成集合时，一般会使用收集器collect()
  long dishes = menu.stream().collect(Collectors.counting())
     or
  long dishes = menu.stream().count()
  
  Comparator<Dish> dishCaloriesComparator = Comparator.comparingInt(Dish::getCalories);
  BinaryOperator<Dish> moreCaloricOf = BinaryOperator.maxBy(dishCaloriesComparator);
  Optional<Dish> mcd= menu.stream().collect(reducing(moreCaloricOf));
  
  <==>menu.stream().collect(reducing((d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2)).get()
  

//summingInt 接受一个把对象因设为求和所需int的函数，类似的summingDouble,averagingInt,averagingDouble


//@ since1.8 
 public class IntSummaryStatistics implements IntConsumer {  // 类似DoubleSummaryStatistics,LongSummaryStatistics
      private long count; 
      private long sum;
      private int min = Integer.MAX_VALUE;
      private int max = Integer.MIN_VALUE;
  }
//一次操作得到4个指标
IntSummaryStatistics menuStatistics=menu.stream().collect(summarizingInt(Dish::getCalories));

连接字符串: joining() ,对流中每个对象应用toString方法得到的所有字符串连接成一个字符串,

  String shortMenu= menu.stream().map(Dish::getName).collect(joining());
  String shortMenu= menu.stream().map(Dish::getName).collect(joining(","));
  
广义的规约情况:Collectors.reducing()
  以上讨论的收集器，都是一个可以用reducing()工厂方法定义的规约过程的特殊情况而已。Collectors.reducing()一般化方法
  int totalCalories = menu.stream().collect(reducing(0, Dish::getCalories, (i,j) -> i + j));
  //para1:起始值
  //para2:对象转换成要计算的项目，函数
  //para3:将多个项目累计额成一个同类型的值
  
   Optional<Dish> mcd=menu.stream().collect(reducing((d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2));
   //reducing(函数),返回Optional

    public static <T, U>  Collector<T, ?, U> reducing(U identity,Function<? super T, ? extends U> mapper,BinaryOperator<U> op) 

Note:收集和归约
  Stream的collect和reduce方法，通常会得到相同的结果;
  
    Stream<Integer> stream = Arrays.asList(1,2,3,4).stream();
          List<Integer> numbers =stream.reduce(
                  new ArrayList<Integer>(),
                  (List<Integer> list,Integer e)->{list.add(e);return list;},
                  (List<Integer> list1,List<Integer> list2)->{list1.addAll(list2);return list1;}
          );
          
  这个解决方法有2个问题：语义问题和实际问题，reducing指在把两个值结合起来生成一个新值，是一个不可变的归约; collect方法的设计就是要改变容器，从而累积要输出的结果;
  所以，上面的方法在滥用reducing，原地改变list，---错误的用法，导致不能并行工作，
  collect方法特别适合表达可变容器上的规约的原因
  
  1.收集框架的灵活性，以不同的方法执行同样的操作。
   int total=menu.stream().collect(reducing(0, Dish::getCalories, Integer::sum));
   归约操作：利用累积函数sum,
    
   //将dish映射为每个dish的热量，利用Stream.reduce(Function)来操作      
   int total= menu.stream().map(Dish::getCalories).reduce(Integer::sum).get()
   //或者映射为一个IntStream
    int total= menu.stream().mapToInt(Dish::getCalories).sum();
    
   
    /**
            * 实际上counting也是利用reducing实现的
            public static <T> Collector<T, ?, Long>
            counting() {
            return reducing(0L, e -> 1L, Long::sum);
            }
            */
  2. 根据情况选择最佳解决方案
  FP通常提供多种方法来执行同一个操作，上述例子说明，收集器在某种程度上比Stream接口上直接提供的方法更为复杂，好处是可以提供更高水平的抽象和概率，更容易重用和定义。
    
  reducing连接字符串：
   String shortMenu= menu.stream().map(Dish::getName).collect(joining());
   String shortMenu= menu.stream().map(Dish::getName).collect(reducing((s1,s2)->s1+s2)).get();
   String shortMenu= menu.stream().collect(reducing("",Dish::getName,(s1,s2)->s1+s2));
   
   
6.3 分组groupingBy

根据一个属性或者多个属性对集合中的项目进行分组,   
                                          
 Map<Dish.Type, List<Dish>>  dishesByType = menu.stream().collect(groupingBy(Dish::getType));    
 这个函数叫做分类函数
 
  Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream().collect(
                 groupingBy(dish -> {
                     if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                     else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                     else return CaloricLevel.FAT;
                 } ));                               
  // lambda 
  
  多级分组
  //双参数的Collectors.groupingBy()
  Map<Dish.Type, Map<CaloricLevel, List<Dish>>> lll
                   =menu.stream().collect(
                                 groupingBy(Dish::getType,
                                           groupingBy((Dish dish) -> {
                                                              if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                                                              else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                                                              else return CaloricLevel.FAT;
                                                      } )
                                           )
                                      );
   //内层传递给外层

 按子组收集数据
   传递给第一个groupingBy的第二个收集器可以是任何类型
   //数一数每类菜单有多少个
  Map<Dish.Type, Long> count =  menu.stream().collect(groupingBy(Dish::getType, counting()));
  //按照类型分类,查找热量最高
  Map<Dish.Type, Optional<Dish>> ll=  menu.stream().collect(groupingBy(Dish::getType,
                          maxBy(comparingInt(Dish::getCalories))));
  //Optional 这里没用                 
  
  1.把收集的结果转换为另外一种类型
  Collectors.collectingAndThan(收集器,转换函数)
   
   Map<Dish.Type, Dish> ll=  menu.stream()
                          .collect(groupingBy(Dish::getType,
                            collectingAndThan(   //包装后的收集器
                              maxBy(comparingInt(Dish::getCalories)),Optional::get
                             ));
  
2.与groupingBy联和使用的其他收集器

//每一组dish求和
  Map<Dish.Type, Integer> totalCaloriesByType = menu.stream()
                            .collect(groupingBy(Dish::getType,summingInt(Dish::getCalories)));
//mapping(变换,收集)
//每种类型的Dish 菜单中有哪些 CaloricLevel
Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType = menu.stream().collect(
                groupingBy(Dish::getType, 
                    mapping(
                        dish -> { if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                        else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                        else return CaloricLevel.FAT; },    toSet() 
                     )));                            

// toSet()  -->> toCollection(HashSet::new)


6.4 分区 partitioningBy

分区是分组的特殊情况:由一个谓词(boolean)作为分类函数,意味着得到的分组Map键的类型是Boolean,最终被分为两组.

Map<Boolean, List<Dish>>  partMenu = menu.stream().collect(partitioningBy(Dish::isVegetarian)); 
List<Dish> vegList = partMenu.get(true);

List<Dish> vegList = menu.stream().filter(Dish::isVegetarian).collect(toList());

 分区的优势:
  保留了分区函数返回true和false的两套流元素列表, 
  
  
  P127 
   



Map<Boolean, Map<Dish.Type, List<Dish>>> ll = menu.stream().collect(partitioningBy(Dish::isVegetarian, groupingBy(Dish::getType)));








  