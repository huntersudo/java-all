package lambdasinaction.chap3;

import java.io.*;
<<<<<<< HEAD
=======

/**
 * 将普通需求转换为用lambda实现的例子
 */
>>>>>>> develop
public class ExecuteAround {

	public static void main(String ...args) throws IOException{

        // method we want to refactor to make more flexible
        String result = processFileLimited();
        System.out.println(result);

        System.out.println("---");

<<<<<<< HEAD
=======
		/**
		 * 传递的不同的lambda，重用processFile方法
		 */
>>>>>>> develop
		String oneLine = processFile((BufferedReader b) -> b.readLine());
		System.out.println(oneLine);

		String twoLines = processFile((BufferedReader b) -> b.readLine() + b.readLine());
		System.out.println(twoLines);

	}

    public static String processFileLimited() throws IOException {
        try (BufferedReader br =
                     new BufferedReader(new FileReader("lambdasinaction/chap3/data.txt"))) {
            return br.readLine();
        }
    }

<<<<<<< HEAD
=======
	/**
	 *  上面是普通的方法，下面是转换为
	 *
	 *  传递一个BufferedReader并返回String的lambda
	 *   并需要一个方法ProcessFile内执行lambda所代表的代码
	 *
	 * @param p
	 * @return
	 * @throws IOException
	 */
>>>>>>> develop

	public static String processFile(BufferedReaderProcessor p) throws IOException {
		try(BufferedReader br = new BufferedReader(new FileReader("lambdasinaction/chap3/data.txt"))){
			return p.process(br);
		}

	}

<<<<<<< HEAD
=======
	/**
	 * 函数式接口:
	 */
>>>>>>> develop
	public interface BufferedReaderProcessor{
		public String process(BufferedReader b) throws IOException;

	}
}
