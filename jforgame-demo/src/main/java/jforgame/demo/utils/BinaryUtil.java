package jforgame.demo.utils;

public class BinaryUtil {

    public static String binaryString(int num) {
        StringBuilder sb = new StringBuilder();
        for (int i = 31; i >=0 ; i--) {
            int s = (num >> i) & 1;
            sb.append(s);
        }
        return sb.toString();
    }

}
