package edu.gmu.TCS.callgraph;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
public class CGEdge {

	String label;
	public EdgeType type;
	@XmlTransient
	public CGNode fromNode;
	public CGNode toNode;

	public CGEdge() {
	}

	public CGEdge(EdgeType type, CGNode fromNode, CGNode toNode) {
		super();
		this.type = type;
		this.fromNode = fromNode;
		this.toNode = toNode;
	}


	public CGEdge(EdgeType type) {
		this.type = type;
	}

	public CGEdge(String label, EdgeType type) {
		this(type);
		this.label = label;
	}

	public enum EdgeType {
		METHOD, API
	}

	String getLabel() {
		return type.name() + (label != null ? " (" + label + ")" : "");
	}

	String getColor() {
		return "black";
//		switch (type) {
//		case METHOD:
//			return "firebrick";
//		case API:
//			return "dodgerblue";
//		default:
//			return "black";
//		}
	}
}
