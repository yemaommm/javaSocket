package so.sao.shop.gpssocket.Utils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author negocat on 2017/10/30.
 */
public class CodeUtils {

    private static final String stmp = "00000000";

    /**
     * @功能: BCD码转为10进制串(阿拉伯数据)
     * @参数: BCD码
     * @结果: 10进制串
     */
    public static String bcd2Str(byte[] bytes) {
        if (bytes.length <= 0){
            return "";
        }
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
                .toString().substring(1) : temp.toString();
    }

    /**
     * @功能: 10进制串转为BCD码
     * @参数: 10进制串
     * @结果: BCD码
     */
    public static byte[] str2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;
        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
     * @param value
     *            要转换的int值
     * @return byte数组
     */
    public static byte[] intToBytes( int value )
    {
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }
    /**
     * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。  和bytesToInt2（）配套使用
     */
    public static byte[] intToBytes2(int value)
    {
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16)& 0xFF);
        src[2] = (byte) ((value>>8)&0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
     *
     * @param src
     *            byte数组
     * @param offset
     *            从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset+1] & 0xFF)<<8)
                | ((src[offset+2] & 0xFF)<<16)
                | ((src[offset+3] & 0xFF)<<24));
        return value;
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
     */
    public static int bytesToInt2(byte[] src, int offset) {
        int value;
        value = (int) ( ((src[offset] & 0xFF)<<24)
                |((src[offset+1] & 0xFF)<<16)
                |((src[offset+2] & 0xFF)<<8)
                |(src[offset+3] & 0xFF));
        return value;
    }

    /**
     * bytes转二进制字符串
     */
    public static String bytesToBinaryString(byte[] b){
        String s = new BigInteger(1, b).toString(2);
        int len = 8*b.length;
        len -= s.length();
        if (len <= 0){
            return s;
        }
        StringBuffer str = new StringBuffer();
        for (int i = 0;i < len/8;i++){
            str.append(stmp);
        }
        len = len % 8;
        if (len <= 0){
            return str.toString();
        }
        str.append(stmp.substring(0, len) + s);

        return str.toString();
    }

    /**
     * bytes转十六进制字符串
     */
    public static String bytesToHexString(byte[] b){
        String s = new BigInteger(1, b).toString(16);
        int len = 2*b.length;
        len -= s.length();
        if (len <= 0){
            return s;
        }
        StringBuffer str = new StringBuffer();
        for (int i = 0;i < len/2;i++){
            str.append(stmp);
        }
        len = len % 2;
        if (len <= 0){
            return str.toString();
        }
        str.append(stmp.substring(0, len) + s);

        return str.toString();
    }

    /**
     * 二进制字符串转bytes
     */
    public static byte[] binaryToBytes(String str){
        return new BigInteger(str, 2).toByteArray();
    }

    /**
     * 日期时间：0A03170F3217，年月日时分秒，每个占 1byte，转换过来 10 年 3 月 23 日 15 时50 分 23 秒，年份再加 2000 就是 2010 年。
     */
    public static byte[] toDate(){
        byte[] bytes = new byte[6];
        LocalDateTime now = LocalDateTime.now();
        bytes[0] = (byte) (now.getYear() - 2000);
        bytes[1] = (byte) (now.getMonth().getValue()+1);
        bytes[2] = (byte) now.getDayOfMonth();
        bytes[3] = (byte) now.getHour();
        bytes[4] = (byte) now.getMinute();
        bytes[5] = (byte) now.getSecond();

        return bytes;
    }

}
