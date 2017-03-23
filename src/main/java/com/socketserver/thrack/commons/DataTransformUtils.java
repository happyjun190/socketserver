package com.socketserver.thrack.commons;

import java.math.BigDecimal;

/**
 * Created by ziye on 2017/3/22.
 */
public class DataTransformUtils {

    //   物理量         单位     倍率         说明
    //电压(包括交直流)    V       10          16 位无符号整型量，范围 0~65535， 放大 10 倍 表示， 如 3456 表示电压为 345.6V
    //电流(包括交直流)    A       10          16 位无符号整型量， 范围 0~65535，放大 10 倍 表示， 如 123  表示电流为 12.3A
    //频率               Hz      100         16 位无符号整型量，范围 0~65535，放大 10 倍 表示， 如 5000 表示 50.00Hz
    //功率（包括交直流）  KW      1000        32 位无符号整型量，范围 0~0xFFFF FFFF，放大 1000 倍表示，如 5000 表示 5.00KW
    //功率因数            /      1000        16 位有符号整型量，范围-32767~32768，放大1000 倍表示
    //                                                         实际范围-1~+1，负数用补码表示，数值范围-1000~1000
    //                                                         例如： 998 表示功率因数为 0.998
    //                                                         例如： 0xfc7c 表示功率因数为-0.900
    //电量               KWh     100          32 位无符号整型量，范围 0~0xFFFF FFFF。 100表示 1KWh
    //温度               ℃      100          16 位有符号整型量，范围-32767~32768，放大100 倍表示，负温度取补码，如 0xf63c 为-25℃
    //接地电阻            M      100          16 位无符号整型量，范围 0~65535，放大 100倍表示， 如 123 表示接地电阻为 1.23M
    //漏电流             mA      100          16 位无符号整型量，范围 0~65535，放大 100倍表示，如 12345 表示实际漏电流 123.45mA
    //风速               m/s     100          16 位无符号整型量，范围 0~65535，放大 100倍表示， 如 2345 表示风速 23.45m/s
    //工作或运行时间      s       1           32 位无符号整型量，范围 0~0xFFFFFFFF，大约可表示到 136 年
    //汇率                /      100          16 位无符号整型量，范围 0~65535，每 KWh 对应的价格。如 234 表示 1KWh 的价格是 2.34 元
    //省钱量             元      100          32 位无符号整型量，范围 0~0xFFFFFFFF， 100表示 1 元
    //CO2                Kg      100          32 位无符号整型量，范围 0~0xFFFFFFFF， 100表示 1kg


    /**
     * 转换2byte数据和指定的倍数，输出为无符号的数据
     * @param orginBytes 2个byte
     * @param multiple 数据放大倍数
     * @return
     */
    public static BigDecimal tranfrom2ByteAndMulToUnsignedRealValue(byte[] orginBytes, int multiple) {
        BigDecimal highByteToNum = new BigDecimal(orginBytes[0] & 0x000000FF);
        BigDecimal lowByteToNum = new BigDecimal(orginBytes[1] & 0x000000FF);
        BigDecimal boost = highByteToNum.multiply(new BigDecimal(Math.pow(16d, 2d))).add(lowByteToNum);
        return boost.divide(new BigDecimal(multiple));
    }


    /**
     * 转换2byte数据和指定的倍数，输出为有符号的数据
     * @param orginBytes 2个byte
     * @param multiple 数据放大倍数
     * @return
     */
    public static BigDecimal tranfrom2ByteAndMulToSignedRealValue(byte[] orginBytes, int multiple) {
        int realHighByteToNum = (int) orginBytes[0];
        int realLowByteToNum = (int) orginBytes[1];
        BigDecimal highByteToNum = new BigDecimal(orginBytes[0] & 0x000000FF);
        BigDecimal lowByteToNum = new BigDecimal(orginBytes[1] & 0x000000FF);
        BigDecimal boost = highByteToNum.multiply(new BigDecimal(Math.pow(16d, 2d))).add(lowByteToNum);
        if(realHighByteToNum>=0&&realLowByteToNum>=0) {
            return boost.divide(new BigDecimal(multiple));
        } else {
            return new BigDecimal(0).subtract(boost.divide(new BigDecimal(multiple)));
        }
    }


