package com.wondertek.mam.util.superutil.j2se;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class MathUtils {
	/**
     * 返回科学计算后的乘法结果
     * @param val1
     * @param val2
     * @param mc    精度
     * @return
     */
    public static double multiply(double val1,double val2,int mc) {
        if(val1==0 || val2==0) return 0;
        BigDecimal bg1 = new BigDecimal(val1);
        BigDecimal bg2 = new BigDecimal(val2);
        BigDecimal result = new BigDecimal(0);
        result = bg1.multiply(bg2, new MathContext(mc,RoundingMode.HALF_UP));
        
        return result.doubleValue();
    }
    /**
     * 返回科学计算后的除法结果
     * @param fz  分子
     * @param fm  分母
     * @param mc    精度
     * @return
     */
    public static double divide(double fz,double fm,int mc) {
        if(fz==0 || fm==0) return 0;
        BigDecimal bg1 = new BigDecimal(fz);
        BigDecimal bg2 = new BigDecimal(fm);
        BigDecimal result = new BigDecimal(0);
        
        result = bg1.divide(bg2, new MathContext(mc,RoundingMode.HALF_UP));
        
        return result.doubleValue();
    }
    
    /**
     * 返回科学计算后的减法结果
     * @param val1
     * @param val2
     * @param mc    精度
     * @return
     */
    public static double subtract(double val1,double val2,int mc) {
        if(val2==0) return val1;
        BigDecimal bg1 = new BigDecimal(val1);
        BigDecimal bg2 = new BigDecimal(val2);
        BigDecimal result = new BigDecimal(0);
        
        result = bg1.subtract(bg2, new MathContext(mc,RoundingMode.HALF_UP));
        
        return result.doubleValue();
    }
    
    /**
     * 返回科学计算后的加法结果
     * @param val1  
     * @param val2
     * @param mc    精度
     * @return
     */
    public static double add(double val1,double val2,int mc) {
        BigDecimal bg1 = new BigDecimal(val1);
        BigDecimal bg2 = new BigDecimal(val2);
        BigDecimal result = new BigDecimal(0);
        
        result = bg1.add(bg2, new MathContext(mc,RoundingMode.HALF_UP));
        
        return result.doubleValue();
    }


    /**
     * 计算n！的值
     */
    public long getFactorial(int n) {
        // 因为当n大于17时，n!的值超过了long类型的范围，会出现错误。因此这里限定了n必须小于等于17。
        // 数学上没有负数的阶乘的概念，因此n必须大于等于0。
        if ((n < 0) || (n > 17)) {
            System.err.println("n的值范围必须在区间[0, 17]内！");
            return -1;
        } else if (n == 0) {
            // 0!的值为1
            return 1;
        } else {
            long result = 1;
            for (; n > 0; n--) {
                result *= n;
            }
            return result;
        }
    }
	
	
	public static void main(String[] args)
	{
		double v1=23456;
		double v2=123;
		System.out.println(MathUtils.divide(v2, v1, 4));
	}

}
