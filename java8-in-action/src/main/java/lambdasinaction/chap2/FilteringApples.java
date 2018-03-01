package lambdasinaction.chap2;

import java.util.*;
import java.util.function.Predicate;

public class FilteringApples{

	public static void main(String ... args){

		List<Apple> inventory = Arrays.asList(new Apple(80,"green"),
				new Apple(155, "green"),
				new Apple(120, "red"));

		// [Apple{color='green', weight=80}, Apple{color='green', weight=155}]
		List<Apple> greenApples = filterApplesByColor(inventory, "green");
		System.out.println(greenApples);

		// [Apple{color='red', weight=120}]
		List<Apple> redApples = filterApplesByColor(inventory, "red");
		System.out.println(redApples);

		/**
		 *策略模式， 但是需要声明很多只需要实例化一次的类
		 */
		// [Apple{color='green', weight=80}, Apple{color='green', weight=155}]
		List<Apple> greenApples2 = filter(inventory, new AppleColorPredicate());
		System.out.println(greenApples2);

		// [Apple{color='green', weight=155}]
		List<Apple> heavyApples = filter(inventory, new AppleWeightPredicate());
		System.out.println(heavyApples);

		// []
		List<Apple> redAndHeavyApples = filter(inventory, new AppleRedAndHeavyPredicate());
		System.out.println(redAndHeavyApples);

		/**
		 * 匿名类,可以同时声明和实例化一个类
		 * but，匿名类太笨重，还是有很多模板代码
		 *
		 */
		// [Apple{color='red', weight=120}]
		List<Apple> redApples2 = filter(inventory, new ApplePredicate() {
			public boolean test(Apple a){
				return a.getColor().equals("red"); 
			}
		});
		System.out.println(redApples2);


		/**
		 * lambda 表达式
		 * 参数化，可以更好的适应变化
		 */
		List<Apple> redApples3 = filter(inventory, (Apple a)->a.getColor().equals("red"));
		System.out.println(redApples3);
		/**
		 *
		 */

		System.out.println("hhhhhhhhhhhhhh");
		List<Integer> numbers=Arrays.asList(new Integer[]{0,1,2,3,4,5,6});
        List<Integer> evenNumbers =filter2(numbers,(Integer i)-> i%2==0);
		System.out.println(evenNumbers);

	}

	/**
	 *泛型化的，可以用在更多的地方,如上，
	 */
	public static <T> List<T> filter2(List<T> list, Predicate<T> p){
		List<T> result = new ArrayList<>();
		for(T e : list){
			if(p.test(e)){
				result.add(e);
			}
		}
		return result;
	}


	/**
	 * 根据抽象条件筛选
	 * 通过具体实现来传递行为
	 * 再来看，各个具体的实现-->>>然后行为参数化，实际上就是lambda表达式
	 * 可以把迭代要筛选的集合逻辑与集合中的每个元素应用的行为区分开来，这样可以重复使用同一个方法，给他不同的行为(lambda表达式)达到不同的牡蛎
	 *
	 */

	public static List<Apple> filter(List<Apple> inventory, ApplePredicate p){
		List<Apple> result = new ArrayList<>();
		for(Apple apple : inventory){
			if(p.test(apple)){
				result.add(apple);
			}
		}
		return result;
	}

	/**
	 * 需要一种比添加很多参数更好的方法来应对变化的需求，更高层次的抽象
	 * 对所选的标准建模：考虑的是苹果，需要根据Apple的某些属性来返回一个boolean值，称为谓词predicate
	 *
	 */
	interface ApplePredicate{
		public boolean test(Apple a);
	}

	/**
	 * 下面的多个实现代表不同的实现标准,类似策略模式，然后修改上面的方法 filter(List<Apple> inventory, ApplePredicate p)
	 */
	static class AppleWeightPredicate implements ApplePredicate{
		public boolean test(Apple apple){
			return apple.getWeight() > 150; 
		}
	}
	static class AppleColorPredicate implements ApplePredicate{
		public boolean test(Apple apple){
			return "green".equals(apple.getColor());
		}
	}

	static class AppleRedAndHeavyPredicate implements ApplePredicate{
		public boolean test(Apple apple){
			return "red".equals(apple.getColor()) 
					&& apple.getWeight() > 150; 
		}
	}



	/**
	 * 不同的需求，大量重复代码
	 */
	public static List<Apple> filterGreenApples(List<Apple> inventory){
		List<Apple> result = new ArrayList<>();
		for(Apple apple: inventory){
			if("green".equals(apple.getColor())){
				result.add(apple);
			}
		}
		return result;
	}
	public static List<Apple> filterApplesByColor(List<Apple> inventory, String color){
		List<Apple> result = new ArrayList<>();
		for(Apple apple: inventory){
			if(apple.getColor().equals(color)){
				result.add(apple);
			}
		}
		return result;
	}
	public static List<Apple> filterApplesByWeight(List<Apple> inventory, int weight){
		List<Apple> result = new ArrayList<>();
		for(Apple apple: inventory){
			if(apple.getWeight() > weight){
				result.add(apple);
			}
		}
		return result;
	}


}