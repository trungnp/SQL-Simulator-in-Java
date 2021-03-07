package RModel;

import org.w3c.dom.Attr;

import java.util.*;

public class Relation {

	private String name;
	private ArrayList<Tuple> tuples;
	private ArrayList<Attribute> attributes;
	private Attribute PK; //Primary key of the relation, PK is in attributes
	
	public Relation(String name, Collection<Attribute> attrs, Attribute PK) {
		
		this.name = name;
		this.attributes = new ArrayList<Attribute>(attrs); 
		this.tuples = new ArrayList<Tuple>();
		if(isLegalPK(PK))
			this.PK = PK;
		else
			System.out.println("Primary key does not exist in the attribute set, please check the PK name.");
	}
	
	public Relation(String name, Collection<Attribute> attrs, Collection<Tuple> tuples, Attribute PK) throws Exception {
		
		this.name = name;
		this.attributes = new ArrayList<Attribute>(attrs);
		if(isLegalPK(PK)) {
			this.PK = PK;
			if (isPrimaryNull(tuples)) throw new Exception("Primary key is null, please check primary key column.");
		}
		else
				System.out.println("Primary key does not exist in the attribute set, please check the PK name.");
		if(hasDuplicate(tuples)) throw new Exception("There are duplicate tuples, please check the set of tuple.");
		this.tuples = new ArrayList<>(tuples);
	}

	//check whether primary key is in the set of attributes
	public boolean isLegalPK(Attribute PK) {
		for (Attribute attr : this.attributes) {
			if(attr.equals(PK)) return true;
		}
		return false;
	}

	//check whether is there any primary key is null
	public boolean isPrimaryNull(Collection<Tuple> tuples) {
		for(Tuple tuple: tuples) {
			if(tuple.getAttribute(this.PK.getName()) == null || tuple.getAttribute(this.PK.getName()) == "") return true;
		}
		return false;
	}

	//check duplicate of a set of tuples
	public boolean hasDuplicate(Collection<Tuple> tuples) {
		Set<Map<String, Object>> set = new HashSet<>();
		for(Tuple t : tuples) {
			if (!set.add(t.getTupleValues()))
				return true;
		}
		return false;
	}

	public Set<Object> primaryKeys() {
		Set<Object> PK_List = new HashSet<>();
		for(Tuple t : tuples) {
			PK_List.add(t.getAttribute(this.PK.getName()));
		}
		return PK_List;
	}

	//insert a new tuple into the relation
	public void insertTuple(Tuple newTuple) {
		if(newTuple.getAttributeNames().size() != attributes.size()) throw new IllegalArgumentException("Number of fields of the new tuple does not match number of attributes");
		else {
			for(int i = 0; i < attributes.size(); i++) {
				Class attrType = attributes.get(i).getType();
				Class valueTypeTuple = newTuple.getTupleValues().get(attributes.get(i).getName()).getClass();
				if(!attrType.equals(valueTypeTuple)) throw new IllegalArgumentException("Type incompatible.");
			}
		}

		ArrayList<Tuple> tmp = new ArrayList<>(this.tuples);
		tmp.add(newTuple);

		if(hasDuplicate(tmp)) throw new IllegalArgumentException("The new tuple is duplicate and is discarded.");
		else if(isPrimaryNull(tmp)) throw new IllegalArgumentException("Primary key cannot be nulll. Insert failed!");
		else if(primaryKeys().contains(newTuple.getAttribute(this.PK.getName()))) throw new IllegalArgumentException("Primary key is duplicate");
		else {
			this.tuples = tmp;
			System.out.println("Insert new tuple successfully.");
		}
	}

	//delete tuples based on condition applying on attribute attr
	public void deleteTuple(String attrName, String condition, Object operand) {
		switch (condition) {
			case "=":
			case "equals":
				for(Tuple t : tuples) {
					if(t.getAttribute(attrName).equals(operand))
						tuples.remove(t);
				}
//			case "<":
//			case "less than":
//				for(Tuple t : tuples) {
//					if(t.getAttribute(attrName) < operand)
//						tuples.remove(t);
//				}
//			case ">":
//			case "greater than":
//				for(Tuple t : tuples) {
//					if(t.getAttribute(attrName) > operand)
//						tuples.remove(t);
//				}
		}
	}
	
	public void updateTuple() {
		
	}

	public String getPK() {return this.PK.getName();}
	
	public void printRelation() {
//		System.out.println(Arrays.toString(attributes.toArray()));
		String str = "RELATION: " + this.name + "\n";
		for( Attribute attr : attributes ) {
			str += attr.getName() + "\t";
		}
		str += "\n";
		for (Tuple tuple : this.tuples) {
			for(Attribute attr: attributes ) {
				Object val = tuple.getAttribute(attr.getName());
				str += val + "\t";
			}
			str += "\n";
		}
		System.out.println(str);
	}
     
}
