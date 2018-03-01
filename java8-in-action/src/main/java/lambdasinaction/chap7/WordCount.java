package lambdasinaction.chap7;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class WordCount {

    public static final String SENTENCE =
            " Nel   mezzo del cammin  di nostra  vita " +
            "mi  ritrovai in una  selva oscura" +
            " che la  dritta via era   smarrita ";

    public static void main(String[] args) {
        System.out.println("Found " + countWordsIteratively(SENTENCE) + " words");
        System.out.println("Found " + countWords(SENTENCE) + " words");
    }

    public static int countWordsIteratively(String s) {  //逐个遍历String中的所有字符,
        int counter = 0;
        boolean lastSpace = true;
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(c)) {
                lastSpace = true;
            } else {
                if (lastSpace) counter++;   // 上一个字符是空格，当前字符不是空格，计数器+1
                lastSpace = Character.isWhitespace(c);
            }
        }
        return counter;
    }

    public static int countWords(String s) {
        //Stream<Character> stream = IntStream.range(0, s.length())
        //                                    .mapToObj(SENTENCE::charAt).parallel();
        Spliterator<Character> spliterator = new WordCounterSpliterator(s);
        Stream<Character> stream = StreamSupport.stream(spliterator, true);

        return countWords(stream);
    }

    private static int countWords(Stream<Character> stream) {
        WordCounter wordCounter = stream.reduce(new WordCounter(0, true),
                                                WordCounter::accumulate,
                                                WordCounter::combine);
        return wordCounter.getCounter();
    }

    //遍历Character流时计数的类
    private static class WordCounter {
        private final int counter;
        private final boolean lastSpace;

        public WordCounter(int counter, boolean lastSpace) {
            this.counter = counter;
            this.lastSpace = lastSpace;
        }

        public WordCounter accumulate(Character c) {
            if (Character.isWhitespace(c)) {
                return lastSpace ? this : new WordCounter(counter, true);
            } else {
                return lastSpace ? new WordCounter(counter+1, false) : this;
            }
        }

        public WordCounter combine(WordCounter wordCounter) {  // 合并两个wordCounter，把计数器加起来
            return new WordCounter(counter + wordCounter.counter, wordCounter.lastSpace);
        }

        public int getCounter() {
            return counter;
        }
    }



    private static class WordCounterSpliterator implements Spliterator<Character> {

        private final String string;
        private int currentChar = 0;  // 初始化

        private WordCounterSpliterator(String string) {
            this.string = string;
        }

        @Override
        public boolean tryAdvance(Consumer<? super Character> action) {  // 处理当前字符
            action.accept(string.charAt(currentChar++));
            return currentChar < string.length();  // 如果还有字符要处理，返回true
        }

        @Override
        public Spliterator<Character> trySplit() {
            int currentSize = string.length() - currentChar;
            if (currentSize < 10) {
                return null;   // 返回null表示要处理的字符足够小，可以顺序处理
            }

            //设置试探拆分位置
            for (int splitPos = currentSize / 2 + currentChar; splitPos < string.length(); splitPos++) {

                //让拆分位置前进直到下一个空格,
                if (Character.isWhitespace(string.charAt(splitPos))) {
                    // 新 WordCounterSpliterator 来解析从开始  到 拆分位置 的部分
                    Spliterator<Character> spliterator = new WordCounterSpliterator(string.substring(currentChar, splitPos));
                    // 将当前的WordCounterSpliterator 的起始位置设置为拆分位置
                    currentChar = splitPos;
                    return spliterator;
                }

            }
            return null;
        }

        @Override
        public long estimateSize() {
            return string.length() - currentChar;
        }

        @Override
        public int characteristics() {
            return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
        }
    }
}
