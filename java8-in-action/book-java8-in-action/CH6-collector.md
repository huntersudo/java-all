
CH6- 用流收集数据
  
6.流收集数据

把java8的流看数据迭代器, 区分Collection,Collector,collector 

Map<Currency,List<Transaction>> transactionByCurrencies = transactions.stream().collect(groupingBy(Transaction::getCurrency));
 
如果用java7，需要一个fro循环，一个map等，如果是多级分组，则指令式和函数式之间的区别会更明显。
 
收集器用作高级归约，预定义收集器：将流元素规约和汇总为一个值，元素分组，元素分区
 
规约和汇总: 

需要将流项目重组成集合时，一般会使用收集器collect()

```
long dishes = menu.stream().collect(Collectors.counting())
     or
long dishes = menu.stream().count()
```
```
  Comparator<Dish> dishCaloriesComparator = Comparator.comparingInt(Dish::getCalories);
  BinaryOperator<Dish> moreCaloricOf = BinaryOperator.maxBy(dishCaloriesComparator);
  Optional<Dish> mcd= menu.stream().collect(reducing(moreCaloricOf));
  
menu.stream().collect(reducing((d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2)).get()

```

//summingInt 接受一个把对象因设为求和所需int的函数，类似的summingDouble,averagingInt,averagingDouble
```
//@ since1.8 
 public class IntSummaryStatistics implements IntConsumer {  // 类似DoubleSummaryStatistics,LongSummaryStatistics
      private long count; 
      private long sum;
      private int min = Integer.MAX_VALUE;
      private int max = Integer.MIN_VALUE;
  }
//一次操作得到4个指标
IntSummaryStatistics menuStatistics=menu.stream().collect(summarizingInt(Dish::getCalories));
```

连接字符串: joining() ,对流中每个对象应用toString方法得到的所有字符串连接成一个字符串,

```
  String shortMenu= menu.stream().map(Dish::getName).collect(joining());
  String shortMenu= menu.stream().map(Dish::getName).collect(joining(","));
``` 
 
广义的规约情况:Collectors.reducing()

以上讨论的收集器，都是一个可以用reducing()工厂方法定义的规约过程的特殊情况而已。Collectors.reducing()一般化方法

int totalCalories = menu.stream().collect(reducing(0, Dish::getCalories, (i,j) -> i + j));
//para1:起始值
//para2:对象转换成要计算的项目，函数
//para3:将多个项目累计额成一个同类型的值
  
