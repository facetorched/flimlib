package slim;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;

public class SLIMCurveNativeTest {

	protected ArrayList<NativeTest> tests;
	protected float tolerance;

	@Before
	public void setUp() throws Exception {
		this.tests = new ArrayList<NativeTest>();

		JSONParser parser = new JSONParser();
		JSONObject testRun = (JSONObject) ((JSONObject) parser.parse(new FileReader("./test_files/ref.json")))
				.get("testrun");

		JSONArray tests = (JSONArray) testRun.get("tests");
		this.tolerance = testRun.containsKey("tolerance") ? Float.parseFloat((String) testRun.get("tolerance")) : 10;

		Iterator<?> itr = tests.iterator();
		while (itr.hasNext()) {
			this.tests.add(new NativeTest((JSONObject) itr.next(), this.tolerance));
		}
	}

	@Test
	public void runTests() {
		this.tolerance += 1;
	}
}

class NativeTest {

	protected JSONObject json;
	protected float tolerance;
	protected int n_data, fit_start, fit_end;
	protected float xincr, chisq_target;
	protected NoiseType noise;
	protected float[] y, params, instr, chisq, sig, fitted, residuals, Z, A, tau;
	protected String comments;
	protected String test;

	public NativeTest(JSONObject test, float tolerance) {
		this.json = test;
		this.comments = (String) test.get("comment");
		this.tolerance = getFloat(test, "tolerance", tolerance);
		JSONObject inputs = (JSONObject) test.get("inputs");
		this.n_data = (int) getFloat(inputs, "ndata", 0);
		this.fit_start = (int) getFloat(inputs, "fitstart", n_data / 4);
		this.fit_end = (int) getFloat(inputs, "fitstop", n_data * 7 / 8);
		this.xincr = getFloat(inputs, "xinc", 0.1f);
		this.chisq_target = getFloat(inputs, "chisquaretarget", 1);
		this.noise = NoiseType.swigToEnum((int) getFloat(inputs, "noise", 0));
		this.y = getFloatArr(inputs, "counts");
		this.test = (String) test.get("test");
		//this.params = 0;
		if (test.equals("mono-exp")) {
			//this.params
		} else if (test.equals("bi-exp")) {
			
		} else if (test.equals("tri-exp"))
		this.instr = getFloatArr(inputs, "instr");
		this.chisq = new float[] { 0 };
		this.sig = new float[this.n_data];
		for (int i = 0; i < sig.length; i++)
			sig[i] = 1.0f;
		this.fitted = new float[this.n_data];
		this.residuals = new float[this.n_data];
		JSONObject outputs = (JSONObject) test.get("outputs");
		this.Z = new float[] { getFloat(outputs, "z", 2.0f) };
		this.A = new float[] { getFloat(outputs, "a", 1.0f) };
		this.tau = new float[] { getFloat(outputs, "t", 3.0f) };
		run();
	}

	public void run() {
		int result = SLIMCurve.GCI_triple_integral_fitting_engine(xincr, y, fit_start, fit_end, instr, noise, sig, Z, A,
				tau, fitted, residuals, chisq, chisq_target);
		System.out.printf("Triple Integral chisq is %f\n", chisq[0]);
		System.out.printf("Triple Integral return value %d a %f t %f z %f\n", result, A[0], tau[0], Z[0]);
		//SLIMCurve.GCI_marquardt_fitting_engine(xincr, y, ndata, result, result, instr, noise, sig, param, paramfree, restrain, fitfunc, fitted, residuals, chisq, covar, alpha, erraxes, result, chisq_delta, chisq_percent)
	}
	
	private static float getFloat(JSONObject source, String property, float defaultVal) {
		return source.containsKey(property) ? Float.parseFloat((String) source.get(property)) : defaultVal;
	}
	
	private static float[] getFloatArr(JSONObject source, String arrName) {
		JSONObject array = (JSONObject) source.get(arrName);
		if (array == null)
			return null;
		float[] arr = new float[(int) getFloat(array, "size", 0)];
		String[] data = ((String) array.get("value")).split(",");
		for (int i = 0; i < arr.length; i++)
			arr[i] = Float.parseFloat(data[i]);
		return arr;
	}
}
