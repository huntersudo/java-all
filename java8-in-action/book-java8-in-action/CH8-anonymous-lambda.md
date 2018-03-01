CH8  - 软件设计方法，设计模式，重构，测试、调试
CH9  - 默认方法，改进api
CH10 - java.util.Optional


## 8.1 重构
- 重构代码，lambda取代匿名类
- 方法引用重构lambda
- StreamAPI 重构命令式的数据处理

### 匿名类 -> lambda 

- 匿名类中this代表类本身,lambda中this代表的是包含类
- 匿名类可以屏蔽包含类的变量,lambda不能
- 在涉及重载的上下文里，转换为lambda可能会更晦涩，匿名类的类型是在初始化时确定的，而lambda的类型取决于它的上下文.


```
 
        Runnable r1=new Runnable() {
            @Override
            public void run() {
                System.out.println("HELLO");
            }
        };

        Runnable r2=()-> System.out.println("HELLO");

```

### lambda -> 方法引用
lambda适合需要传递代码片段的场景,为了改善代码的可读性，尽量使用代码引用
- 将lambda表达式抽取到一个方法内
- 尽量使用静态辅助方法，比如sum,maxBy
- 尽量使用集合类,可以更清楚地表达问题陈述

```
int totalCalories = menu.strean().map(Dish::getCalories)
                                 .resuce(0,(x,y)->x+y);
// 使用下面的                            
int totalCalories = menu.strean().collect(summingInt(Dish::getCalories));

```

### 命令式的数据处理 切换到Stream 
- 将所有使用迭代器的处理模式全部改为Stream API,更能表明意图
- 筛选和抽取
- 这种转换比较麻烦，因为要考虑很多控制流语句，比如break，continue等

```
List<String> dishNames = new ArrayList<>();
for(Dish dish :menu){
 if(dish.getCalories()>300){
   dishNames.add(dish.getName())
 }
}
// 下面看起来更像问题陈述

menu.parallelStream()
    .filter( d -d.getCalories()>300)
    .map(Dish::getName())
    .collect(toList())


```


### 增加代码的灵活性
- lambda 表达式利于 行为参数化
- 模式1 ：有条件的延迟执行
场景： 如果需要频繁地去客户端代码查询一个对象的状态，只是为了传递参数，或者方法调用，那么考虑实现一个新方法，
以lambda或者方法比到时作为参数，新方法在检查完该对象的状态后才调用原来到方法，这样代码会更易读,封装性更好;

```
if(logger.isLogger(LOg.FINER)){
 logger.finer("Problem:"+generateDiagnostic());
}
//Q1: isLogger暴露了客户端代码
//Q2: 每输出一条日志，都要去查询状态
// -- log会内部自己判断
 logegr.log(Level.FINER,"Problem:"+generateDiagnostic()); 

// java 8
logegr.log(Level.FINER,() -> "Problem:"+generateDiagnostic()); 

//内部实现:
public void log(Level level,Supplier<String> msg){
  if(logger.isLoggable(level)){
   log(level,msg.get())   // 执行lambda
  }
}

```


- 模式2 ：环绕执行
场景：重用准备和清理阶段的逻辑，

```

String oneLine = processFile((BufferedReader b) -> b.readLine());		
String twoLines = processFile((BufferedReader b) -> b.readLine() + b.readLine());

public static String processFile(BufferedReaderProcessor p) throws IOException {
		try(BufferedReader br = new BufferedReader(new FileReader("lambdasinaction/chap3/data.txt"))){
			return p.process(br);
		}
}	

public interface BufferedReaderProcessor{
		public String process(BufferedReader b) throws IOException;
}


```

## 8.2 设计模式

策略、模板、观察者、责任链、工厂

### 策略

避免僵化代码,也不用创建各个子类了，之间传递lambda

```
 public static void main(String[] args) {
        // old school
        Validator v1 = new Validator(new IsNumeric());
        System.out.println(v1.validate("aaaa"));
        Validator v2 = new Validator(new IsAllLowerCase ());
        System.out.println(v2.validate("bbbb"));

        // with lambdas
        Validator v3 = new Validator((String s) -> s.matches("\\d+"));
        System.out.println(v3.validate("aaaa"));
        Validator v4 = new Validator((String s) -> s.matches("[a-z]+"));
        System.out.println(v4.validate("bbbb"));
    }

    interface ValidationStrategy {
        public boolean execute(String s);
    }

    static private class IsAllLowerCase implements ValidationStrategy {
        public boolean execute(String s){
            return s.matches("[a-z]+");
        }
    }
    static private class IsNumeric implements ValidationStrategy {
        public boolean execute(String s){
            return s.matches("\\d+");
        }
    }

    static private class Validator{
        private final ValidationStrategy strategy;
        public Validator(ValidationStrategy v){
            this.strategy = v;
        }
        public boolean validate(String s){
            return strategy.execute(s); }
    }

``` 

### 模板
传递lambda，直接插入不同的行为，不再继承

```
abstract class OnlineBanking {
    public void processCustomer(int id){
        Customer c = Database.getCustomerWithId(id);
        makeCustomerHappy(c);
    }
    abstract void makeCustomerHappy(Customer c);
}

//

public void processCustomer(int id, Consumer<Customer> makeCustomerHappy){
        Customer c=Database.getCustomerWithId(id);
        makeCustomerHappy.accept(c);
}

// 使用
new OnlineBankingLambda().processCustomer(1337, (Customer c) -> System.out.println("Hello!"));

```

