CH4- 引入流


StreamAPI流允许以声明性方式处理数据集合(通过查询语句，而不是临时的一个实现)，

####StreamAPI
支持并行操作，解决多线程使用不方便的问题，比如，避免synchronized

1.没有共享的可变数据:for并行，java.util.stream
代码必须同时对不同个输入安全地执行，也就意味着，代码不可以访问共享的可变数据---这些函数被称为纯函数（无副作用函数，无状态函数）
2.将方法和函数传递给其他方法：

上面是函数式编程范式的基石，对比之下，命令式范式中，程序则是一系列改变状态的指令
java中的并行和无共享状态: 数据在执行时无互动
```$xslt  
List<Apple> heavyApples2_ = inventory.stream().filter((Apple a) -> a.getWeight() > 150).collect(toList());                             
List<Apple> heavyApples3_ = inventory.parallelStream().filter((Apple a) -> a.getWeight() > 150).collect(toList());
List<T>并没有 stream等方法，使用了接口的默认方法来实现，
``` 
java 8,实现的细节被放到本该归属的库里
- 代码声明式方式：说明想要完成什么，而不是如何实现
- 基础操作链接起来，复合
- 可并行


```$xslt
public static List<String> getLowCaloricDishesNamesInJava8(List<Dish> dishes){
     return dishes.stream()
             .filter(d -> d.getCalories() < 400)
             .sorted(comparing(Dish::getCalories))
             .map(Dish::getName)
             .collect(toList());
}

```
          
## 流简介：
- java8中的集合都有一个新的stream方法，返回一个流。
- Collection主要用于存储和访问数据，Stream则主要用于描述对数据的计算。

流操作有两个特点：

1.流水线,
2.内部迭代，背后进行

(1)上面的getLowCaloricDishesNamesInJava8中除了collect操作外,其他操作都会返回一个流:链中的方法都在排队等待,直到调用collect

(2)集合和流之间的差异就是什么时候计算
(3)不管什么时候，集合里的元素都得放在内存中，元素都得先算出来才可以成为集合的一部分
(4)流则是概念上固定的数据结构（不能添加和删除元素），其元素都是按需计算的，像一种延迟创建的集合，生产者-消费者的关系
(5)流只能遍历一次：遍历完后，说这个流被消费掉了

```$xslt
List<String> names = Arrays.asList("Java8", "Lambdas", "In", "Action");
Stream<String> s = names.stream();
s.forEach(System.out::println);
// uncommenting this line will result in an IllegalStateException
// because streams can be consumed only once
s.forEach(System.out::println);

```   
## CH4 流操作:
- 中间操作：filter,map,limit,sorted,distinct 结果是流
- 终端操作：collect,结果是任何不是流的值,如 List，Integer(count)，void(forEach) 
   menu.stream().forEach(System.out::println)

    