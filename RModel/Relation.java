package RModel;

import org.w3c.dom.Attr;

import java.util.*;

public class Relation {

	private String name;
	private ArrayList<Tuple> tuples;
	private ArrayList<Attribute> attributes;
	private Attribute PK; //Primary key of the relation, PK is in attributes
	private Map<Relation, Attribute> FKs; //Foreign keys of the relation. It must keep track of which relation it references to.
	private Map<Attribute, Relation> referencingRelation; //keep track of which relation is referencing to.
	private Read_Write_Data rw;

	//create a new relation with a PK, set of foreign keys, and an initial set of tuples
	public Relation(String name, Collection<Attribute> attrs, Collection<Tuple> tuples, Attribute PK, Map<Relation, Attribute> FKs) throws Exception {

		this.name = name;
		this.attributes = new ArrayList<>(attrs);
		this.PK = PK;
		this.FKs = new HashMap<Relation, Attribute>(FKs);
		this.tuples = new ArrayList<>(tuples);
		this.referencingRelation = new HashMap<>();
		check_ReferentialConstraint(FKs, tuples);
		check_Duplicate(tuples);
		check_PrimaryConstraint(tuples);
		addReferencingRelation(FKs);

	}

	//create a new empty relation with a primary key and a set of foreign key.
	public Relation(String name, Collection<Attribute> attrs, Attribute PK, Map<Relation, Attribute> FKs) {
		this.name = name;
		this.attributes = new ArrayList<>(attrs);
		this.tuples = new ArrayList<Tuple>();
		this.FKs = new HashMap<Relation, Attribute>(FKs);
		this.PK = PK;
		this.referencingRelation = new HashMap<>();
		addReferencingRelation(FKs);
	}

	//create a new empty relation with a primary key.
	public Relation(String name, Collection<Attribute> attrs, Attribute PK) {
		
		this.name = name;
		this.attributes = new ArrayList<>(attrs);
		this.tuples = new ArrayList<Tuple>();
		this.FKs = new HashMap<Relation, Attribute>();
		this.PK = PK;
		this.referencingRelation = new HashMap<>();
	}

	//create a new relation with a primary key and an initial set of tuples.
	public Relation(String name, Collection<Attribute> attrs, Collection<Tuple> tuples, Attribute PK) {
		
		this.name = name;
		this.attributes = new ArrayList<>(attrs);
		this.FKs = new HashMap<Relation, Attribute>();
		this.PK = PK;
		this.tuples = new ArrayList<>(tuples);
		this.referencingRelation = new HashMap<>();
		check_PrimaryConstraint(tuples);
		check_Duplicate(tuples);
	}

	//create a new relation with only attributes and tuples for the output of a query.
	public Relation(Collection<Attribute> attrs, Collection<Tuple> tuples) {
		this.attributes = new ArrayList<Attribute>(attrs);
		this.tuples = new ArrayList<Tuple>(tuples);
	}

	/*-----------------------Data Definition-----------------------*/
	//check whether is there any primary key is null
	public void check_PrimaryConstraint(Collection<Tuple> tuples) {
		ArrayList<Object> primaryKeys = getValuesOfColumn(tuples, this.PK);
		Set<Object> uniquePKs = new HashSet<>(primaryKeys);
		if(primaryKeys.contains(null) || primaryKeys.contains(""))
			throw new IllegalArgumentException("Primary keys cannot be null.");
		else if((primaryKeys.size() != uniquePKs.size()))
			throw new IllegalArgumentException("Primary key cannot be duplicated.");
	}

	//check duplicate of a set of tuples
	public void check_Duplicate(Collection<Tuple> tuples) {
		Set<Map<String, Object>> set = new HashSet<>();
		for(Tuple t : tuples) {
			if (!set.add(t.getTupleValues()))
				throw new IllegalArgumentException("There is duplicate tuple.");
		}
	}

	//check domain constraint
	public void check_DomainConstraint(Collection<Tuple> tuples) {
		for(Tuple tuple : tuples) {
			if(attributes.size() != tuple.getAttributeValues().size()) throw new IllegalArgumentException("Number of fields of the new tuple does not match number of attributes");
			else {
				for(Attribute a : attributes) {
					if(!a.getType().equals(tuple.getAttribute(a.getName()).getClass()))
						throw new IllegalArgumentException("Domain constraint is violated, type incompatible.");
				}
			}
		}
	}

