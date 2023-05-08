package com.budwk.app.device.handler.common.utils;

import org.nutz.lang.Strings;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @author wizzer.cn
 */
public class ByteConvertUtil {
    public static final char[] HEX_ARRAY = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static final Pattern HEX_PATTERN = Pattern.compile("[\\dA-Fa-f]+");

    public ByteConvertUtil() {
    }

    /**
     * 获取指定位是 0 还是 1
     *
     * @param num
     * @param index
     * @return
     */
    public static int get(int num, int index) {
        return (num & (0x1 << index)) >> index;
    }


    /**
     * 校验是否是合法的 16 进制字符串
     *
     * @param str
     * @return
     */
    public static boolean isValidHex(String str) {
        return Strings.isNotBlank(str) && str.length() % 2 == 0 && HEX_PATTERN.matcher(str).find();
    }

    /**
     * short 转 byte[]
     *
     * @param data
     * @return
     */
    public static byte[] getBytes(short data) {
        byte[] bytes = new byte[2];
        bytes[1] = (byte) (data & 0xff);
        bytes[0] = (byte) ((data & 0xff00) >> 8);
        return bytes;
    }


    public static int crc16Modbu(byte[] bytes, int offset, int count) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < count; i++) {
            CRC ^= ((int) bytes[offset + i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return CRC;
    }

    /**
     * short 转 byte[]
     *
     * @param data
     * @return
     */
    public static byte[] getBytesSmall(short data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        return bytes;
    }


    /**
     * char 转 byte[]
     *
     * @param data
     * @return
     */
    public static byte[] getBytes(char data) {
        byte[] bytes = new byte[2];
        bytes[1] = (byte) (data);
        bytes[0] = (byte) (data >> 8);
        return bytes;
    }

    public static String stringToAscii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sbu.append((int) chars[i]);
            } else {
                sbu.append((int) chars[i]);
            }
        }
        return sbu.toString();
    }

    /**
     * int 转 byte[]
     *
     * @param data
     * @return
     */
    public static byte[] getBytes(int data) {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) (data & 0xff);
        bytes[2] = (byte) ((data & 0xff00) >> 8);
        bytes[1] = (byte) ((data & 0xff0000) >> 16);
        bytes[0] = (byte) ((data & 0xff000000) >> 24);
        return bytes;

    }


    /**
     * int 转 byte[] 小端模式
     *
     * @param data
     * @return
     */
    public static byte[] getBytesSmall(int data) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        bytes[2] = (byte) ((data & 0xff0000) >> 16);
        bytes[3] = (byte) ((data & 0xff000000) >> 24);
        return bytes;

    }

    /**
     * long 转 byte[]
     *
     * @param data
     * @return
     */
    public static byte[] getBytes(long data) {
        byte[] bytes = new byte[8];
        bytes[7] = (byte) (data & 0xff);
        bytes[6] = (byte) ((data >> 8) & 0xff);
        bytes[5] = (byte) ((data >> 16) & 0xff);
        bytes[5] = (byte) ((data >> 24) & 0xff);
        bytes[3] = (byte) ((data >> 32) & 0xff);
        bytes[2] = (byte) ((data >> 40) & 0xff);
        bytes[1] = (byte) ((data >> 48) & 0xff);
        bytes[0] = (byte) ((data >> 56) & 0xff);
        return bytes;
    }

    /**
     * float 转 byte[]
     *
     * @param data
     * @return
     */
    public static byte[] getBytes(float data) {
        int intBits = Float.floatToIntBits(data);
        return getBytes(intBits);
    }

    /**
     * double 转 byte[]
     *
     * @param data
     * @return
     */
    public static byte[] getBytes(double data) {
        long intBits = Double.doubleToLongBits(data);
        return getBytes(intBits);
    }

    /**
     * 字节转为int 低位在前
     *
     * @param buff
     * @param nStart
     * @param nLen
     * @return
     */
    public static int bytes2LfInt(byte[] buff, int nStart, int nLen) {
        int nX = 1;
        int nRet = 0;

        for (int i = 0; i < nLen; ++i) {
            nRet += nX * buff[nStart + i];
            nX <<= 8;
        }

        return nRet;
    }

    /**
     * byte[] 转 Ascii
     *
     * @param bytes
     * @param start
     * @param len
     * @return
     */
    public static String bytes2Ascii(byte[] bytes, int start, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            byte b = bytes[i + start];
            if (b == 0) {
                continue;
            }
            sb.append((char) b);
        }
        return sb.toString();
    }

    /**
     * Ascii 转 byte[]
     *
     * @param ascii
     * @return
     */
    public static byte[] ascii2Bytes(String ascii) {
        char[] chars = ascii.toCharArray();
        byte[] bytes = new byte[chars.length];
        int i = 0;
        for (char ch : chars) {
            bytes[i++] = (byte) ch;
        }
        return bytes;
    }

    /**
     * byte[] 转 short 低字节在前
     *
     * @param buff
     * @param nStart
     * @return
     */
    public static short bytes2LfShort(byte[] buff, int nStart) {
        short s = 0;
        // 最低位
        short s0 = (short) (buff[nStart] & 0xff);
        short s1 = (short) (buff[nStart + 1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

    /**
     * byte[] 转 short 高字节在前
     *
     * @param buff
     * @param start
     * @return
     */
    public static short bytes2HfShort(byte[] buff, int start) {
        return (short) (((buff[0 + start] << 8) | buff[1 + start] & 0xff));
    }

    /**
     * bcd 转int
     *
     * @param cc
     * @return
     */
    public static int bcd2Int(byte cc) {
        int n = cc >> 4;
        n *= 10;
        n += cc & 15;
        return n;
    }

    /**
     * 转为时间字符串，字节顺序为 年-月-日-时-分-秒
     *
     * @param buff
     * @param nStart
     * @param len
     * @return
     */
    public static String byte2TimeSmhdmy(byte[] buff, int nStart, int len) {

        int year = len < 7 ? LocalDate.now().getYear() / 100 * 100 : 0;
        return byte2BCDTime(buff, nStart, len, year);
    }

    public static String byte2BCDTime(byte[] buff, int nStart, int len, int baseYear) {
        int[] ns = new int[7];
        int start = 0;
        if (len == 7 && buff[nStart] != 0) {
            baseYear = bcd2Int(buff[nStart]) * 100;
            start++;
        }
        for (int i = 1; i <= len; ++i) {
            ns[i] = bcd2Int(buff[nStart + i - 1]);
        }

        String strTime = String.format("%02d-%02d-%02d %02d:%02d:%02d", ns[1] + baseYear, ns[2], ns[3], ns[4], ns[5],
                ns[6]);
        return strTime;
    }

    /**
     * 字节转字符串
     *
     * @param buff
     * @param nStart
     * @param nLen
     * @return
     */
    public static String bytesLf2String(byte[] buff, int nStart, int nLen) {
        char[] hexChars = new char[nLen * 2];
        for (int j = 0; j < nLen; j++) {
            int v = buff[nStart + nLen - j - 1] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 校验和
     *
     * @param cs
     * @param nStart
     * @param nLen
     * @return
     */
    public static byte calcSum(byte[] cs, int nStart, int nLen) {
        return (byte) calcSumInt(cs, nStart, nLen);
    }

    public static int calcSumInt(byte[] cs, int nStart, int nLen) {
        int nSum = 0;

        for (int i = nStart; i < nLen + nStart; ++i) {
            nSum += cs[i];
        }

        return nSum;
    }

    /**
     * byte[] -> int
     *
     * @param buff
     * @param nStart
     * @param nLen
     * @return
     */
    public static int bytes2Int(byte[] buff, int nStart, int nLen) {
        try {
            int nRet = 0;

            for (int i = 0; i < nLen; ++i) {
                nRet <<= 8;
                nRet += buff[nStart + i] & 255;
            }

            return nRet;
        } catch (Exception var5) {
            var5.printStackTrace();
            return -1;
        }
    }

    /**
     * byte[] -> int 小端模式
     *
     * @param buff
     * @param nStart
     * @param nLen
     * @return
     */
    public static int bytes2IntSmall(byte[] buff, int nStart, int nLen) {
        try {
            int nRet = 0;

            for (int i = 0; i < nLen; ++i) {
                nRet <<= 8;
                nRet += buff[nStart + nLen - i - 1] & 255;
            }

            return nRet;
        } catch (Exception var5) {
            var5.printStackTrace();
            return -1;
        }
    }

    /**
     * byte[] -> 无符号 int (long)
     *
     * @param buff
     * @param nStart
     * @param nLen
     * @return
     */
    public static long bytes2UInt(byte[] buff, int nStart, int nLen) {
        long nRet = 0L;

        for (int i = 0; i < nLen; ++i) {
            nRet <<= 8;
            nRet += (long) (buff[nStart + i] & 0xFF);
        }

        return nRet;
    }

    /**
     * byte[] -> 无符号 int (long) 小端模式
     *
     * @param buff
     * @param nStart
     * @param nLen
     * @return
     */
    public static long bytes2UIntSmall(byte[] buff, int nStart, int nLen) {
        long nRet = 0L;

        for (int i = 0; i < nLen; ++i) {
            nRet <<= 8;
            nRet += buff[nStart + nLen - i - 1] & 255;
        }
        return nRet;
    }

    /**
     * byte[]转hex字符串
     *
     * @param buff
     * @param nStart
     * @param nLen
     * @return
     */
    public static String bytes2String(byte[] buff, int nStart, int nLen) {
        if (buff == null) return "";
        char[] hexChars = new char[nLen * 2];
        for (int j = 0; j < nLen; j++) {
            int v = buff[j + nStart] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * BCD转字符串
     *
     * @param buff
     * @param nStart
     * @param nLen
     * @return
     */
    public static String bcb2String(byte[] buff, int nStart, int nLen) {
        StringBuilder sb = new StringBuilder(nLen * 2);
        for (int i = 0; i < nLen; i++) {
            sb.append((byte) ((buff[i + nStart] & 0xf0) >>> 4));
            sb.append((byte) (buff[i + nStart] & 0x0f));
        }
        String s = sb.toString().substring(0, 1).equalsIgnoreCase("0") ? sb.toString().substring(1) : sb.toString();
        return Strings.alignRight(s, 4, '0');
    }

    /**
     * 8421
     *
     * @功能: BCD码转为10进制串(阿拉伯数据)
     * @参数: BCD码
     * @结果: 10进制串
     * <p>
     * 例如：“11”---> 3131
     */
    public static String bcd2Str(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
                .toString().substring(1) : temp.toString();
    }

    public static void int32ToBytes(long nIn, byte[] buff, int nStart, int nLen) {
        for (int i = 0; i < nLen; ++i) {
            buff[nStart + nLen - 1 - i] = (byte) ((int) (nIn & 255L));
            nIn >>= 8;
        }

    }

    /**
     * 常见CRC参数模型如下:
     * <table>
     *             <tr>
     *                 <th>CRC算法名称</th>
     *                 <th>多项式公式</th>
     *                 <th>宽度</th>
     *                 <th>多项式</th>
     *                 <th>初始值</th>
     *                 <th>结果异或值</th>
     *                 <th>输入反转</th>
     *                 <th>输出反转</th>
     *             </tr>
     *             <tr>
     *                 <td>CRC-16/IBM</td>
     *                 <td>x<sup>16</sup> + x<sup>15</sup> + x<sup>2</sup> + 1</td>
     *                 <td>16</td>
     *                 <td>8005</td>
     *                 <td>0000</td>
     *                 <td>0000</td>
     *                 <td>true</td>
     *                 <td>true</td>
     *             </tr>
     *             <tr>
     *                 <td>CRC-16/MAXIM</td>
     *                 <td>x<sup>16</sup> + x<sup>15</sup> + x<sup>2</sup> + 1</td>
     *                 <td>16</td>
     *                 <td>8005</td>
     *                 <td>0000</td>
     *                 <td>FFFF</td>
     *                 <td>true</td>
     *                 <td>true</td>
     *             </tr>
     *             <tr>
     *                 <td>CRC-16/USB</td>
     *                 <td>x<sup>16</sup> + x<sup>15</sup> + x<sup>2</sup> + 1</td>
     *                 <td>16</td>
     *                 <td>8005</td>
     *                 <td>FFFF</td>
     *                 <td>FFFF</td>
     *                 <td>true</td>
     *                 <td>true</td>
     *             </tr>
     *             <tr>
     *                 <td>CRC-16/MODBUS</td>
     *                 <td>x<sup>16</sup> + x<sup>15</sup> + x<sup>2</sup> + 1</td>
     *                 <td>16</td>
     *                 <td>8005</td>
     *                 <td>FFFF</td>
     *                 <td>0000</td>
     *                 <td>true</td>
     *                 <td>true</td>
     *             </tr>
     *             <tr>
     *                 <td>CRC-16/CCITT</td>
     *                 <td>x<sup>16</sup> + x<sup>12</sup> + x<sup>5</sup> + 1</td>
     *                 <td>16</td>
     *                 <td>1021</td>
     *                 <td>0000</td>
     *                 <td>0000</td>
     *                 <td>true</td>
     *                 <td>true</td>
     *             </tr>
     *
     *
     *             <tr>
     *                 <td>CRC-16/XMODEM</td>
     *                 <td>x<sup>16</sup> + x<sup>12</sup> + x<sup>5</sup> + 1</td>
     *                 <td>16</td>
     *                 <td>1021</td>
     *                 <td>0000</td>
     *                 <td>0000</td>
     *                 <td>false</td>
     *                 <td>false</td>
     *             </tr>
     *     </table>
     * <p>
     * <p>
     * 当前对应的是 CRC-16/IBM
     *
     * @param pd
     * @param nNum
     * @return
     */
    public static int crc16(byte[] pd, int nNum) {
        return crc16(pd, 0, nNum);
    }

    /**
     * 当前对应的是 CRC-16/IBM
     *
     * @param bytes 数据源
     * @param start 开始
     * @param len   长度
     * @return
     */
    public static int crc16(byte[] bytes, int start, int len) {
        int res = 0x0000;
        // 0x8005 位反转结果就是 0xA001
        int POLYNOMIAL = 0xA001;
        if (len < 0) {
            len = bytes.length;
        }
        if (start + len > bytes.length) {
            len = bytes.length - start;
        }
        for (int j = start; j < start + len; j++) {
            byte data = bytes[j];
            res = res ^ data;
            for (int i = 0; i < 8; i++) {
                res = (res & 0x0001) == 1 ? (res >> 1) ^ POLYNOMIAL : res >> 1;
            }
        }
        return res;
    }

    /**
     * CRC Modbus
     * 多项式：x<sup>16</sup> + x<sup>12</sup> + x<sup>5</sup> + 1
     *
     * @param bytes
     * @param start
     * @param len
     * @return
     */
    public static int crc16Modbus(byte[] bytes, int start, int len) {
        int res = 0xFFFF;
        // 0x8005 位反转结果就是 0xA001
        int POLYNOMIAL = 0xA001;
        if (len < 0) {
            len = bytes.length;
        }
        if (start + len > bytes.length) {
            len = bytes.length - start;
        }
        for (int j = start; j < start + len; j++) {
            byte data = bytes[j];
            res = res ^ (data & 0x00FF);
            for (int i = 0; i < 8; i++) {
                res = (res & 0x0001) == 1 ? (res >> 1) ^ POLYNOMIAL : res >> 1;
            }
        }
        return res;
    }

    /**
     * crc Xmodem
     * 多项式：x<sup>16</sup> + x<sup>15</sup> + x<sup>2</sup> + 1
     *
     * @param bytes
     * @param offset
     * @param count
     * @return
     */
    public static int crc16Xmodem(byte[] bytes, int offset, int count) {
        int crc = 0x0000; // initial value
        int polynomial = 0x1021; // poly value
        for (int index = 0; index < count; index++) {
            byte b = bytes[offset + index];
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit)
                    crc ^= polynomial;
            }
        }
        crc &= 0xffff;
        return crc;
    }

    public static int string2Bytes(String str, byte[] buff, int nStart) {
        int nLen = 0;
        int nDone = 0;
        int cVal = 0;
        str = str.toUpperCase();

        for (int i = 0; i < str.length(); ++i) {
            char cc = str.charAt(i);
            if (cc >= '0' && cc <= '9') {
                cVal <<= 4;
                cVal += cc - 48;
                ++nDone;
            } else {
                if (cc < 'A' || cc > 'F') {
                    continue;
                }

                cVal <<= 4;
                cVal += cc - 65 + 10;
                ++nDone;
            }

            cVal &= 255;
            if (nDone > 1) {
                buff[nStart + nLen] = (byte) (cVal & 255);
                ++nLen;
                nDone = 0;
                cVal = 0;
            }
        }

        if (nDone > 0) {
            buff[nLen + nStart] = (byte) (cVal & 255);
            ++nLen;
        }

        return nLen;
    }

    /**
     * 字节数组转16进制字符串
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        if (null == bytes) {
            return null;
        }
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 16进制字符串转字节数组
     *
     * @param hexString
     * @return
     */
    public static byte[] hexToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.startsWith("0x") ? hexString.substring(2, hexString.length()) : hexString;
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toUpperCase().toCharArray();
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            // 两个字符对应一个byte
            int pos = i * 2;
            int h = Arrays.binarySearch(HEX_ARRAY, hexChars[pos]) << 4;
            int l = Arrays.binarySearch(HEX_ARRAY, hexChars[pos + 1]);
            // 非16进制字符
            if (h == -1 || l == -1) {
                return null;
            }
            bytes[i] = (byte) (h | l);
        }
        return bytes;
    }

    /**
     * 将字节转为位数组，数组中的每个元素代表每一位的值（即值只会为 0或1）
     *
     * @param s
     * @return
     */
    public static byte[] bitToByte(String s) {
        byte[] bytes = hexToBytes(s);
        return bitToByte(bytes, 0, bytes.length);
    }

    public static byte[] bitToByte(byte[] bytes, int start, int len) {
        if (len > bytes.length) {
            len = bytes.length;
        }
        byte[] flag = new byte[len * 8];
        int index = 0;
        for (int i = len - 1; i >= 0; --i) {
            for (int j = 0; j < 8; ++j) {
                flag[index++] = (byte) (bytes[i + start] & 1);
                bytes[i + start] = (byte) (bytes[i + start] >> 1);
            }
        }

        return flag;
    }

    public static String parseIp2Str(byte[] data, int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int p = i + start;
            if (p < data.length) {
                sb.append(data[p] & 0xFF);
            } else {
                sb.append(0);
            }
            if (i < 3) {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    public static byte[] parseStr2IpBytes(String ipStr) {
        if (ipStr == null) {
            return new byte[4];
        }
        String[] strs = ipStr.split("\\.");
        byte[] ipBytes = new byte[4];
        int len = Math.min(strs.length, ipBytes.length);
        for (int i = 0; i < len; i++) {
            try {
                ipBytes[i] = Integer.valueOf(strs[i]).byteValue();
            } catch (Exception e) {

            }
        }
        return ipBytes;
    }

    public static String pkcs7padding(String data) {
        int bs = 16;
        int padding = bs - (data.length() % bs);
        String padding_text = "";
        for (int i = 0; i < padding; i++) {
            padding_text += (char) padding;
        }
        return data + padding_text;
    }

    /**
     * 获取一个字节的某一位的值
     *
     * @param b     字节
     * @param index 位
     * @return 返回指定位的值，0 或 1
     */
    public static int getByteBit(byte b, int index) {
        return ((b >> index) & 0x1);
    }

    /**
     * 获取一个字节某几位
     * @param b 字节
     * @param start 开始位置
     * @param length 长度
     * @return 10进制int
     */
    public static int getBits(byte b,int start,int length) {
        int bit = (int)((b>>start)&(0xFF>>(8-length)));
        return bit;
    }

    /**
     * 从后往前将字节数组添加到 ByteBuffer 中去
     *
     * @param buffer
     * @param bytes
     */
    public static void putBufferBytesReverse(ByteBuffer buffer, byte[] bytes) {
        for (int i = bytes.length - 1; i >= 0; i--) {
            buffer.put(bytes[i]);
        }
    }

    public static byte bit2byte(String bit8) {
        int re, len;
        len = bit8.length();
        if (len == 8) {// 8 bit处理
            if (bit8.charAt(0) == '0') {// 正数
                re = Integer.parseInt(bit8, 2);
            } else {// 负数
                re = Integer.parseInt(bit8, 2) - 256;
            }
        } else {//4 bit处理
            re = Integer.parseInt(bit8, 2);
        }
        return (byte) re;
    }

    public static String hexToBinStr(String hexString) {
        if (hexString != null && hexString.length() % 2 == 0) {
            StringBuilder bString = new StringBuilder();
            for (int i = 0; i < hexString.length(); i += 2) {
                String tmp = Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 2), 16));
                bString.append(Strings.cutRight(tmp, 8, '0'));
            }
            return bString.toString();
        } else {
            return null;
        }
    }

    /**
     * 将字节数组转为对应的 8 位的二进制字符串
     *
     * @param bytes
     * @return
     */
    public static String bytes28bits(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(byte28bit(b));
        }
        return sb.toString();
    }

    /**
     * 字节转二进制的 8 位字符串
     *
     * @param b
     * @return
     */
    public static String byte28bit(byte b) {
        return Strings.cutRight(Integer.toBinaryString(b), 8, '0');
    }

    /**
     * 截取字节的从 from 到 to 的位转为 int
     * 注意一个字节是 8 位，例如：1001 0110
     * 最左边的是第7位，最右边的是第0位， bit2Int(b,7,6); 返回的就是 0b10 转成10进制值为 2
     *
     * @param b    字节
     * @param from 开始位数（0开始7结束）
     * @param to   结束位数（0开始7结束）
     * @return
     */
    public static int bit2Int(byte b, int from, int to) {
        if (from > 7 || to > 7 || from < 0 || to < 0) {
            throw new IllegalArgumentException("参数错误，from 和 to 参数范围均应为 0-7");
        }
        String bit = byte28bit(b);
        if (from < to) {
            int tmp = from;
            from = to;
            to = tmp;
        }
        return Integer.parseInt(bit.substring(7 - from, 7 - to + 1), 2);
    }

    /**
     * 截取字节数组并返回float
     *
     * @param originalArray 原始字节数组
     * @param srcPos        起始位置
     * @return
     */
    public static float byte2Float(byte[] originalArray, int srcPos) {
        ByteBuffer buf = ByteBuffer.allocateDirect(4);
        byte[] bytes = new byte[4];
        System.arraycopy(originalArray, srcPos, bytes, 0, 4);
        buf.put(bytes);
        buf.rewind();
        return buf.getFloat();
    }

    public static float byte2FloatSmall(byte[] originalArray, int srcPos) {
        ByteBuffer buf = ByteBuffer.allocateDirect(4);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        byte[] bytes = new byte[4];
        System.arraycopy(originalArray, srcPos, bytes, 0, 4);
        buf.put(bytes);
        buf.rewind();
        return buf.getFloat();
    }

    /**
     * 截取字节数组并返回float
     *
     * @param originalArray 原始字节数组
     * @param srcPos        起始位置
     * @return
     */
    public static String interceptByte2Hex(byte[] originalArray, int srcPos) {
        ByteBuffer buf = ByteBuffer.allocateDirect(4);
        byte[] bytes = new byte[2];
        System.arraycopy(originalArray, srcPos, bytes, 0, 2);
        buf.put(bytes);
        buf.rewind();
        return bytes2String(bytes, 0, bytes.length);
    }

    /**
     * @description 截取字节数组并返回新数组
     * @author zsmiao
     * @date 2021/7/2
     */
    public static byte[] interceptByteArray(byte[] originalArray, int srcPos, int length) {
        byte[] bytes = new byte[length];
        System.arraycopy(originalArray, srcPos, bytes, 0, length);
        return bytes;
    }

    /**
     * @param b     字节数组
     * @param index 起始位置
     * @return 浮点数
     * @description 字节数组转浮点数
     * @author zsmiao
     * @date 2021/7/8
     */
    public static float getFloat(byte[] b, int index) {
        int l;
        l = b[index];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }

    /**
     * @param b     字节数组
     * @param index 起始位置
     * @return 浮点数
     */
    public static double getDouble(byte[] b, int index) {
        long l;
        l = b[index];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        l &= 0xffffffff;
        l |= ((long) b[index + 4] << 32);
        l &= 0xffffffffffL;
        l |= ((long) b[index + 5] << 40);
        l &= 0xffffffffffffL;
        l |= ((long) b[index + 6] << 48);
        l &= 0xffffffffffffffL;
        l |= ((long) b[index + 7] << 56);
        return Double.longBitsToDouble(l);
    }

    /**
     * 从字节数组的index处的连续8个字节获得一个long
     * @param bytes 字节数字
     * @param index 下标
     * @return long
     */
    public static long getLong(byte[] bytes, int index) {
        return (0xff00000000000000L & ((long) bytes[index] << 56)) |

                (0x00ff000000000000L & ((long) bytes[index + 1] << 48)) |

                (0x0000ff0000000000L & ((long) bytes[index + 2] << 40)) |

                (0x000000ff00000000L & ((long) bytes[index + 3] << 32)) |

                (0x00000000ff000000L & ((long) bytes[index + 4] << 24)) |

                (0x0000000000ff0000L & ((long) bytes[index + 5] << 16)) |

                (0x000000000000ff00L & ((long) bytes[index + 6] << 8)) |

                (0x00000000000000ffL & (long) bytes[index + 7]);

    }

    /**
     * 高低位进制互换
     * @param hex 十六进制字符串
     * @return 字符串
     */
    public static String reverseHex(String hex) {
        if (hex.length() == 3) {
            hex = "0" + hex;
        }
        final char[] charArray = hex.toCharArray();
        final int length = charArray.length;
        final int times = length / 2;
        for (int c1i = 0; c1i < times; c1i += 2) {
            final int c2i = c1i + 1;
            final char c1 = charArray[c1i];
            final char c2 = charArray[c2i];
            final int c3i = length - c1i - 2;
            final int c4i = length - c1i - 1;
            charArray[c1i] = charArray[c3i];
            charArray[c2i] = charArray[c4i];
            charArray[c3i] = c1;
            charArray[c4i] = c2;
        }
        return new String(charArray);
    }

    /**
     * 生成累加和
     * @param data 十六进制字符串
     * @return 生成累加和字符串
     */
    public static String makeChecksum(String data) {
        if (data == null || data.equals("")) {
            return "";
        }
        int total = 0;
        int len = data.length();
        int num = 0;
        while (num < len) {
            String s = data.substring(num, num + 2);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        //用256求余最大是255，即16进制的FF
        int mod = total % 256;
        String hex = Integer.toHexString(mod);
        len = hex.length();
        // 如果不够校验位的长度，补0,这里用的是两位校验
        if (len < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * BCD 类型
     */
    public static final int BCD_8421 = 0;
    public static final int BCD_5421 = 1;
    public static final int BCD_2421 = 2;

    /**
     * BCD 权
     */
    public static final int[][] BCD_WEIGHT = new int[][]{
            {8, 4, 2, 1},
            {5, 4, 2, 1},
            {2, 4, 2, 1}
    };

    public static String bytes2bcd8421(byte[] bytes, int start, int len) {
        return bytes2bcd(bytes, BCD_8421, start, len);
    }

    public static String bytes2bcd5421(byte[] bytes, int start, int len) {
        return bytes2bcd(bytes, BCD_5421, start, len);
    }

    public static String bytes2bcd2421(byte[] bytes, int start, int len) {
        return bytes2bcd(bytes, BCD_2421, start, len);
    }

    public static String bytes2bcd(byte[] bytes, int bcdType, int start, int len) {
        if (bcdType > 2 || bcdType < 0) {
            throw new IllegalArgumentException("bcdType must be 0、1 or 2");
        }
        int[] weight = BCD_WEIGHT[bcdType];
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < start + len; i++) {
            byte b = bytes[i];
            String[] chars = byte28bit(b).split("");
            for (int j = 0; j < 2; j++) {
                int v = 0;
                for (int k = 0; k < 4; k++) {
                    v += weight[k] * Integer.parseInt(chars[k + 4 * j]);
                }
                sb.append(v);
            }
        }
        return sb.toString();
    }


    public static String int2Binary(int value, int bitNum) {
        String str = Integer.toBinaryString(value);
        while (str.length() < bitNum) {
            str = "0" + str;
        }
        return str;
    }

    /**
     * 十六进制转二进制
     * @param hexString 十六进制字符串
     * @return 二进制字符串
     */
    public static String hex16To2String(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    //二制转十六进制
    public static String hex2To16String(String bString) {
        if (bString == null || bString.equals("") || bString.length() % 8 != 0)
            return null;
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }

    /**
     * 字节转Long  指定大小端 默认大端 false
     *
     * @param data
     * @param offset
     * @param littleEndian
     * @return
     */
    public static long bytesToLong(byte[] data, int offset, boolean littleEndian) {
        ByteBuffer buff = ByteBuffer.wrap(data, offset, 8);
        if (littleEndian) {
            buff.order(ByteOrder.LITTLE_ENDIAN);
        }
        return buff.getLong();
    }

    public static long bytesToULong(byte[] data, int offset, boolean littleEndian) {
        ByteBuffer buff = ByteBuffer.wrap(data, offset, 8);
        if (littleEndian) {
            buff.order(ByteOrder.LITTLE_ENDIAN);
        }
        return buff.getLong();
    }

    public static long bytesToLong(byte[] bytes, ByteOrder byteOrder) {
        long values = 0;
        if (ByteOrder.LITTLE_ENDIAN == byteOrder) {
            for (int i = (bytes.length - 1); i >= 0; i--) {
                values <<= Byte.SIZE;
                values |= (bytes[i] & 0xff);
            }
        } else {
            for (int i = 0; i < bytes.length; i++) {
                values <<= Byte.SIZE;
                values |= (bytes[i] & 0xff);
            }
        }

        return values;
    }

    /**
     * 字节转float  指定大小端 默认大端 false
     *
     * @param data
     * @param offset
     * @param littleEndian
     * @return
     */
    public static float bytesToFloat(byte[] data, int offset, boolean littleEndian) {
        ByteBuffer buff = ByteBuffer.wrap(data, offset, 4);
        if (littleEndian) {
            buff.order(ByteOrder.LITTLE_ENDIAN);
        }
        return buff.getFloat();
    }

    /**
     * 字节转double 指定大小端 默认大端 false
     *
     * @param data
     * @param offset
     * @param littleEndian
     * @return
     */
    public static double bytesToDouble(byte[] data, int offset, boolean littleEndian) {
        ByteBuffer buff = ByteBuffer.wrap(data, offset, 8);
        if (littleEndian) {
            buff.order(ByteOrder.LITTLE_ENDIAN);
        }
        return buff.getDouble();
    }


    /**
     * 将二进制小数转化为十进制
     *
     * @param binXStr 二进制小数
     * @return 十进制小数
     */
    public static double bin2DecXiao(String binXStr) {
        double decX = 0.0;
        //位数
        int k = 0;
        for (int i = 0; i < binXStr.length(); i++) {
            int exp = binXStr.charAt(i) - '0';
            exp = -(i + 1) * exp;
            if (exp != 0) {
                decX += Math.pow(2, exp);
            }
        }
        return decX;
    }


    /**
     * 两个字节数组合并为一个字节数组
     * @param bt1 字节数组1
     * @param bt2 字节数组2
     * @return byte[]
     */
    public static byte[] byteConcat(byte[] bt1, byte[] bt2) {
        byte[] bytes = new byte[bt1.length + bt2.length];
        int len = 0;
        System.arraycopy(bt1, 0, bytes, 0, bt1.length);
        len += bt1.length;
        System.arraycopy(bt2, 0, bytes, len, bt2.length);
        return bytes;
    }


    /**
     * ascii 转bcd码
     */
    public static byte[] ascToBcd(byte[] ascii) {
        byte[] bcd = new byte[ascii.length / 2];
        int j = 0;
        for (int i = 0; i < (ascii.length + 1) / 2; i++) {
            bcd[i] = ascToBcd(ascii[j++]);
            bcd[i] = (byte) (((j >= ascii.length) ? 0x00 : ascToBcd(ascii[j++])) + (bcd[i] << 4));
        }
        return bcd;
    }

    private static byte ascToBcd(byte asc) {
        byte bcd;

        if ((asc >= '0') && (asc <= '9')) {
            bcd = (byte) (asc - '0');
        } else if ((asc >= 'A') && (asc <= 'F')) {
            bcd = (byte) (asc - 'A' + 10);
        } else if ((asc >= 'a') && (asc <= 'f')) {
            bcd = (byte) (asc - 'a' + 10);
        } else {
            bcd = (byte) (asc - 48);
        }
        return bcd;
    }

    /**
     * String 转bcd码
     */
    public static byte[] strToBcd(String asc) {
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

    public static int getUnsignedByte(byte data) {
        return data & 0xFF;
    }


}
