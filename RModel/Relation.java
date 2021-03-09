package RModel;

import org.w3c.dom.Attr;

import java.util.*;

public class Relation {

	private String name;
	private ArrayList<Tuple> tuples;
	private ArrayList<Attribute> attributes;
	private Attribute PK; //Primary key of the relation, PK is in attributes
	private Map<Relation, Attribute> FKs; //Foreign keys of the relation. It must keep track of which relation it references to.
	private Map<Attribute, Relation> referencingRelation; //keep track of which relation it is referencing to.
	private Read_Write_Data rw;

	//create a new relation with a PK, set of foreign keys, and an initial set of tuples
	public Relation(String name, Collection<Attribute> attrs, Collection<Tuple> tuples, Attribute PK, Map<Relation, Attribute> FKs) throws Exception {

		this.name = name;
		this.attributes = new ArrayList<Attribute>(attrs);
		this.FKs = new HashMap<Relation, Attribute>(FKs);
		addReferencingRelation(FKs);
		if(violate_ReferentialIntegrityConstrainst()) throw new Exception("Foreign key values must be either null or equal to the values of the referencing primary keys");
		if (isPrimaryNull(tuples)) throw new Exception("Primary key is null, please check primary key column.");
		if(hasDuplicate(tuples)) throw new Exception("There are duplicate tuples, please check the set of tuple.");
		this.tuples = new ArrayList<>(tuples);
	}

	//create a new empty relation with a primary key and a set of foreign key.
	public Relation(String name, Collection<Attribute> attrs, Attribute PK, Map<Relation, Attribute> FKs) {
		this.name = name;
		this.attributes = new ArrayList<Attribute>(attrs);
		this.tuples = new ArrayList<Tuple>();
		this.FKs = new HashMap<Relation, Attribute>(FKs);
		addReferencingRelation(FKs);
		this.PK = PK;
	}

	//create a new empty relation with a primary key.
	public Relation(String name, Collection<Attribute> attrs, Attribute PK) {
		
		this.name = name;
		this.attributes = new ArrayList<Attribute>(attrs); 
		this.tuples = new ArrayList<Tuple>();
		this.FKs = new HashMap<Relation, Attribute>();
		this.PK = PK;
	}

	//create a new relation with a primary key and an initial set of tuples.
	public Relation(String name, Collection<Attribute> attrs, Collection<Tuple> tuples, Attribute PK) throws Exception {
		
		this.name = name;
		this.attributes = new ArrayList<Attribute>(attrs);
		this.FKs = new HashMap<Relation, Attribute>();
		this.PK = PK;
		if (isPrimaryNull(tuples)) throw new Exception("Primary key is null, please check primary key column.");
		if(hasDuplicate(tuples)) throw new Exception("There are duplicate tuples, please check the set of tuple.");
		this.tuples = new ArrayList<>(tuples);
	}

