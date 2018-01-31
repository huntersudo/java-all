
java8主要变化反映了从oop到fp的转变，如何表示和如何实现的问题
总结：语言需要不断改进以跟进硬件的更新或者满足程序员的期待。


1.2. 一等公民
java中的函数，一般特指方法，尤其是静态方法； java8中新增了函数--值的一种形式，帮函数当做值传递。
分析：编程语言的整个目的就在于操作值，要是按照历史上编程语言的传统，这些值就被称为  一等值（一等公民）。
     编程语言中，其他结构（类，方法等）在执行期间不能传递，因而是二等公民，
     so: 在运行时传递方法能将方法变成一等公民，-------java8加入了这个功能。
 
方法引用:
   scala和groovy已验证，将方法等概念作为一等值可以扩充程序员的工具库---库设计师。
    listFile(File:isHidden)
    方法引用：：语法   -- 把这个方法作为值的意思 


   根据抽象条件筛选
  	 * 通过具体实现来传递行为
  	 * 再来看，各个具体的实现-->>>然后行为参数化，实际上就是lambda表达式
  	 * 可以把迭代要筛选的集合逻辑与集合中的每个元素应用的行为区分开来，这样可以重复使用同一个方法，给他不同的行为(策略模式，匿名类，lambda表达式)达到不同的牡蛎
 
行为参数化：就是一个方法接受多个不同的行为作为参数，然后完成不同的行为的能力
         可以让代码适应不断变化的需求，减轻未来的工作量
传递代码：就是讲新行为作为参数传递给方法，但是在java8之前这实现起来很啰嗦。为接口声明许多只用一次的实体类而造成的啰嗦代码，在java8之前可用匿名类来减少，但是很多有模板代码，
        进而，使用lambda表达式
        
     
lambda表达式:
   解决代码啰嗦的问题(即使是匿名类，依然啰嗦)
   理解为：可传递的匿名函数的一种方式，无名称，有参数列表，函数主体，返回类型，可能还有一个可以抛出的异常列表。
 
   独立于方法和类，更简洁
 
   (int x) -> x+1  --‘调用时给定参数x,就返回x+1的值的函数’    
    参数， 箭头，主体(必须有返回值)

