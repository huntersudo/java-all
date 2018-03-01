package lambdasinaction.chap5;
import lambdasinaction.chap4.*;

import java.util.stream.*;
import java.util.*;

import static lambdasinaction.chap4.Dish.menu;

public class Finding{

    public static void main(String...args){
        if(isVegetarianFriendlyMenu()){
            System.out.println("Vegetarian friendly");
        }

        System.out.println(isHealthyMenu());
        System.out.println(isHealthyMenu2());
        
        Optional<Dish> dish = findVegetarianDish();
        dish.ifPresent(d -> System.out.println(d.getName()));
<<<<<<< HEAD
=======

        menu.stream().filter(Dish::isVegetarian).findFirst();
>>>>>>> develop
    }
    
    private static boolean isVegetarianFriendlyMenu(){
        return menu.stream().anyMatch(Dish::isVegetarian);
    }
    
    private static boolean isHealthyMenu(){
        return menu.stream().allMatch(d -> d.getCalories() < 1000);
    }
    
    private static boolean isHealthyMenu2(){
        return menu.stream().noneMatch(d -> d.getCalories() >= 1000);
    }
<<<<<<< HEAD
    
=======

    /**
     * java.util.Optional
     * Optional<T> 是一个容器类，代表一个值存在或者不存在，在上面的代码中，findAny可能什么都没找到，引入这个就不用返回null了
     * 几个方法：
     * ifPresent(),包含值的时候返回true，否则false
     * ifPresent(Consumer<T> block)会在值存在的时候执行给定的代码块
     * T get(),存在即返回值 ，否则抛出 NoSuchElement的异常
     * T orElse(T other),会在值存在时返回值，否则返回一个默认值
     *
     */

>>>>>>> develop
    private static Optional<Dish> findVegetarianDish(){
        return menu.stream().filter(Dish::isVegetarian).findAny();
    }
    
}