### 观察者 

- 直接传递lambda ，无需显式地实例化是哪个观察者对象
- lambda在逻辑简单时比较好，如果逻辑很复杂，建议还是用类或者 方法引用的方式

```
public static void main(String[] args) {
        Feed f = new Feed();
        f.registerObserver(new NYTimes());
        f.registerObserver(new Guardian());
        f.registerObserver(new LeMonde());
        f.notifyObservers("The queen said her favourite book is Java 8 in Action!");

        Feed feedLambda = new Feed();

        feedLambda.registerObserver((String tweet) -> {
            if(tweet != null && tweet.contains("money")){
                System.out.println("Breaking news in NY! " + tweet); }
        });
        feedLambda.registerObserver((String tweet) -> {
            if(tweet != null && tweet.contains("queen")){
                System.out.println("Yet another news in London... " + tweet); }
        });

        feedLambda.notifyObservers("Money money money, give me money!");

    }


    // 观察者
    interface Observer{
        void inform(String tweet);
    }
    
    // 不同的观察者 
    static private class NYTimes implements Observer{
        @Override
        public void inform(String tweet) {
            if(tweet != null && tweet.contains("money")){
                System.out.println("Breaking news in NY!" + tweet);
            }
        }
    }


    // Subject 接口 

    interface Subject{
        void registerObserver(Observer o);
        void notifyObservers(String tweet);
    }

    // Subject 使用这个去通知 
    static private class Feed implements Subject{
        private final List<Observer> observers = new ArrayList<>();
        public void registerObserver(Observer o) {
            this.observers.add(o);
        }
        public void notifyObservers(String tweet) {
            observers.forEach(o -> o.inform(tweet));
        }
    }


```

### 责任链

创建处理对象序列, 1-1-1 传递给后继
- lambda直接实现 各个操作逻辑，不用再单独写类去实现

```
 public static void main(String[] args) {
        ProcessingObject<String> p1 = new HeaderTextProcessing();
        ProcessingObject<String> p2 = new SpellCheckerProcessing();
        p1.setSuccessor(p2);
        String result1 = p1.handle("Aren't labdas really sexy?!!");
        System.out.println(result1);


        UnaryOperator<String> headerProcessing =
                (String text) -> "From Raoul, Mario and Alan: " + text;
        UnaryOperator<String> spellCheckerProcessing =
                (String text) -> text.replaceAll("labda", "lambda");
        // 操作链 
        Function<String, String> pipeline = headerProcessing.andThen(spellCheckerProcessing);
        
        String result2 = pipeline.apply("Aren't labdas really sexy?!!");
        
        System.out.println(result2);
    }


////////////
    static private abstract class ProcessingObject<T> {
        protected ProcessingObject<T> successor;

        public void setSuccessor(ProcessingObject<T> successor) {
            this.successor = successor;
        }

        public T handle(T input) {
            T r = handleWork(input);
            if (successor != null) {
                return successor.handle(r);
            }
            return r;
        }

        abstract protected T handleWork(T input);
    }

    static private class HeaderTextProcessing
            extends ProcessingObject<String> {
        public String handleWork(String text) {
            return "From Raoul, Mario and Alan: " + text;
        }
    }

    static private class SpellCheckerProcessing
            extends ProcessingObject<String> {
        public String handleWork(String text) {
            return text.replaceAll("labda", "lambda");
        }
    }


```

### 工厂
 无需暴露实例化的逻辑就能完成对象的创建
  public static void main(String[] args) {
         
         Product p1 = ProductFactory.createProduct("loan");
 
         // lambda 
         Supplier<Product> loanSupplier = Loan::new;
         Product p2 = loanSupplier.get();
         Product p3 = ProductFactory.createProductLambda("loan");
 
     }
 
     static private class ProductFactory {
         public static Product createProduct(String name){  
             switch(name){
                 case "loan": return new Loan();
                 case "stock": return new Stock();
                 case "bond": return new Bond();
                 default: throw new RuntimeException("No such product " + name);
             }
         }
         // lambda 
         public static Product createProductLambda(String name){  
             Supplier<Product> p = map.get(name);
             if(p != null) return p.get();
             throw new RuntimeException("No such product " + name);
         }
     }
 
     static private interface Product {}
     static private class Loan implements Product {}
     static private class Stock implements Product {}
     static private class Bond implements Product {}
 
     // lambda 
     final static private Map<String, Supplier<Product>> map = new HashMap<>();
     static {
         map.put("loan", Loan::new);
         map.put("stock", Stock::new);
         map.put("bond", Bond::new);
     }
     
## lambda 表达式测试
- 不好测，涉及lambda表达式的跟踪栈可能非常难理解

## peek
使用peek查看Stream流中的数据流的值

 List<Integer> result = 
                Stream.of(2, 3, 4, 5)
                .peek(x -> System.out.println("taking from stream: " + x))
                .map(x -> x + 17)
                .peek(x -> System.out.println("after map: " + x))
                .filter(x -> x % 2 == 0)
                .peek(x -> System.out.println("after filter: " + x))
                .limit(3)
                .peek(x -> System.out.println("after limit: " + x))
                .collect(toList());


