

## FP -thinking
副作用,不变性,声明式编程,引用透明性  

无副作用：如果一个方法既不修改它内嵌类的状态，也不修改其他对象的状态，使用return返回所有的计算结果.
副作用：函数的效果已经超过了函数自身的范畴

如何做，oop
做什么，声明式编程

准则：被称为“函数式’的函数或方法  
- 只能修改本地变量，除此之外，  
- 它的引用的对象都应该是不可变的对象。  
- 期望所有的字段均为final类型  
- 且函数或者方法不应该抛出任何异常。  
- 不抛出异常，就使用Optional<T>类型，
- 函数要么返回一个值表示调用成功，要么返回一个对象，表明其无法进行指定的操作。

引用透明性：  
- 没有可感知的副作用
- 不改变对调用者可变的变量，不进行IO,不抛出异常
- 一个函数只要传递同样的参数值，总是返回同样的结果。

不要修改传入的参数，一般选择创建副本

递归和迭代

纯粹的函数式编程语言通常不包含像while或者for这样的迭代构造器,修改公共变量等

无需修改的递归重写，避免使用迭代。

## 单纯的递归需要消耗过的内存，------>  尾调优化(tail-call optimization)，递归调用发生在方法的最后，编译器可以自行决定复用某个栈帧的内存，

java8还不支持这种优化

```  
// 迭代
  public static int factorialIterative(int n) {
        int r = 1;
        for (int i = 1; i <= n; i++) {
            r*=i;
        }
        return r;
    }
    
// 递归
    public static long factorialRecursive(long n) {
        return n == 1 ? 1 : n*factorialRecursive(n-1);
    }

   // tail-call 
   
    public static long factorialTailRecursive(long n) {
        return factorialHelper(1, n);
    }

    public static long factorialHelper(long acc, long n) {
        return n == 1 ? acc : factorialHelper(acc * n, n-1);
    }
    
```




