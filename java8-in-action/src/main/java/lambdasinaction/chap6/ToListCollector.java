package lambdasinaction.chap6;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import static java.util.stream.Collector.Characteristics.*;

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
