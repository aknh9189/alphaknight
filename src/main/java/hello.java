/**
 * Created by aknh9189 on 8/11/16.
 */

public class hello {
    public static void main(String[] args) {
        hello myHello = new hello();
        Boolean f = true;
        f.toString();
        System.out.println((myHello.isOne(3)).);

        for (int x=0; x<100; x++) {
            System.out.println("Hello world " + x);
        }
        int b = 53;
        int c = 55;
        if (c - b != 2) {
            System.out.println(c-b);
        }
        else if (c == 55) {
            System.out.println(b+c + "HAHAHAHA");
        }
        else {
            System.out.println("NO MATCHES");
        }
        int[] arr = {1,2,3,4,54,5,6,7,87,8,8}; //itterate through array
        for (int i : arr) {
            System.out.println(i);
        }
        System.out.printf(isOne(5).toString());


    }
    private boolean isOne(int var) {
        return (var == 1);
    }
}



