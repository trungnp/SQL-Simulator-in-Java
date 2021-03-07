package RModel;

import java.util.ArrayList;
import java.util.HashMap;

public class Driver {

	public static void main(String[] args) throws Exception {

		ArrayList<Attribute> tAttrs = getTestAttributes();
		ArrayList<Tuple> testTuples = getTestTuples();
		
		// printing an attribute
		System.out.println("ATTRIBUTE -- " + tAttrs.get(0));
		System.out.println();
		
		// printing a tuple
		System.out.println("TUPLE 0 -- " + testTuples.get(0));
		System.out.println();

		HashMap<String, Object> attrValues1 = new HashMap<String, Object>();
		attrValues1.put("Attr1", Integer.valueOf(11));
		attrValues1.put("Attr2", "type12");
		attrValues1.put("Attr3", Integer.valueOf(2));

		Relation r = new Relation("R1", tAttrs, testTuples, tAttrs.get(0));
		System.out.println(r.getPK());
		System.out.println("Relation before");
		r.printRelation();
		try {
			r.insertTuple(new Tuple(attrValues1));
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Relation after");
		r.printRelation();
		
		
	}
	
	public static ArrayList<Attribute> getTestAttributes() {
		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		
		attrs.add( new Attribute("Attr1", Integer.class) );
		attrs.add( new Attribute("Attr2", String.class) );
		attrs.add( new Attribute("Attr3", Integer.class) );
		
		return attrs;
		
	}
	
	public static ArrayList<Tuple> getTestTuples() {
		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		
		attrs.add( new Attribute("Attr1", Integer.class) );
		attrs.add( new Attribute("Attr2", String.class) );
		attrs.add( new Attribute("Attr3", Integer.class) );
		
		HashMap<String, Object> attrValues1 = new HashMap<String, Object>();
		attrValues1.put("Attr1", Integer.valueOf(4));
		attrValues1.put("Attr2", "type1");
		attrValues1.put("Attr3", Integer.valueOf(2));
		
		HashMap<String, Object> attrValues2 = new HashMap<String, Object>();
		attrValues2.put("Attr1", Integer.valueOf(1));
		attrValues2.put("Attr2", "type1");
		attrValues2.put("Attr3", Integer.valueOf(2));

		HashMap<String, Object> attrValues3 = new HashMap<String, Object>();
		attrValues3.put("Attr1", Integer.valueOf(11));
		attrValues3.put("Attr2", "type12");
		attrValues3.put("Attr3", Integer.valueOf(2));
		
		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
		
		tuples.add( new Tuple(attrValues1) );
		tuples.add( new Tuple(attrValues2) );
		tuples.add( new Tuple(attrValues3) );
		
		return tuples;
		
	}

}
