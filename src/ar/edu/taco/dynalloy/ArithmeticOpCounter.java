package ar.edu.taco.dynalloy;

/**
 * The Java-arithmetic operation counts of one scope inference, one counter per primitive type.
 * {@code ArithmeticOpCollector} fills them, run by {@link DynAlloyProgramScopeInferencePlugin};
 * {@code ar.edu.taco.infer.ScopeInference} reads them through these public fields to size
 * the program scope of {@code JavaPrimitiveIntegerValue}, {@code JavaPrimitiveLongValue},
 * {@code JavaPrimitiveFloatValue} and {@code JavaPrimitiveCharValue}. Holds no static state.
 */
public class ArithmeticOpCounter {

	public FloatOpCounter floatOpCounter = new FloatOpCounter();
	public LongOpCounter longOpCounter = new LongOpCounter();
	public IntegerOpCounter integerOpCounter = new IntegerOpCounter();
	public CharOpCounter charOpCounter = new CharOpCounter();



}
