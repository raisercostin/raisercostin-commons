/*
 * Created on Nov 13, 2003
 */
package raiser.util;
import java.text.*;
/**
 * @author raiser
 */
public class Formatter
{
    public static String toHexString(int number, int digits)
    {
        boolean possitive = true;
        if(number < 0)
        {
            number = -number;
            possitive = false;
        }
        StringBuffer result = new StringBuffer(Integer.toHexString(number));
        if(result.length() < digits)
        {
            for(int i = digits - result.length() ; i > 0 ; i--)
            {
                result.insert(0,'0');
            }
        }
        if(!possitive)
        {
            result.insert(0,'-');
        }
        return result.toString();
    }
    public static String formatDouble(double value, int width, int decimals)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < width; i++)
        {
            sb.append('0');
        }
        if (decimals > 0)
        {
            sb.append('.');
            for (int i = 0; i < decimals; i++)
            {
                sb.append('0');
            }
        }
        formatter.applyPattern(sb.toString());
        return formatter.format(value);
    }

    public static String formatInteger(int value, int width)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < width; i++)
        {
            sb.append("0");
        }
        formatter.applyPattern(sb.toString());
        return formatter.format(value);
    }

    static DecimalFormat formatter = new DecimalFormat();
    /**
     * Method formatDouble.
     * @param double
     * @param i
     * @param i1
     * @return int
     */
    public static String formatDouble(Double value, int width, int decimals)
    {
        return formatDouble(value.doubleValue(),width,decimals);
    }
    /**
     * Method formatDouble.
     * @param double
     * @param pattern
     * @return Object
     */
    public static Object formatDouble(Double value, String pattern)
    {
        formatter.applyPattern(pattern);
        return formatter.format(value);
    }

    /**
     * Method formatDouble.
     * @param d
     * @param string
     * @return Object
     */
    public static Object formatDouble(double value, String pattern)
    {
        formatter.applyPattern(pattern);
        return formatter.format(value);
    }
    public static String paddLeft(String string, int length, char paddChar)
    {
        if(string.length()<length)
        {
            StringBuffer buffer = new StringBuffer();
            for (int i = length-string.length(); i > 0 ; i--)
            {
                buffer.append(paddChar);
            }
            buffer.append(string);
            string = buffer.toString();
        }
        return string;
    }
    public static String toString(byte value, int length)
    {
        return Formatter.paddLeft(Byte.toString(value),length,'0');
    }
    public static String toString(int value, int length)
    {
        return Formatter.paddLeft(Integer.toString(value),length,'0');
    }
    public static String toHexString(byte value)
    {
        return Integer.toHexString(value&0xff);
    }
    public static String toHexString(byte value, int length)
    {
        return Formatter.paddLeft(Integer.toHexString(value&0xff),length,'0');
    }
    public static String toBinString(byte value)
    {
        return Integer.toBinaryString(value&0xff);
    }
    public static String toBinString(int value)
    {
        return Integer.toBinaryString(value&0xffffffff);
    }
    public static String toBinString(byte value, int length)
    {
        return Formatter.paddLeft(toBinString(value),length,'0');
    }
    public static String toBinString(int value, int length)
    {
        return Formatter.paddLeft(toBinString(value),length,'0');
    }
    /**
     * @param ch
     * @return
     */
    public static String decode(int value)
    {
        return value+"("+(value>=32 ? (char)value:'*')+")";
    }
    public static String toChar(int value)
    {
        if((value<32)&&(value !=13)&&(value!=10))
            
            return "("+value+")";
        return Character.toString((char) value);
    }
}
