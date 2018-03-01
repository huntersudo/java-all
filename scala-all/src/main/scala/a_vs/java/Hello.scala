package main.scala.a_vs.java

import scala.io.Source

/**
  * Created by SML on 2018/2/28. 
  *
  * @author SML
  */


// 命令式scala
object Beer {
  def main(args: Array[String]): Unit = {
    var n: Int = 2
    while (n <= 6) {
      println(s"hello ${n} bottles of beer") //s 为插值操作符
      n += 1
    }
  }
}

//函数式scala
// scala 中，任何事物都是对象，不存在原始数据类型

object Foo {
  def main(args: Array[String]): Unit = {
    2 to 6 foreach (x => println(s"hello ${x} bottles of beer"))
  }
}



// 基础数据结构
object Foo2 {
  def main(args: Array[String]): Unit = {
    //1. 创建集合, java8中暂时没有语法糖
    val authorsToAge = Map("Roul" -> 23, "Tom" -> 40)
    val authors = List("Roul", "Tom", "Alan")
    val numbers = Set(1, 2)

    //2. scala集合默认不可变，也有可变版本scala.collection.mutable
    val newNumbers = numbers + 8 //1,2,8

    //3.  scala支持的集合操作和Stream提供的类似

    val fileLines = Source.fromFile("data.txt").getLines.toList

    val linesLongUpper =
      fileLines.filter(line => line.length() > 10)
        .map(line => line.toUpperCase)

    // 简化为
    val linesLongUpper2 =
      fileLines.filter(_.length() > 10)
        .map(_.toUpperCase)

    // 类似java8 Stream 的parallel ,scala 提供 par

    val linesLongUpper_parallel =
      fileLines.par.filter(_.length() > 10)
        .map(_.toUpperCase)

    //4. 元组,java目前不支持，java只能创建自己的数据结构
    // scala提供了名为 元组字面量 的特性来解决这个问题
    val roul = ("abc", 123) //元组类型为(Int,String)
    // 通过存取器accessor _1,_2，  从1开始
    println(roul._1) // 'abc'
    println(roul._2) // 123


    // 5.stream
    //和java8的Strean比起来，Scala版本的Stream内存的使用效率变低了，因为Scala中的Stream需要能够回溯之前的元素，这意味着之前访问过的匀速都需要在内存汇总记录下来，
    // 而java 8 中的不会记录
  }

  // 6.option

  //      def getCarInsuranceName(person:Option<Person>,minAge:Int){
  //          return person.filter(_.getAge() >= minAge)
  //                  .flatMap(_.getCar)
  //                  .flatMap(_.getInsurence)
  //                  .map(_.getName)
  //                  .getOrElse("Unknown")
  //      }
}

object Foo3 {
  // 函数
  // 谓词定义两个筛选条件
  def main(args: Array[String]): Unit = {
    def isJavaMetioned(tweet:String) :Boolean = tweet.contains("Java")

    def isShortTweet(tweet:String):Boolean = tweet.length()<20

    val tweets = List("I love the new features in Java 8","How's id going","An SQL query walks into a bar");

    tweets.filter(isJavaMetioned).foreach(println)

    // filter的函数签名, ()=>Boolean 函数接口,java不支持
//    def filter[T(p: ()=>Boolean):List[T]
  }
}


object Foo4{
  // 匿名函数和闭包,匿名函数和Scala很类似
  val isLongTweet : String => Boolean
         =(tweet :String)=>tweet.length() > 60

  val isLongTweet2 : String => Boolean
  =new Function1[String,Boolean]{
    def apply(tweet:String):Boolean=tweet.length()>60
  }


  def main(args: Array[String]): Unit = {
    isLongTweet.apply("A very short tweet")
    isLongTweet("A very short tweet")   //编译器会将f(a)转换为 f.apply()a

  }

}
object Foo5{
  // 闭包
  //闭包是一个函数实例，可以不受限制第访问该函数的本地变量。
  // java8中的lambda限制，不能修改定义lambda中函数的本地的变量值，这些变量必须隐式为final
  // 与此相反，Scala中的匿名函数可以取得自身的变量，单并非变量当前指向的值

  def main(args: Array[String]): Unit = {
    var count =0
    val inc=()=>count+1   // 闭包，捕获并递增count
    inc()
    println(count)   //输出1
    inc()
    println(count)   //输出2
  }

}


object Foo6{
  def main(args: Array[String]): Unit = {
    // 科里化
    // java中，为了构造科里化的形式需要你手动地切分函数，尤其是参数非常多的时候
    // scala版本,更方便的定义科里化

    def multiply(x:Int,y:Int) =x*y
    val r=multiply(1,10)
//    科里化版本
    def multiplyCurry(x:Int)(y:Int) =x*y
    val r1=multiplyCurry(2)(10)

    //实际上
    val multiplyByTwo:Int => Int= multiplyCurry(2)  // 2y
    val r2=multiplyByTwo(10)

  }

}


object Foo7{

  //scala语言中的构造器、getter方法和setter方法都能隐式地生成
  def main(args: Array[String]): Unit = {
    class Student(var name:String,var id:Int)
    val s=new Student("tom",1)
    println(s.name)
    s.id=2
    println(s.id)
  }
}


object Foo9 {
  // scala中提供了trait,主要为了实现java中的接口，
  //trait既可以定义抽象方法，也可以带有默认实现的方法,同时还支持想接口那样的多继承
  // trait中可以包含抽象类这样的字段，但是java8不支持
  //trait 不是抽象类，tait支持多继承，抽象类则不支持
  // java支持类型的多继承，因为一个类可以实现多个接口
  // java8通过默认方法引入了对行为的多继承，不过依旧不支持对状态的多继承，而trait支持
 trait Sized{
    var size: Int=0
    def isEmpty() = size ==0
  }

  class Empty extends Sized
  print(new Empty().isEmpty()) // true ,因为size恒定为0


  class Box
  val b1=new Box() with Sized   //对象实例化时构建trait
  print(b1.isEmpty()) //true
  // 如果一个类继承了多个trait，这些trait中又使用了相同的方法签名或者字段，有一系列规则，类似java8的默认方法



}