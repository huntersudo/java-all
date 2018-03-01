package lambdasinaction.chap9;

import jdk.nashorn.internal.objects.NativeUint16Array;

import java.util.List;
import java.util.Optional;

public class Utils{
    public static void paint(List<Resizable> l){
        l.forEach(r -> { r.setAbsoluteSize(42, 42); });

        //TODO: uncomment, read the README for instructions
        //l.forEach(r -> { r.setRelativeSize(2, 2); });
    }

    public interface Sized{
        int size();
        default boolean isEmpty(){
            return size()==0;
        }
    }


    public static Optional<Integer> stringToInt(String a){
        try{
            return Optional.of(Integer.parseInt(a));
        }catch (NumberFormatException e){
            return Optional.empty(); //出错返回一个空对象
        }
    }


}
