
CH10 - java.util.Optional
## CH10 - java.util.Optional

防御式地检查减少NullPointerException
过多的退出语句
  
null 带来的问题  
- 错误之源
- 代码膨胀：各种嵌套的null检查
- 自身毫无意义
- 破坏了java的哲学：避免指针
- 在类型系统上开了个口子：可以被赋值给任务一引用类型的变量

其他语言的null替代
- Groovy: 安全导航操作符能够避免，并在调用链中的变量遭遇null时引用沿着调用链传下去，返回一个null，实际上是一个大扫把，让使用者可以毫无
- Haskell包含一个Maybe类型，本质上是对Optional值的封装，Maybe类型的变量可以是指定类型的值，也可以什么都不是。
- Scala类似的数据结构，名字叫做Optional[T],既可以包含类型T的变量，也可以不包含该变量。显示地调用Option类型的available操作，也是一种变相的null检查

### Optional类
java.util.Optional<T>  

Optional代表可以有，或者没有（Optional.empty()），代表一种意图  
代码中应该始终一致地使用Optional，能非常清晰地节点出变量值的缺失是结构上的问题，还是算法上的缺陷，抑或是数据中的问题。

```
public class Person {

    private Optional<Car> car;  // 可能有车或者没车

    public Optional<Car> getCar() {
        return car;
    }
}

public class Insurance {

    private String name;  // 必须有
    public String getName() {
        return name;
    }
}

```

#### 使用：

- 空对象  
Optional<T> optT =Optional.empty();  
- 依据非空值创建
Optional<T> optT =Optional.of(value); //  if value is null，throws Exception  

- 可接受NUll的创建  
Optional<T> optT =Optional.ofNullable(value); //  if value is null，fine  

Optional 提供了map，flatmap，filter等方法  
- 可以把Optional对象看成一种集合数据，至少包含一个元素  
- 无法序列化，因为Optional类设计时就没有考虑将其作为类的字段使用，并未实现Serialization接口
使用：
```
public class Person {  // 依然可以序列化

    private Car  car;  

    public Car getCar() {
        return car;
    }
    public Optional<Car> getCarAsOptional() {  // 提供一个变量值可能缺失的接口
        return Optional.ofNullable(car);
    }
    
}

```
#### 一些使用  
- get()，最简单单不安全，if null，抛出NoSuchElementException异常
- orElse(T other),允许在Optional不包含值时提供一个默认值
- orElseGet(Supplier<? extends T> other),是orElse方法的延迟调用版本，表示某个方法在对象为空时才调用
- orElseThrow(Supplier<? extends T> other)，为空时，定制希望抛出的异常类型
- ifPresent(Consumer<? super T> ) 变量值存在时，执行一个方法

#### 组合使用    
如下，flatMap 如果person is null，则传递的lambda不会执行，并返回Optional对象,类似,map方法也是如此，
然后在最里面调用最原始的放啊，完成期望操作，-------避免2次null检查

```
public Optional<Insurance> nullSfaeFindCheapestInsurence(Optional<Person> person, Optional<Car> car) {

        return person.flatMap(p -> car.map(c -> findCheapestInsurence(p, c)));

}

```

### 用Optional封住可能为null的值
示例：
``` 
 Object value= map.get("key")

 改为 
 
  Optional<Object> value= Optional.ofNullable(map.get("key"))
  
```

如下，将这些方法封装为一个工具类，直接调用，无需再写笨拙的try/catch

``` 
public static Optional<Integer> stringToInt(String a){
        try{
            return Optional.of(Integer.parseInt(a));
        }catch (NumberFormatException e){
            return Optional.empty(); //出错返回一个空对象 
        }
    }
```

基础类型的Optional，只有OptionalInt,OptionalLong,OptioalDouble，意义不大，不建议用，毕竟Optional只包含一个值，没有多大的box/unbox开销  

```
 public static int readDurationWithOptional(Properties props, String name) {
        return Optional
                .ofNullable(props.getProperty(name))
                .flatMap(ReadPositiveIntParam::s2i)
                .filter(i -> i > 0)
                .orElse(0);
    }

    public static Optional<Integer> s2i(String s) {
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }


```











