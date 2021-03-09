package RModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Driver {

	public static void main(String[] args) throws Exception {
		ArrayList<Attribute> tAttrs = getTestAttributes();
		ArrayList<Tuple> testTuples = getTestTuples();
		Read_Write_Data rw = new Read_Write_Data();
		
		// printing an attribute
		System.out.println("ATTRIBUTE -- " + tAttrs.get(0));
		System.out.println();
		
		// printing a tuple
		System.out.println("TUPLE 0 -- " + testTuples.get(0));
		System.out.println();

		HashMap<String, Object> attrValues1 = new HashMap<String, Object>();
		attrValues1.put("Attr1", Integer.valueOf(111));
		attrValues1.put("Attr2", "type12");
		attrValues1.put("Attr3", Integer.valueOf(22));

		Relation r = new Relation("R1", tAttrs, testTuples, tAttrs.get(0));
		rw.createRelation(r);
		System.out.println(r.getPK());
		System.out.println("Relation before");
		r.printRelation();
		try {
			r.insertTuple(new Tuple(attrValues1));
			testTuples.add(new Tuple(attrValues1));
			rw.writeTuplesToRelation(r, testTuples);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Relation after\n");
		r.printRelation();
		System.out.println("Relation after delete\n");
		//r.deleteTuple("Attr1", "equals", 4);
		r.printRelation();
		//rw.deleteTupleFromRelation(r, testTuples.get(1));
		System.out.println();
		rw.readAllTuplesOfRelation(r);
		if(rw.deleteRelation(r)) {
			System.out.println("Delete R1");
		}
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