（1）函数接口中使用lambda,比如Predicate<T>
    函数式接口：只定义一个抽象方法的Interface，可以同时拥有默认方法
    Lambda表达式允许用以内联的形式为函数式接口的抽象方法提供实现，并把整个表达式作为函数式接口的实例
    函数描述符：
      函数式接口的抽象方法的签名基本上就是Lambda表达式的签名，这种抽象方法称为函数描述符。
      比如 Runnable接口的run方法
    
   如何判定哪些是函数式接口
   
   新JavaAPI 的函数接口都有@FunctionalInterface,这个标注用于表示该接口会设计成一个函数式接口，   
   
   Java自带的函数式接口:Comparable,Runnable,Callable,以及java8引入
   java.util.function.Function,Predicate,Consumer,
    
    Predicate<String> emPredicate=(String s)->s.isEmpty();  // store lambda
      
      public interface Predicate<T> {
             boolean test(T t);  //布尔表达式
      }
       
      public interface Consumer<T>{
            void accept(T t);//对某些对象执行某些操作
      }
        
      public interface Function<T, R> {
              R apply(T t);//接受T，返回 R,实现map的操作
      }
  
  
  原始类型特化: java8 利用特殊IntPredicate,IntFunction等避免装箱,一般加上对应的原始类型前缀
  -------需要进一步研究
  
  任何函数接口都不允许抛出checked Exception,如果需要，
    * 方法1：定义一个函数式接口，并声明checked Exception.
    * 方法2： 把lambda包含在一个try/catch中
  
  
  类型检查：
    lambda类型检查是从上下文(目标类型)推断，lambda所需要代表的类型为目标类型
    特殊的void 兼容规则:如果一个lambda的主体是一个语句表达式，它就是和一个返回void的函数描述符兼容（参数列表兼容的情况下）
    同样的lambda ,不同的函数式接口
     
  类型推断：  
   Comparator<Apple> c= (Apple a1, Apple a2)-> a1.getWeight().compareTo(a2.getWeight());
   Comparator<Apple> c1= (a1, a2)-> a1.getWeight().compareTo(a2.getWeight()); //有类型推断，可写可不写，看是否易读
  
  使用局部变量:
   /**
           * 局部变量的使用,lambda支持使用自由变量，必须是final的
           */
          int portNumber=1337;
          Runnable r=()-> System.out.println(portNumber);
  //        portNumber=1;   // 这就报错了，因为lambda只能捕获指派给他们的局部变量一次，
  
          /**
           * 闭包 closure, 闭包就是一个函数的实例，且它可以无限制的访问那个函数的非本地变量，闭包可以作为参数传递给另外一个函数;
           * java8的lambda和匿名类可以做类似闭包的事情：
           *    可以作为参数传递给方法，
           *    可以访问其作用域外的变量（限制：但是不能修改局部变量的内容,变量必须是final）
           * 可以认为lambda是对值封闭的，而不是对变量封闭，
           * because: 
           *    局部变量是保存在栈上的，并且隐式表示它们仅限于其所在的线程， 如果允许捕获可改变的局部变量，就会引发造成线程不安全的可能性
           *    （实例便变量就可以，因为保存在堆中，而堆是在线程之间共享的）
           * 
           */
  
  方法引用:
   
   inventory.sort((Apple a1,Apple a2)-> a1.getWeight().compareTo(a2.getWeight())
   inventory.sort(comparing(Apple:getWeight))
       
   方法引用，可以看做仅仅调用特定方法的Lambda的一种快捷写法：如果一个Lambda代表的只是“直接调用这个方法”，最好还是用名称调用而不是去描述如何调用。
   将 方法引用看做就是仅仅涉及单一方法的lambda语法糖
       
   如何构建方法引用：
   (1)指向静态方法的引用,Integer:parseInt
   (2)指向任意实例方法的方法引用，String:length
   (3)指向现有方对象的实例方法的方法引用，
   以及其他针对构造函数、数组构造函数和父类调用(super-call)的一些特殊形式的方法引用
     
   构造函数引用：
        //ClassName::new
        Supplier<Apple> c1=Apple::new;  // ()->new Apple()
        Apple a1=c1.get(); //产生一个新的Apple
        Function<Integer,Apple> c2=Apple::new; //  (weight)->new Apple(weight)
        Apple a2=c2.apply(10);
        BiFunction<Integer,String,Apple> c3=Apple::new;// (color,weight)->new Apple(color,weight);
        Apple a3=c3.apply(10,"green");
   
   
   实践：类包裹 -> 匿名类-> lambda->方法引用
   
    
   复合lambda:
     （1）比较器复合
        
             //使用静态方法，Comaprator.comparing
             Comparator<Apple> c= Comparator.comparing(Apple::getWeight);
     
             //逆序
             inventory.sort(c.reversed());
             //比较器链
             inventory.sort(c
                     .reversed()
                     .thenComparing(Apple::getColor));
     
   （2）谓词复合：使用 negate ,and,or
             
             Predicate<Apple> redApple=(a)->"red".equals(a.getColor());
             Predicate<Apple> notRedApple =redApple.negate();// 当前谓词的 非，不是红色的苹果
     
             Predicate<Apple> redAndHeavy=redApple.and(a->a.getWeight()>150); //红色且比较重
     
             Predicate<Apple> redAndHeavyOrGreenApple=redApple.and(a->a.getWeight()>150).or(a->"green".equals(a.getColor())); //红色且比较重或者是绿色的
     
   （3）函数复合：Function 的andThen和compose默认方法
              
             Function<Integer,Integer> f=x->x+1;
             Function<Integer,Integer> g=x->x*2;
     
             Function<Integer,Integer> h=f.andThen(g);  //g(f(x)) 流水线
             System.out.println(h.apply(10)); //22
     
             Function<Integer,Integer> hh=f.compose(g);  //f(g(x))
             System.out.println(hh.apply(10));  //21 
  
  
  (4)积分的计算
  
        /**
         * 积分
         * f(x)=x+10
         * ∫3,7 f(x)dx
         * integrate(x+10,3,7) ：错误的，
         * dx： 以x为自变量，结果是x+10的那个函数
         * 必须写成： integrate((double x)->x+10,3,7)
         * or      integrate((C::f,3,7)
         */
     System.out.println(integrate((double x)->x+10,3,7));   //60

    /*//错误的方法
    public double integerate((double -> double)f,double a,double b){
        return (f(a)+f(b))*(b-a)/2.0
    }*/
    public static double integrate(DoubleFunction<Double> f,double a,double b){
        return (f.apply(a)+f.apply(b))*(b-a)/2.0;
    }

    public interface DoubleFunction<R> {
        R apply(double value);
    }
     
 

    
1.4 接口中的默认方法: 使用default关键字, 写出更容易改进的接口。
  在java8之前，更新一个接口，会导致实现它的类都给更新了-逻辑灾难，在java8中用默认方法解决
  
  
  