    /**
     * 转换4byte数据和指定的倍数，输出为无符号的数据
     * @param orginBytes 4个byte
     * @param multiple 数据放大倍数
     * @return
     */
    public static BigDecimal tranfrom4ByteAndMulToUnsignedRealValue(byte[] orginBytes, int multiple) {
        BigDecimal high2BytesValue = tranfrom2ByteAndMulToUnsignedRealValue(new byte[]{orginBytes[0], orginBytes[1]}, 1);
        BigDecimal low2BytesValue = tranfrom2ByteAndMulToUnsignedRealValue(new byte[]{orginBytes[2], orginBytes[3]}, 1);
        BigDecimal boost = high2BytesValue.multiply(new BigDecimal(Math.pow(16d, 4d))).add(low2BytesValue);
        return boost.divide(new BigDecimal(multiple));

    }


    /**
     * 转换4byte数据和指定的倍数，输出为有符号的数据
     * @param orginBytes 4个byte
     * @param multiple 数据放大倍数
     * @return
     */
    public static BigDecimal tranfrom4ByteAndMulToSignedRealValue(byte[] orginBytes, int multiple) {
        BigDecimal high2BytesValue = tranfrom2ByteAndMulToSignedRealValue(new byte[]{orginBytes[0], orginBytes[1]}, 1);
        BigDecimal low2BytesValue = tranfrom2ByteAndMulToSignedRealValue(new byte[]{orginBytes[2], orginBytes[3]}, 1);

        BigDecimal boost;
        //高位为负数 or 高位为0且低位负数
        //high2BytesValue <0 或者 (high2BytesValue=0 and low2BytesValue<0 )
        if((high2BytesValue.compareTo(new BigDecimal("0"))==-1)||
                (high2BytesValue.compareTo(new BigDecimal("0"))==0)&&low2BytesValue.compareTo(new BigDecimal("0"))==-1 ) {
            boost = new BigDecimal(0).subtract(high2BytesValue.abs().multiply(new BigDecimal(Math.pow(16d, 4d))).add(low2BytesValue.abs()));
        } else {
            boost = high2BytesValue.abs().multiply(new BigDecimal(Math.pow(16d, 4d))).add(low2BytesValue.abs());
        }
        return boost.divide(new BigDecimal(multiple));
    }


    /**
     * 获取数据字节数据中，从offset开始的length个字节
     * @param dataBytes
     * @param offset
     * @param length
     * @return
     */
    public static byte[] getBytesArrFromOffsetAndLength(byte[] dataBytes, int offset, int length) {
        byte[] resultBytes = new byte[length];
        int index = 0;
        for(int i=offset; i<offset+length; i++) {
            resultBytes[index++] = dataBytes[i];
        }
        return resultBytes;
    }




    public static void main(String args[]) {
        System.out.println(tranfrom2ByteAndMulToUnsignedRealValue(new byte[]{(byte) 0xFF, (byte) 0xFF}, 10));
        System.out.println(tranfrom2ByteAndMulToSignedRealValue(new byte[]{(byte) 0xFF, (byte) 0xFF}, 10));
        System.out.println(tranfrom4ByteAndMulToUnsignedRealValue(new byte[]{(byte) 0xFF, (byte) 0xFF,(byte) 0xFF, (byte) 0xFF}, 1));
        System.out.println(tranfrom4ByteAndMulToSignedRealValue(new byte[]{(byte) 0xFF, (byte) 0xFF,(byte) 0xFF, (byte) 0xFF}, 10));
        //System.out.println(new BigDecimal(4294967295d).divide(new BigDecimal(3600d)).divide(new BigDecimal(24d)).divide(new BigDecimal(365d)));
    }

}