	//check referential integrity constraints
	public void check_ReferentialConstraint(Map<Relation, Attribute> FKs, Collection<Tuple> tuples) {
		for(Map.Entry<Relation, Attribute> entry : FKs.entrySet()) {
			Set<Object> referencedAttrs = entry.getKey().primaryKeys();
			ArrayList<Object> referencingAttr = getValuesOfColumn(tuples, entry.getValue());
			for(Object o : referencingAttr) {
				if(!referencedAttrs.contains(o) && o != null)
					throw new IllegalArgumentException("Violates referential integrity constraint.");
			}
		}
	}
	/*-----------------------Data Definition-----------------------*/

	//keep track of which relation it is referencing to
	public void addReferencingRelation(Map<Relation, Attribute> FKs) {
		for (Map.Entry<Relation, Attribute> entry : FKs.entrySet()) {
			entry.getKey().referencingRelation.put(entry.getValue(), this);
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

	//get all attribute names of the relation
	public ArrayList<String> getAttributeNames() {
		ArrayList<String> attrNames = new ArrayList<>();
		for(Attribute attr : getAttributes())
			attrNames.add(attr.getName());
		return attrNames;
	}

	//get all tuples of the relation
	public ArrayList<Tuple> getTuples() {
		return this.tuples;
	}

	//get the primary key of the relaiton
	public Attribute getPK() {return this.PK;}


	/*-----------------------Data Manipulation-----------------------*/
	//insert a new tuple into the relation
	public void insertTuple(Tuple newTuple) {
		ArrayList<Tuple> tmp = new ArrayList<>(getTuples());
		tmp.add(newTuple);
		if(trigger_Constraint_after_Insert(tmp))
			this.tuples = tmp;
		System.out.println("Insert new tuple successfully.");
	}

	//delete tuples based on condition applying on attribute attr
	public void deleteTuple(Attribute attr, String condition, Object operand) {
		switch (condition) {
			case "=":
			case "equals":
				if(operand instanceof Integer || operand instanceof String)
					this.tuples.removeIf(t -> t.getAttribute(attr.getName()).equals(operand));
				else if(operand instanceof Attribute) {
					Attribute a = (Attribute)operand ;
					this.tuples.removeIf(t -> t.getAttribute(attr.getName()).equals(t.getAttribute(a.getName())));
				} else
					throw new IllegalArgumentException("Type of the operand is incompatible with attribute " + attr.getName());
				break;
			case "<":
			case "less than":
				if(operand instanceof Integer)
					this.tuples.removeIf(t -> (Integer)t.getAttribute(attr.getName()) < (Integer)operand);
				else if(operand instanceof String)
					this.tuples.removeIf(t -> t.getAttribute(attr.getName()).toString().compareTo(operand.toString()) < 0);
				else if(operand instanceof Attribute) {
					Attribute a = (Attribute)operand ;
					this.tuples.removeIf(t -> t.getAttribute(attr.getName()).toString().compareTo(t.getAttribute(a.getName()).toString()) < 0);
				}
				else
					throw new IllegalArgumentException("Type of the operand is incompatible with attribute " + attr.getName());
				break;
			case ">":
			case "greater than":
				if(operand instanceof Integer)
					this.tuples.removeIf(t -> (Integer)t.getAttribute(attr.getName()) > (Integer)operand);
				else if(operand instanceof String)
					this.tuples.removeIf(t -> t.getAttribute(attr.getName()).toString().compareTo(operand.toString()) > 0);
				else if(operand instanceof Attribute) {
					Attribute a = (Attribute) operand;
					this.tuples.removeIf(t -> t.getAttribute(attr.getName()).toString().compareTo(t.getAttribute(a.getName()).toString()) > 0);
				}
				else
					throw new IllegalArgumentException("Type of the operand is incompatible with attribute " + attr.getName());
				break;
		}
		//check referential integrity constraint
		trigger_ReferrntialConstraint_after_Delete();
		System.out.println("Delete successfully.");
	}

	//check whether the newTuple violates any constraint. If it does not violate, then return true
	public boolean trigger_Constraint_after_Insert(Collection<Tuple> tmp) {
		check_PrimaryConstraint(tmp);
		check_DomainConstraint(tmp);
		check_Duplicate(tmp);
		check_ReferentialConstraint(getFKs(), tmp);
		return true;
	}

	//check if there is any relation referencing to the deleted primary key, then set that value to null
	public void trigger_ReferrntialConstraint_after_Delete() {
		if (referencingRelation.size() > 0) {
			for (Map.Entry<Attribute, Relation> entry : referencingRelation.entrySet()) {
				Relation r = entry.getValue();
				for (int i = 0; i < r.getTuples().size(); i++) {
					if (!this.getValuesOfColumn(getTuples(), getPK()).contains(r.getTuples().get(i).getAttribute(entry.getKey().getName())))
						r.getTuples().get(i).replaceAttributeValue(entry.getKey(), null);
				}
			}
		}
	}
	
	public void updateTuple(Attribute attr, String condition, Object operand, Object newValue) {
		ArrayList<Tuple> tmp = new ArrayList<>(getTuples());
		for(int i = 0; i < getTuples().size(); i++) {
			Object curValue = null;
			switch (condition) {
				case "=":
				case "equals":
					if(operand instanceof String || operand instanceof Integer) {
						if (getTuples().get(i).getAttribute(attr.getName()).equals(operand))
							curValue = getTuples().get(i).getAttribute(attr.getName());
					}
					else if(operand instanceof Attribute) {
						Attribute a = (Attribute)operand ;
						Object b = getTuples().get(i).getAttribute(attr.getName());
						Object c = getTuples().get(i).getAttribute(a.getName());
						Boolean d = b.equals(c);
						if(d)
							curValue = getTuples().get(i).getAttribute(attr.getName());
					} else
						throw new IllegalArgumentException("Type of the operand is incompatible with attribute " + attr.getName());
					break;
				case "<":
				case "less than":
					if (operand instanceof Integer) {
						if ((Integer) getTuples().get(i).getAttribute(attr.getName()) < (Integer) operand)
							curValue = getTuples().get(i).getAttribute(attr.getName());
					}
					else if(operand instanceof String) {
						if (getTuples().get(i).getAttribute(attr.getName()).toString().compareTo(operand.toString()) < 0)
							curValue = getTuples().get(i).getAttribute(attr.getName());
					}
					else if(operand instanceof Attribute) {
						Attribute a = (Attribute)operand ;
						if (getTuples().get(i).getAttribute(attr.getName()).toString().compareTo(getTuples().get(i).getAttribute(a.getName()).toString()) < 0)
							curValue = getTuples().get(i).getAttribute(attr.getName());
					} else
						throw new IllegalArgumentException("Type of the operand is incompatible with attribute " + attr.getName());
					break;
				case ">":
				case "greater than":
					if (operand instanceof Integer) {
						if ((Integer) getTuples().get(i).getAttribute(attr.getName()) > (Integer) operand)
							curValue = getTuples().get(i).getAttribute(attr.getName());
					}
					else if(operand instanceof String) {
						if (getTuples().get(i).getAttribute(attr.getName()).toString().compareTo(operand.toString()) > 0)
							curValue = getTuples().get(i).getAttribute(attr.getName());
					}
					else if(operand instanceof Attribute) {
						Attribute a = (Attribute) operand;
						if (getTuples().get(i).getAttribute(attr.getName()).toString().compareTo(getTuples().get(i).getAttribute(a.getName()).toString()) > 0)
							curValue = getTuples().get(i).getAttribute(attr.getName());
					}
					break;
			}
			if(curValue != null) {
				tmp.get(i).replaceAttributeValue(attr, newValue);
				trigger_Constraint_after_Update(attr, curValue, newValue);
				System.out.println("Update successfully.");
			}
		}
	}

	public void trigger_Constraint_after_Update(Attribute attr, Object curValue, Object newValue) {
		if(referencingRelation.containsKey(attr)) {
			for(Map.Entry<Attribute, Relation> entry : referencingRelation.entrySet()) {
				Relation r = entry.getValue();
				for(int i = 0; i < r.getTuples().size(); i++) {
					if(r.getTuples().get(i).getAttribute(attr.getName()).equals(curValue))
						r.getTuples().get(i).replaceAttributeValue(attr, newValue);
				}
			}
		}
	}


	/*-----------------------Data Manipulation-----------------------*/



	public void printTuple() {
		for(Attribute attr : attributes) {
			System.out.print(attr.getName() + "\t\t\t");
		}
		System.out.println();
		for (Tuple tuple : tuples) {
			for(Attribute attr : attributes) {
				System.out.print(tuple.getAttribute(attr.getName()) + "\t\t\t");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void printRelation() {
//		System.out.println(Arrays.toString(attributes.toArray()));
		String str = "RELATION: " + this.name + "\n";
		for( Attribute attr : attributes ) {
			str += attr.getName() + "\t\t\t";
		}
		str += "\n";
		for (Tuple tuple : this.tuples) {
			for(Attribute attr: attributes ) {
				Object val = tuple.getAttribute(attr.getName());
				str += val + "\t\t\t";
			}
			str += "\n";
		}
		System.out.println(str);
	}
     
}