	/*-----------------------Data Definition-----------------------*/
	//check whether is there any primary key is null
	public boolean isPrimaryNull(Collection<Tuple> tuples) {
		ArrayList<Object> primaryKeys = getValuesOfColumn(tuples, this.PK);
		return primaryKeys.contains(null) || primaryKeys.contains("");
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

	//check referential integrity constraints
	public boolean violate_ReferentialIntegrityConstrainst() {
		for(Map.Entry<Relation, Attribute> entry : this.FKs.entrySet()) {
			Set<Object> referencedAttrs = entry.getKey().primaryKeys();
			ArrayList<Object> referencingAttr = getValuesOfColumn(getTuples(), entry.getValue());

			for(Object o : referencingAttr) {
				if(!referencedAttrs.contains(o) || o != null)
					return true;
			}
		}
		return false;
	}
	/*-----------------------Data Definition-----------------------*/

	//keep track of which relation it is referencing to
	public void addReferencingRelation(Map<Relation, Attribute> FKs) {
		for (Map.Entry<Relation, Attribute> entry : FKs.entrySet()) {
			entry.getKey().referencingRelation.put(entry.getValue(), entry.getKey());
		}
	}

	//get values of a column
	public ArrayList<Object> getValuesOfColumn(Collection<Tuple> tuples, Attribute attr) {
		ArrayList<Object> result = new ArrayList<Object>();
		for(Tuple t : tuples)
			result.add(t.getAttribute(attr.getName()));
		return result;
	}

	//get foreign keys of the relation
	public Map<Relation, Attribute> getFKs() {
		return this.FKs;
	}

	//return primary keys of the relation
	public Set<Object> primaryKeys() {
		return new HashSet<>(getValuesOfColumn(getTuples(), getPK()));
	}

	//get name of the relation
	public String getName() {
		return this.name;
	}

	//get all attributes of the relation
	public ArrayList<Attribute> getAttributes() {
		return this.attributes;
	}

	//get all tuples of the relation
	public ArrayList<Tuple> getTuples() {
		return this.tuples;
	}


	/*-----------------------Data Manipulation-----------------------*/
	//insert a new tuple into the relation
	public void insertTuple(Tuple newTuple) {
		if(newTuple.getAttributeNames().size() != getAttributes().size()) throw new IllegalArgumentException("Number of fields of the new tuple does not match number of attributes");
		else {
			for(int i = 0; i < getAttributes().size(); i++) {
				Class attrType = getAttributes().get(i).getType();
				Class valueTypeTuple = newTuple.getTupleValues().get(getAttributes().get(i).getName()).getClass();
				if(!attrType.equals(valueTypeTuple)) throw new IllegalArgumentException("Type incompatible.");
			}
		}

		if(getTuples().contains(newTuple)) throw new IllegalArgumentException("The new tuple is duplicate and is discarded.");
		else if(newTuple.getAttribute(getPK().getName()) == null || newTuple.getAttribute(getPK().getName()).equals("")) throw new IllegalArgumentException("Primary key cannot be nulll. Insert failed!");
		else if(primaryKeys().contains(newTuple.getAttribute(this.PK.getName()))) throw new IllegalArgumentException("Primary key is duplicate");
		else {
			ArrayList<Tuple> reverse = new ArrayList<>(getTuples());
			this.tuples.add(newTuple);
			if(violate_ReferentialIntegrityConstrainst()) {
				this.tuples = reverse;
				throw new IllegalArgumentException("New tuple violates referential integrity constraint.");
			} else
				System.out.println("Insert new tuple successfully.");
		}
	}

	//delete tuples based on condition applying on attribute attr
	public void deleteTuple(String attrName, String condition, Object operand) {
		switch (condition) {
			case "=":
			case "equals":
				this.tuples.removeIf(t -> t.getAttribute(attrName).equals(operand));
			case "<":
			case "less than":
				if(operand instanceof Integer)
					this.tuples.removeIf(t -> (Integer)t.getAttribute(attrName) < (Integer)operand);
				else
					this.tuples.removeIf(t -> t.getAttribute(attrName).toString().compareTo(operand.toString()) < 0);
			case ">":
			case "greater than":
				if(operand instanceof Integer)
					this.tuples.removeIf(t -> (Integer)t.getAttribute(attrName) > (Integer)operand);
				else
					this.tuples.removeIf(t -> t.getAttribute(attrName).toString().compareTo(operand.toString()) > 0);
		}
		//check if there is any relation referencing to the deleted primary key, then set that value to null
		if(referencingRelation.size() > 0) {
			for(Map.Entry<Attribute, Relation> entry : referencingRelation.entrySet()) {
				ArrayList<Object> referencedValues = entry.getValue().getValuesOfColumn(entry.getValue().getTuples(),entry.getKey());
				for(Object o : referencedValues) {
					if(!getValuesOfColumn(getTuples(), entry.getKey()).contains(o)) {
						o = null;
					}
				}
			}
		}
	}
	
	public void updateTuple() {
		
	}

	/*-----------------------Data Manipulation-----------------------*/

	public Attribute getPK() {return this.PK;}
	
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
