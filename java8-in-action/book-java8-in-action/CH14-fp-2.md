
##CH14 FP TRICK

无处不在的函数：
韩式可以作为参数传递，可作为返回值，还能存储在数据结构中。

高阶函数：
- 接受至少一个函数作为参数
- 返回的结果是一个函数


## 科里化：  
- 表示一种将一个带有n元组参数的函数转换成n个一元函数链的方法。

- 是一种将具备2个参数转化为使用一个参数的函数g,并且这个函数的返回值也是一个函数，它会作为新函数的一个参数。  

后者的返回值和初始函数的返回值相同，即f(x,y) = (g(x))(y)  

- 由此，我们可以由此推出：可以将一个使用了6个参数的函数科里化为成一个接受2,4,6号参数，并返回一个接受5号参数的函数，这个函数又返回一个接受剩下的第1号和第3号参数的函数

- 一个函数使用的所有参数仅有部分被传递时,通常我们说和这个函数就是被部分应用的。
示例：

```` 
   static double converter(double x, double y, double z) {
         return x * y + z;
   }
   
   CTOf(x) = x*9/5 + 32 ; // 摄氏度转为华氏
```` 
   //1.乘以转换因子,
   //2.如果需要，进行基线调整
   
   //生产一个带一个参数的转换方法
 ```` 
   static DoubleUnaryOperator curriedConverter(double y, double z) {
        return (double x) -> x * y + z;
   }
 ```` 
   // 
```` 
   DoubleUnaryOperator convertCtoF = curriedConverter(9.0/5, 32);  // 构造一个函数，还可以构造其他类型的函数
   System.out.println(convertCtoF.applyAsDouble(24));  // 75.2 ,相当于直接调用这个函数，入参x
   
   DoubleUnaryOperator convertFtoC = expandedCurriedConverter(-32, 5.0/9, 0);   // 
   System.out.println(convertFtoC.applyAsDouble(98.6));  //37.0   入参x 
  
   static DoubleUnaryOperator expandedCurriedConverter(double w, double y, double z) {
       return (double x) -> (x + w) * y + z;
   }
````
- 并没有一次性地向converter传递x,f,b; 相反，你只要使用了一个参数f，b并返回了一个方法，这个方法会接受参数x,最终返回你期望的值x*f+b.
- 这样，复用了现有的转换逻辑，同时又为不同的住转换因子创建了不同的转换方法。


## Stream的延迟计算
 无法声明一个递归的stream，因为stream仅能使用一次
 
 
 P290附近，寻找更好的解释
 
## 模式匹配


