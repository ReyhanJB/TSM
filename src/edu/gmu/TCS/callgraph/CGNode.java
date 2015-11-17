package edu.gmu.TCS.callgraph;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import edu.gmu.TCS.callgraph.CGEdge.EdgeType;
import soot.SootMethod;
import soot.util.dot.DotGraph;
import soot.util.dot.DotGraphEdge;
import soot.util.dot.DotGraphNode;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "methodName", "energyValue", "RScore", "children" })
public class CGNode {

	SootMethod method;
	public boolean useless;
	public Set<CGEdge> outEdges;
	public Set<CGEdge> inEdges;
	String fakeName;
	@XmlElement(name = "eScore")
	public Double energyValue;
	public static boolean ignoreSimpleLinks;

	public static void connect(CGNode from, CGNode to, EdgeType type) {
		CGEdge edge = new CGEdge(type, from, to);
		from.outEdges.add(edge);
		to.inEdges.add(edge);
		if (type == EdgeType.API) {
			for (CGEdge in : to.inEdges) {
				in.fromNode.updateEnergyVal(to.energyValue);
			}
			markParentsUseFull(to);
		}
	}

	private static void markParentsUseFull(CGNode node) {
		node.useless = false;
		for (CGEdge inEdge : node.inEdges) {
			if (inEdge.fromNode.useless)
				markParentsUseFull(inEdge.fromNode);
		}
	}

	private CGNode() {
		outEdges = new HashSet<>();
		inEdges = new HashSet<>();
		useless = true;
	}

	public CGNode(SootMethod method) {
		this();
		this.method = method;
	}

	public CGNode(SootMethod method, Double energyValue) {
		this();
		this.method = method;
		this.energyValue = energyValue;
	}

	public CGNode(String fakeName) {
		this();
		this.fakeName = fakeName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null || method.getSignature() == null) ? 0 : method.getSignature().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CGNode other = (CGNode) obj;
		if (method == null && other.method != null)
			return false;
		if (method != null && other.method == null)
			return false;
		else if (method != null && other.method != null && !method.getSignature().equals(other.method.getSignature()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[method=" + method + "]";
	}

	@XmlElement(name = "node")
	public String getMethodName() {
		if (method == null)
			return fakeName;
		return method.getSignature().substring(1, method.getSignature().length() - 1);
	}

	@XmlElement(name = "outgoingEdge")
	public Set<CGEdge> getChildren() {
		Set<CGEdge> result = new HashSet<>();
		getNonSimpleEdges(this, result);
		return result;
	}

	public static void getNonSimpleEdges(CGNode node, Set<CGEdge> result) {
		for (CGEdge edge : node.outEdges) {
			if (edge.toNode.useless)
				continue;
			result.add(edge);
		}
	}

	public String getShortLabel() {
		if (method == null)
			return "Root";
		String[] labels = method.getSignature().split(":");
		String clazz = labels[0].substring(labels[0].lastIndexOf(".") + 1).trim();
		String meth = labels[1].trim().split(" ")[1];
		meth = meth.substring(0, meth.indexOf("("));
		String label = clazz + "(" + meth + ")";
		if (isAPINode())
			label += ":" + energyValue;
		else
			label += "\nr=" + getRScore() + "\ne=" + energyValue;
		return label;
	}

	public void printCallGraph(String resultFilePath) {
		DotGraph dot = new DotGraph("CallGraph");
		dot.setNodeStyle("\"rounded,bold,filled\"");
		dot.setNodeShape("box");
		visit(this, new HashSet<CGNode>(), dot);
		dot.plot(resultFilePath);
	}

	private static void visit(CGNode node, Set<CGNode> visited, DotGraph dot) {
		visited.add(node);
		DotGraphNode drawNode = dot.drawNode(node.getMethodName());
		drawNode.setLabel(node.getShortLabel());
		drawNode.setAttribute("color", node.getColor());
		for (CGEdge outEdge : node.getChildren()) {
			CGNode toNode = outEdge.toNode;
			DotGraphEdge drawEdge = dot.drawEdge(node.getMethodName(), toNode.getMethodName());
			// drawEdge.setLabel(outEdge.getLabel());
			drawEdge.setAttribute("style", "bold");
			drawEdge.setAttribute("color", outEdge.getColor());
			if (!visited.contains(toNode))
				visit(toNode, visited, dot);
		}
	}

	public boolean isAPINode() {
		// return energyValue != null && energyValue > 0;
		return outEdges == null || outEdges.isEmpty();
	}

	private String getColor() {
		if (isAPINode())
			return "limegreen";
		return "aliceblue";
	}

	@XmlElement(name = "rScore")
	public Integer getRScore() {
		int in = 0, out = 0;
		if (inEdges != null)
			for (CGEdge inEdge : inEdges)
				if (!inEdge.fromNode.useless)
					in++;
		if (outEdges != null)
			for (CGEdge outEdge : outEdges)
				if (!outEdge.toNode.useless)
					out++;
		return in * out;
	}

	private void updateEnergyVal(double val) {
		if (energyValue == null)
			energyValue = 0.0;
		energyValue += val;
	}
}