Optional<Dish> mcd=menu.stream().collect(reducing((d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2));
//reducing(函数),返回Optional

```
public static <T, U>  Collector<T, ?, U> reducing(U identity,Function<? super T, ? extends U> mapper,BinaryOperator<U> op) 

```

Note:收集和归约

Stream的collect和reduce方法，通常会得到相同的结果;
```
Stream<Integer> stream = Arrays.asList(1,2,3,4).stream();
List<Integer> numbers =stream.reduce(
                  new ArrayList<Integer>(),
                  (List<Integer> list,Integer e)->{list.add(e);return list;},
                  (List<Integer> list1,List<Integer> list2)->{list1.addAll(list2);return list1;}
          );
```          

这个解决方法有2个问题：语义问题和实际问题，reducing指在把两个值结合起来生成一个新值，是一个不可变的归约; collect方法的设计就是要改变容器，从而累积要输出的结果;

所以，上面的方法在滥用reducing，原地改变list，---错误的用法，导致不能并行工作，

collect方法特别适合表达可变容器上的规约的原因
  
1.收集框架的灵活性，以不同的方法执行同样的操作。
 int total=menu.stream().collect(reducing(0, Dish::getCalories, Integer::sum));
 归约操作：利用累积函数sum,

```
 //将dish映射为每个dish的热量，利用Stream.reduce(Function)来操作      
 int total= menu.stream().map(Dish::getCalories).reduce(Integer::sum).get()
 
 //或者映射为一个IntStream 
 int total= menu.stream().mapToInt(Dish::getCalories).sum();
```
   
2. 根据情况选择最佳解决方案

FP通常提供多种方法来执行同一个操作，上述例子说明，收集器在某种程度上比Stream接口上直接提供的方法更为复杂，好处是可以提供更高水平的抽象和概率，更容易重用和定义。
```    
reducing连接字符串：
   String shortMenu= menu.stream().map(Dish::getName).collect(joining());
   String shortMenu= menu.stream().map(Dish::getName).collect(reducing((s1,s2)->s1+s2)).get();
   String shortMenu= menu.stream().collect(reducing("",Dish::getName,(s1,s2)->s1+s2));
```  
   
6.3.1 分组groupingBy

根据一个属性或者多个属性对集合中的项目进行分组,   
                                          
 Map<Dish.Type, List<Dish>>  dishesByType = menu.stream().collect(groupingBy(Dish::getType));    
 
 这个函数叫做分类函数
 ```
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
  
 //Caloric levels by type: {MEAT=[DIET, NORMAL, FAT], FISH=[DIET, NORMAL], OTHER=[DIET, NORMAL]}

```

6.3.2 按子组收集数据
一般来说，可以把第二个groupingby传递给外层收集器来实现多级分组，进一步来说，传递给第一个groupingBy的第二个收集器可以是任何类型。

//数一数每类菜单有多少个，把counting收集器传递过去

```  
Map<Dish.Type, Long> count =  menu.stream().collect(groupingBy(Dish::getType, counting()));
```

//按照类型分类,查找热量最高,把maxBy收集器传递过去

```
Map<Dish.Type, Optional<Dish>> ll=  menu.stream().collect(groupingBy(Dish::getType,
                          maxBy(comparingInt(Dish::getCalories))));

//{MEAT=Optional[pork], FISH=Optional[salmon], OTHER=Optional[pizza]}    

```
          
1.把收集的结果转换为另外一种类型

Collectors.collectingAndThan(收集器,转换函数)

从外向内，分组操作得到的每个子流都用和这个第二个收集器做进一步归约

```    
Map<Dish.Type, Dish> ll=  menu.stream()
                              .collect(groupingBy(Dish::getType,
                               collectingAndThan(   //包装后的收集器
                                maxBy(comparingInt(Dish::getCalories)),Optional::get
                             )); 
//{MEAT=pork, FISH=salmon, OTHER=pizza}
```

2.与groupingBy联和使用的其他收集器

//每一组dish求和
```
  Map<Dish.Type, Integer> totalCaloriesByType = menu.stream()
                            .collect(groupingBy(Dish::getType,summingInt(Dish::getCalories)));
// 
```

//mapping(变换,收集)

//每种类型的Dish 菜单中有哪些 CaloricLevel

```
Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType = menu.stream().collect(
                groupingBy(Dish::getType, 
                    mapping(
                        dish -> { if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                        else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                        else return CaloricLevel.FAT; }, toSet() 
                     )));                            

// toSet()  -->> toCollection(HashSet::new)

```

#### 分区 partitioningBy

分区是分组的特殊情况:由一个谓词(boolean)作为分类函数,意味着得到的分组Map键的类型是Boolean,最终被分为两组.

```
Map<Boolean, List<Dish>>  partMenu = menu.stream().collect(partitioningBy(Dish::isVegetarian)); 
//{false=[pork, beef, chicken, prawns, salmon], true=[french fries, rice, season fruit, pizza]}

List<Dish> vegList = partMenu.get(true);

List<Dish> vegList = menu.stream().filter(Dish::isVegetarian).collect(toList());
```

6.4.1 分区的优势:

保留了分区函数返回true和false的两套流元素列表, 
   
```
Map<Boolean, Map<Dish.Type, List<Dish>>> ll = menu.stream().collect(partitioningBy(Dish::isVegetarian, groupingBy(Dish::getType)));

// {false={MEAT=[pork, beef, chicken], FISH=[prawns, salmon]}, true={OTHER=[french fries, rice, season fruit, pizza]}}

```

6.4.2 将数字按照质数和非质数分区

将前n个自然数划分

```
 public static boolean isPrime1(int candidate) {
        return IntStream.range(2, candidate)
                .noneMatch(i -> candidate % i == 0);
 }
```

优化:小于等于待测试数平方根

```
public static boolean isPrime(int candidate) {
        return IntStream.rangeClosed(2, candidate-1)
                .limit((long) Math.floor(Math.sqrt((double) candidate)) - 1)
                .noneMatch(i -> candidate % i == 0);
}
```
    

##6.5 收集器接口  
Collector 接口包含了一系列方法，为实现具体的规约操作提供了范本。

```
public class ToListCollector<T> implements Collector<T,A,R> {
  Supplier<A> supplier();      
  BiConsumer<A, T> accumulator();   
  Function<A,R> finisher(); 
  BinaryOperator<A> combiner();
  Set<Characteristics> characteristics(); 
}
```
(1) T是流中要收集的项目的泛型;  
(2) A是累加器的类型,累加器是在收集的过程中用于累积部分结果的对象;  
(3) R是收集操作得到的对象（通常但不到一定集合）的类型;  
实现ToListCollector，将Stream<T>中的所有元素都手机到一个List里  
```
public class ToListCollector<T> implements Collector<T, List<T>, List<T>> {

    @Override
    public Supplier<List<T>> supplier() { //1. 创建集合操作的起始点
        //建立新的结果容器，返回空的supplier，调用时返回一个空的累加器实例
//        return () -> new ArrayList<T>();
        return ArrayList::new;
    }

    @Override
    public BiConsumer<List<T>, T> accumulator() //原位修改累加器
    {
        //将元素添加到结果容器
//        return (list, item) -> list.add(item);
        return List::add;
    }

    @Override
    public Function<List<T>, List<T>> finisher() {  //恒等函数
        //对结果容器应用最终转换,以便将累加器对象转换为整个结合操作的最终结果
//        return i -> i;
        return Function.identity(); //无需转换

    }
    //上面的三个方法已支持顺序规约
    @Override
    public BinaryOperator<List<T>> combiner() {  //合并两个结果容器,定义了并行处理
        return (list1, list2) -> {
            list1.addAll(list2);
            return list1;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        // 返回一个不可变的Characteristics集合，定义了收集器的行为，包含三个项目的枚举
        // UNORDERED--规约结国不受流中项目的便利和累积顺序的影响;
        // CONCURRENT--如没有UNORDERED，则只有无序数据源时才可并行
        // IDENTITY_FINISH--表明完成器方法返回的函数是一个恒等函数，累加器可以不加检查地转换是可以的。
        return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH, CONCURRENT));
    }
}

```  
- 使用

List<Dish> dishes = menuStream.collect(new ToListCollector<Dish>());  
// 区别toList是一个工厂,自建的需要new
List<Dish> dishes = menuStream.collect(toList());

- 进行自定义收集而不去实现Collector  

对于IDENTITY_FINISH的收集操作,Stream有一个重载方法可以接受supplier,accumulator和combiner

```
List<Dish> dishes = menuStream.collect(
ArrayList::new,   //供应源
List::add,        //累加器
List:addAll       //组合器
);

```

##6.6 开发自己的收集器以获得更好的性能

划分质数的方法进行改进：

优化1：如果除数本身不是质数那就不用测了，假设已有其他质数的列表,

```
 public static boolean isPrime(List<Integer> primes, Integer candidate) {
        return primes.stream().takeWhile(i -> i <= candidateRoot).noneMatch(i -> candidate % i == 0);
}  
```

优化2：需要在质数大于被测试数的平方根时停下来,filter(p-> p<= candidateRoot)要处理整个流时才能返回正确的结果,
如果质数和非质数的列表都很大，这就不行了，实际上只需要在质数大于被测数平方根时候停下来就可以了,

```
//给定一个排序列表和一个谓词，会返回元素满足谓词的最长前缀，这个方法是即时的

public static <A> List<A> takeWhile(List<A> list, Predicate<A> p) {
        int i = 0;
        for (A item : list) {
            if (!p.test(item)) {
                return list.subList(0, i);
            }
            i++;
        }
        return list;
 }

```
改进后：
```
 public static boolean isPrime(List<Integer> primes, Integer candidate) {
        double candidateRoot = Math.sqrt((double) candidate);
        return takeWhile(primes, i -> i <= candidateRoot).stream().noneMatch(i -> candidate % i == 0);
 }
```

```
实现一个新的collector，或者使用collect的重载方法

public Map<Boolean, List<Integer>> partitionPrimesWithInlineCollector(int n) {
        return Stream.iterate(2, i -> i + 1).limit(n)
                .collect(
                        () -> new HashMap<Boolean, List<Integer>>() {{
                            put(true, new ArrayList<Integer>());
                            put(false, new ArrayList<Integer>());
                        }},
                        (acc, candidate) -> {
                            acc.get( isPrime(acc.get(true), candidate) )
                                    .add(candidate);
                        },
                        (map1, map2) -> {
                            map1.get(true).addAll(map2.get(true));
                            map1.get(false).addAll(map2.get(false));
                        });
    }
```






  