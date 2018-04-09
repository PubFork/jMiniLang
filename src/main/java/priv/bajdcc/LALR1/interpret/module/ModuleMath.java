package priv.bajdcc.LALR1.interpret.module;

import priv.bajdcc.LALR1.grammar.Grammar;
import priv.bajdcc.LALR1.grammar.runtime.*;
import priv.bajdcc.util.ResourceLoader;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

/**
 * 【模块】数学模块
 *
 * @author bajdcc
 */
public class ModuleMath implements IInterpreterModule {

	private static ModuleMath instance = new ModuleMath();
	private RuntimeCodePage runtimeCodePage;

	public static ModuleMath getInstance() {
		return instance;
	}

	final static BigInteger HUNDRED = BigInteger.valueOf(100);
	final static BigDecimal PI2 = BigDecimal.valueOf(Math.PI * 2.0);
	final static private Random rand = new Random();

	@Override
	public String getModuleName() {
		return "sys.math";
	}

	@Override
	public String getModuleCode() {
		return ResourceLoader.load(getClass());
	}

	@Override
	public RuntimeCodePage getCodePage() throws Exception {
		if (runtimeCodePage != null)
			return runtimeCodePage;

		String base = ResourceLoader.load(getClass());

		Grammar grammar = new Grammar(base);
		RuntimeCodePage page = grammar.getCodePage();
		IRuntimeDebugInfo info = page.getInfo();
		info.addExternalValue("g_PI", () -> new RuntimeObject(BigDecimal.valueOf(Math.PI)));
		info.addExternalValue("g_PI_2", () -> new RuntimeObject(BigDecimal.valueOf(Math.PI * 2.0)));
		info.addExternalValue("g_E", () -> new RuntimeObject(BigDecimal.valueOf(Math.E)));
		info.addExternalValue("g_random", () -> new RuntimeObject(BigDecimal.valueOf(rand.nextDouble())));
		buildUnaryFunc(info);

		return runtimeCodePage = page;
	}

	private void buildUnaryFunc(IRuntimeDebugInfo info) {
		info.addExternalFunc("g_sqrt", new ModuleMathUnaryFunc("开方",
				ModuleMathUnaryFunc.ModuleMathUnaryFuncType.kSqrt));
		info.addExternalFunc("g_sqrt_double", new ModuleMathUnaryFunc("开方",
				ModuleMathUnaryFunc.ModuleMathUnaryFuncType.kSqrtDouble));
		info.addExternalFunc("g_cos", new ModuleMathUnaryFunc("余弦",
				ModuleMathUnaryFunc.ModuleMathUnaryFuncType.kCos));
		info.addExternalFunc("g_sin", new ModuleMathUnaryFunc("正弦",
				ModuleMathUnaryFunc.ModuleMathUnaryFuncType.kSin));
		info.addExternalFunc("g_floor", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "四舍五入";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kReal, RuntimeObjectType.kInt};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				BigDecimal decimal = (BigDecimal) args.get(0).getObj();
				BigInteger n = (BigInteger) args.get(1).getObj();
				return new RuntimeObject(decimal.setScale(n.intValue(), BigDecimal.ROUND_HALF_UP));
			}
		});
		info.addExternalFunc("g_atan2", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "atan(y, x)";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kReal, RuntimeObjectType.kReal};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				BigDecimal y = (BigDecimal) args.get(0).getObj();
				BigDecimal x = (BigDecimal) args.get(1).getObj();
				return new RuntimeObject(BigDecimal.valueOf(Math.atan2(y.doubleValue(), x.doubleValue())));
			}
		});
		info.addExternalFunc("g_random_int", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "随机数";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kInt};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				return new RuntimeObject(BigInteger.valueOf(rand.nextInt(((BigInteger) args
						.get(0).getObj()).intValue())));
			}
		});
	}

	public static BigDecimal sqrt(BigDecimal number, int scale, int roundingMode) {
		if (number.compareTo(BigDecimal.ZERO) < 0)
			throw new ArithmeticException("sqrt with negative");
		BigInteger integer = number.toBigInteger();
		StringBuilder sb = new StringBuilder();
		String strInt = integer.toString();
		int lenInt = strInt.length();
		if (lenInt % 2 != 0) {
			strInt = '0' + strInt;
			lenInt++;
		}
		BigInteger res = BigInteger.ZERO;
		BigInteger rem = BigInteger.ZERO;
		for (int i = 0; i < lenInt / 2; i++) {
			res = res.multiply(BigInteger.TEN);
			rem = rem.multiply(HUNDRED);
			BigInteger temp = new BigInteger(strInt.substring(i * 2, i * 2 + 2));
			rem = rem.add(temp);
			BigInteger j = BigInteger.TEN;
			while (j.compareTo(BigInteger.ZERO) > 0) {
				j = j.subtract(BigInteger.ONE);
				if (((res.add(j)).multiply(j)).compareTo(rem) <= 0) {
					break;
				}
			}
			res = res.add(j);
			rem = rem.subtract(res.multiply(j));
			res = res.add(j);
			sb.append(j);
		}
		sb.append('.');
		BigDecimal fraction = number.subtract(number.setScale(0,
				BigDecimal.ROUND_DOWN));
		int fracLen = (fraction.scale() + 1) / 2;
		fraction = fraction.movePointRight(fracLen * 2);
		String strFrac = fraction.toPlainString();
		for (int i = 0; i <= scale; i++) {
			res = res.multiply(BigInteger.TEN);
			rem = rem.multiply(HUNDRED);
			if (i < fracLen) {
				BigInteger temp = new BigInteger(strFrac.substring(i * 2,
						i * 2 + 2));
				rem = rem.add(temp);
			}
			BigInteger j = BigInteger.TEN;
			while (j.compareTo(BigInteger.ZERO) > 0) {
				j = j.subtract(BigInteger.ONE);
				if (((res.add(j)).multiply(j)).compareTo(rem) <= 0) {
					break;
				}
			}
			res = res.add(j);
			rem = rem.subtract(res.multiply(j));
			res = res.add(j);
			sb.append(j);
		}
		return new BigDecimal(sb.toString()).setScale(scale, roundingMode);
	}

	public static BigDecimal sqrt(BigDecimal number, int scale) {
		return sqrt(number, scale, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal sqrt(BigDecimal number) {
		int scale = number.scale() * 2;
		if (scale < 50)
			scale = 50;
		return sqrt(number, scale, BigDecimal.ROUND_HALF_UP);
	}
}
