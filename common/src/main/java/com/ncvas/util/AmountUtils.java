package com.ncvas.util;

import java.math.BigDecimal;

public class AmountUtils {

	/** 金额为分的格式 */
	public static final String CURRENCY_FEN_REGEX = "\\-?[0-9]+.?[0-9]+";

	/**
	 * 将分为单位的转换为元并返回金额格式的字符串 （除100）
	 * 
	 * @param amount
	 * @return
	 * @throws Exception
	 */
	public static String changeF2Y(Long amount) throws Exception {
		if (!amount.toString().matches(CURRENCY_FEN_REGEX)) {
			throw new Exception("金额格式有误");
		}

		int flag = 0;
		String amString = amount.toString();
		if (amString.charAt(0) == '-') {
			flag = 1;
			amString = amString.substring(1);
		}
		StringBuffer result = new StringBuffer();
		if (amString.length() == 1) {
			result.append("0.0").append(amString);
		} else if (amString.length() == 2) {
			result.append("0.").append(amString);
		} else {
			String intString = amString.substring(0, amString.length() - 2);
			for (int i = 1; i <= intString.length(); i++) {
				if ((i - 1) % 3 == 0 && i != 1) {
					result.append(",");
				}
				result.append(intString.substring(intString.length() - i, intString.length() - i + 1));
			}
			result.reverse().append(".").append(amString.substring(amString.length() - 2));
		}
		if (flag == 1) {
			return "-" + result.toString();
		} else {
			return result.toString();
		}
	}

	/**
	 * 将分为单位的转换为元 （除100）
	 * 
	 * @param amount
	 * @return
	 * @throws Exception
	 */
	public static String changeF2Y(String amount,int newScale) {
		//构造以字符串内容为值的BigDecimal类型的变量bd
		BigDecimal bd=new BigDecimal(amount); 
		//设置小数位数，第一个变量是小数位数，第二个变量是取舍方法(四舍五入)   
		amount = bd.setScale(newScale, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(100)).toString();
		//如在方法进来的时候判断金额格式，肯定会报异常，因amount的数据有可能是：143.898E+7这种
		if (!amount.matches(CURRENCY_FEN_REGEX)) {
			try {
				throw new Exception("金额格式有误"+":" +amount);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return amount;
	}
	
	/**
	 * 将分为单位的转换为元 （除100）
	 * 
	 * @param amount
	 * @return
	 * @throws Exception
	 */
	public static String changeF2Y(String amount) throws Exception {
		
		//构造以字符串内容为值的BigDecimal类型的变量bd
		BigDecimal bd=new BigDecimal(amount); 
		//设置小数位数，第一个变量是小数位数，第二个变量是取舍方法(四舍五入)   
		amount = bd.setScale(2, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(100)).toString();
		//如在方法进来的时候判断金额格式，肯定会报异常，因amount的数据有可能是：143.898E+7这种
		if (!amount.matches(CURRENCY_FEN_REGEX)) {
			throw new Exception("金额格式有误"+":" +amount);
		}
		return amount;
		//return BigDecimal.valueOf(Long.valueOf(amount)).divide(new BigDecimal(100)).toString();
	}

	/**
	 * 将元为单位的转换为分 （乘100）
	 * 
	 * @param amount
	 * @return
	 */
	public static String changeY2F(Long amount) {
		return BigDecimal.valueOf(amount).multiply(new BigDecimal(100)).toString();
	}

	/**
	 * 将元为单位的转换为分 替换小数点，支持以逗号区分的金额
	 * 
	 * @param amount
	 * @return
	 */
	public static String changeY2F(String amount) {
		String currency = amount.replaceAll("\\$|\\￥|\\,", ""); // 处理包含, ￥
																// 或者$的金额
		int index = currency.indexOf(".");
		int length = currency.length();
		Long amLong = 0l;
		if (index == -1) {
			amLong = Long.valueOf(currency + "00");
		} else if (length - index >= 3) {
			amLong = Long.valueOf((currency.substring(0, index + 3)).replace(".", ""));
		} else if (length - index == 2) {
			amLong = Long.valueOf((currency.substring(0, index + 2)).replace(".", "") + 0);
		} else {
			amLong = Long.valueOf((currency.substring(0, index + 1)).replace(".", "") + "00");
		}
		return amLong.toString();
	}

	/**
	 * @param amount
	 * @param digits 保留小数位
     * @return
     */
	public static String formatNumber(String amount,int digits) {
		if ((amount == null) || ("".equals(amount.trim())))amount="0";
		BigDecimal bd = new BigDecimal(Double.parseDouble(amount));
		double num =bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return String.valueOf(num);
	}
	/**
	 * 按照位数前面补0
	 * @param number
	 * @param len 数字补0后的总长度
	 * @return
	 */
	public static String fixNumberAddPre0(String number,int len) {
		String str1 = String.format("%0"+String.valueOf(len)+"d", Integer.valueOf(number));
		return str1;
	}
	/**
	 * @param number
	 * @return
	 */
	public static String fixNumberRemovePre0(String number) {
		String str1 =number.replaceFirst("^0*", "");
		return str1;
	}
	public static void main(String[] args) {
		/*String str="86.64566666";
		BigDecimal bd = new BigDecimal(Double.parseDouble(str));
		System.out.println(bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
		System.out.println("=================");
		DecimalFormat df = new DecimalFormat("#.00");
		System.out.println(df.format(Double.parseDouble(str)));
		System.out.println("=================");
		System.out.println(String.format("%.2f", Double.parseDouble(str)));
		System.out.println("=================");
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		System.out.println(nf.format(Double.parseDouble(str)));*/
		String str = "000000000000000050580";
		System.out.println(fixNumberRemovePre0(str));
//补零操作  10表示补零后字符串的长度为10
		String str1 =fixNumberAddPre0("50580",6);
		System.out.println(str1);
	}
}
