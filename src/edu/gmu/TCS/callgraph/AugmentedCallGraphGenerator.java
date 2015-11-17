package edu.gmu.TCS.callgraph;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.xmlpull.v1.XmlPullParserException;

import soot.MethodOrMethodContext;
import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import edu.gmu.TCS.callgraph.CGEdge.EdgeType;
import edu.gmu.sca.util.FlowDroidCallGraphGenerator;
import edu.gmu.sca.util.SootUtil;
import edu.gmu.sca.util.StringUtil;

public class AugmentedCallGraphGenerator {

	static Set<CGNode> visited = new HashSet<CGNode>();
	static Map<String, Double> energyGreedyAPIs;

	public static CGNode createGraph(String apk, String graphFile, Map<String, Double> apis, String sourceSinkFile) throws IOException, XmlPullParserException,
			JAXBException {
		energyGreedyAPIs = apis;
		FlowDroidCallGraphGenerator.generateCallGraph(apk, sourceSinkFile);
		CGNode root = new CGNode(FlowDroidCallGraphGenerator.entryPoint);
		visit(FlowDroidCallGraphGenerator.callGraph, root);
		StringUtil.toXML(new File(graphFile, new File(apk).getName().replaceAll("\\.apk", ".xml")).getAbsolutePath(), root);
		root.printCallGraph(new File(graphFile, new File(apk).getName().replaceAll("\\.apk", ".gv")).getAbsolutePath());
		return root;
	}

	private static void visit(CallGraph cg, CGNode current) {
		SootMethod method = current.method;
		visited.add(current);
		processMethod(current);
		// dot.drawNode(identifier);

		// iterate over unvisited parents
		Iterator<MethodOrMethodContext> ptargets = new Targets(cg.edgesInto(method));
		while (ptargets.hasNext()) {
			SootMethod p = (SootMethod) ptargets.next();
			if (p == null)
				continue;
			CGNode parent = new CGNode(p);
			if (!visited.contains(parent))
				visit(cg, parent);
		}

		// iterate over unvisited children
		Iterator<MethodOrMethodContext> ctargets = new Targets(cg.edgesOutOf(method));
		while (ctargets.hasNext()) {
			SootMethod c = (SootMethod) ctargets.next();
			if (c == null)
				continue;
			// dot.drawEdge(identifier, c.getName());
			CGNode child = new CGNode(c);
			CGNode.connect(current, child, EdgeType.METHOD);
			if (!visited.contains(child))
				visit(cg, child);
		}
	}

	private static void processMethod(CGNode node) {
		SootMethod method = node.method;
		if (method == null || !method.getDeclaringClass().isApplicationClass())
			return;
		try {
			PatchingChain<Unit> units = method.getActiveBody().getUnits();
			for (Unit unit : units) {
				InvokeExpr methodCall = SootUtil.getInvokedMethod(unit);
				if (methodCall == null)
					continue;
				String methodName = methodCall.getMethod().getName();
				if(methodName.equals("<init>"))
					methodName = methodCall.getMethod().getDeclaringClass().getShortJavaStyleName();
				String methodClass = methodCall.getMethod().getDeclaringClass().getName();
				Double val = energyGreedyAPIs.get(methodClass  + "." + methodName);
				if (val != null) {
					CGNode eNode = new CGNode(methodCall.getMethod(), val);
					CGNode.connect(node, eNode, EdgeType.API);
				}
			}
		} catch (RuntimeException e) {
			System.err.println(e.getMessage());
		}
	}
}
