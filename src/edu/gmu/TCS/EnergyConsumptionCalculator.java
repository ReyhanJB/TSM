package edu.gmu.TCS;

import java.io.IOException;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.PatchingChain;
import soot.Scene;
import soot.Transform;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import edu.gmu.sca.util.SootUtil;

public class EnergyConsumptionCalculator extends BodyTransformer {

	EnergyIndicator energyIndicator;
	ProcessManifest manifest;

	public static EnergyIndicator calculate(String apkFilePath, String category) throws IOException, XmlPullParserException {
		EnergyConsumptionCalculator calculator = new EnergyConsumptionCalculator(apkFilePath, category);
		calculator.run(apkFilePath);
		return calculator.energyIndicator;
	}

	private EnergyConsumptionCalculator(String apkFilePath, String category) throws IOException, XmlPullParserException {
		manifest = new ProcessManifest(apkFilePath);
		energyIndicator = new EnergyIndicator(manifest.getPackageName(), category);
	}

	private void run(String apkFilePath) {
		SootUtil.initSootForApkAnalysis(true, apkFilePath, null);
		PackManager.v().getPack("jtp").add(new Transform("jtp.androidTransformer", this));
		Scene.v().loadNecessaryClasses();
		PackManager.v().runPacks();

	}

	@Override
	protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
		if (!b.getMethod().getDeclaringClass().getName().startsWith(manifest.getPackageName()))
			return;
		PatchingChain<Unit> units = b.getUnits();
		for (Unit unit : units) {
			InvokeExpr methodCall = SootUtil.getInvokedMethod(unit);
			if (methodCall == null)
				continue;
			String methodName = methodCall.getMethod().getName();
			String methodClass = methodCall.getMethod().getDeclaringClass().getName();
			Double val = GreenDroid.energyGreedyAPIs.get(methodClass + "." + methodName);
			if (val != null) {
				energyIndicator.addUsage(val);
			}
		}
	}

}
