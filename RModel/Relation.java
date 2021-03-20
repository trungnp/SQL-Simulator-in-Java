package RModel;

import org.w3c.dom.Attr;

import java.util.*;

public class Relation {

	private String name; //Relation name
	private ArrayList<Tuple> tuples; //A set of tuples of the relation
	private ArrayList<Attribute> attributes;  //A set of attribute of the relation
	private Attribute PK; //Primary key of the relation, PK is in attributes
	private Map<Relation, Attribute> FKs; //Foreign keys of the relation. It must keep track of which relation is being referenced by the relation
	private Map<Attribute, Relation> referencingRelation; //Keep track of which relation is referencing to the relation.

	/*
	create a new relation with a name, a set of attributes, a set of tuples, a primary key, and a set of foreign keys
	If any constraint is violated, it will return a message indicating which constraint is violated
	*/
	public Relation(String name, Collection<Attribute> attrs, Collection<Tuple> tuples, Attribute PK, Map<Relation, Attribute> FKs) {

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

	//create a new empty relation with a name, a set of attribute,  a primary key, and a set of foreign key.
	public Relation(String name, Collection<Attribute> attrs, Attribute PK, Map<Relation, Attribute> FKs) {
		this.name = name;
		this.attributes = new ArrayList<>(attrs);
		this.tuples = new ArrayList<Tuple>();
		this.FKs = new HashMap<Relation, Attribute>(FKs);
		this.PK = PK;
		this.referencingRelation = new HashMap<>();
		addReferencingRelation(FKs);
	}

	//create a new empty relation with a name, a set of attribute,  and a primary key
	public Relation(String name, Collection<Attribute> attrs, Attribute PK) {
		
		this.name = name;
		this.attributes = new ArrayList<>(attrs);
		this.tuples = new ArrayList<Tuple>();
		this.FKs = new HashMap<Relation, Attribute>();
		this.PK = PK;
		this.referencingRelation = new HashMap<>();
	}

	//create a new empty relation with a name, a set of attribute,  a set of tuples, and a primary key
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

	//create a new empty relation with a name, a set of attribute, and a set of tuples used for output of a query
	public Relation(String name, Collection<Attribute> attrs, Collection<Tuple> tuples) {
		this.name = name;
		this.attributes = new ArrayList<Attribute>(attrs);
		this.tuples = new ArrayList<Tuple>(tuples);
	}

	//create a new relation with only a set of attributes and a set of tuples used for the output of a query.
	public Relation(Collection<Attribute> attrs, Collection<Tuple> tuples) {
		this.attributes = new ArrayList<Attribute>(attrs);
		this.tuples = new ArrayList<Tuple>(tuples);
	}

	/*-----------------------Data Definition-----------------------*/
	//check whether is there any primary key is null. If yes, throw a message indicating the constraint
	public void check_PrimaryConstraint(Collection<Tuple> tuples) {
		ArrayList<Object> primaryKeys = getValuesOfColumn(tuples, this.PK);
		Set<Object> uniquePKs = new HashSet<>(primaryKeys);
		if(primaryKeys.contains(null) || primaryKeys.contains(""))
			throw new IllegalArgumentException("Primary keys cannot be null.");
		else if((primaryKeys.size() != uniquePKs.size()))
			throw new IllegalArgumentException("Primary key cannot be duplicated.");
	}

	//check duplicate of a set of tuples. If there any duplicate, throw a message indicating there is duplicate tuple
	public void check_Duplicate(Collection<Tuple> tuples) {
		Set<Map<String, Object>> set = new HashSet<>();
		for(Tuple t : tuples) {
			if (!set.add(t.getTupleValues()))
				throw new IllegalArgumentException("There is duplicate tuple.");
		}
	}

	//check domain constraint. If it is violated, throw a message indicating the constraint
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

	//check referential integrity constraints. If it is violated, throw a message indicating the constraint
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

	//keep track of which relation is referencing to the relation
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

	//get an Attribute by name
	public Attribute getAttributeByName(String name) {
		for(Attribute attr : this.attributes) {
			if(attr.getName().equals(name))
				return attr;
		}
		return null;
	}

	/*-----------------------Data Manipulation-----------------------*/

	/*
	insert a new tuple into the relation. Throw a message indicating whether inserting successful or failed.
	after insertion, check whether any constraint is violated or not.
	 */
	public void insertTuple(Tuple newTuple) {
		ArrayList<Tuple> tmp = new ArrayList<>(getTuples());
		String s = String.format("INSERT TUPLE INTO RELATION %s (", this.getName());
		for(Attribute attr : this.getAttributes())
			s += newTuple.getAttribute(attr.getName()).toString() + ",";
		s = s.substring(0, s.length()-1) + ") ";
		try {
			tmp.add(newTuple);
			if(trigger_Constraint_after_Insert(tmp)) { //no constraint violated
				this.tuples = tmp;
				System.out.println(s + "successfully.");
			}
		} catch (IllegalArgumentException e) { //contraint violated
			System.out.println(s + "failed due to: " +e.getMessage());
		}
	}


	/*
	delete tuples based on condition applying on attribute attr
	after deleting a tuple, it needs to check whether any foreign of other relation is referencing to the deleted primary.
	if yes, change its value to null.
	 */
	public void deleteTuple(Attribute attr, String condition, Object operand) {
		switch (condition) {
			case "=":
				if(operand instanceof Integer || operand instanceof String)
					this.tuples.removeIf(t -> t.getAttribute(attr.getName()).equals(operand));
				else if(operand instanceof Attribute) {
					Attribute a = (Attribute)operand ;
					this.tuples.removeIf(t -> t.getAttribute(attr.getName()).equals(t.getAttribute(a.getName())));
				} else
					throw new IllegalArgumentException("Type of the operand is incompatible with attribute " + attr.getName());
				break;
			case "<":
				if (operand instanceof String || operand instanceof Integer) {
					this.tuples.removeIf(t -> t.getAttribute(attr.getName()).toString().compareTo(operand.toString()) < 0);
				} else if(operand instanceof Attribute) {
					Attribute a = (Attribute)operand ;
					this.tuples.removeIf(t -> t.getAttribute(attr.getName()).toString().compareTo(t.getAttribute(a.getName()).toString()) < 0);
				}
				else
					throw new IllegalArgumentException("Type of the operand is incompatible with attribute " + attr.getName());
				break;
			case ">":
				if (operand instanceof String || operand instanceof Integer) {
					this.tuples.removeIf(t -> t.getAttribute(attr.getName()).toString().compareTo(operand.toString()) > 0);
				} else if(operand instanceof Attribute) {
					Attribute a = (Attribute)operand ;
					this.tuples.removeIf(t -> t.getAttribute(attr.getName()).toString().compareTo(t.getAttribute(a.getName()).toString()) > 0);
				}
				else
					throw new IllegalArgumentException("Type of the operand is incompatible with attribute " + attr.getName());
				break;
		}
		//check referential integrity constraint and set foreign key to null
		trigger_ReferrentialConstraint_after_Delete();
		System.out.printf("Delete tuple in %s where %s %s %s successfully.%n", this.name, attr.getName(), condition, operand.toString());
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
	public void trigger_ReferrentialConstraint_after_Delete() {
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

	/*
	update a tuple based on condition and operand applying on attribuet whereAttr
	after update, it will check whether any constraint is violated or not.
	if no constraint violated, it will continue to check whether any foreign key of other relations is affected or not.
	if yes, change its value to the new value that just has been updated.
	 */
	public void updateTuple(Attribute whereAttr, String condition, Object operand, Attribute setAttr, Object newValue) {
		ArrayList<Tuple> tmp = new ArrayList<>(getTuples());
		for(int i = 0; i < getTuples().size(); i++) {
			Object curValue = null;
			switch (condition) {
				case "=":
					if(operand instanceof String || operand instanceof Integer) {
						if (getTuples().get(i).getAttribute(whereAttr.getName()).equals(operand))
							curValue = getTuples().get(i).getAttribute(setAttr.getName());
					}
					else if(operand instanceof Attribute) {
						Attribute a = (Attribute)operand ;
//						Object b = getTuples().get(i).getAttribute(setAttr.getName());
//						Object c = getTuples().get(i).getAttribute(a.getName());
//						Boolean d = b.equals(c);
						if(getTuples().get(i).getAttribute(setAttr.getName()).equals(getTuples().get(i).getAttribute(a.getName())))
							curValue = getTuples().get(i).getAttribute(whereAttr.getName());
					} else
						throw new IllegalArgumentException("Type of the operand is incompatible with attribute " + whereAttr.getName());
					break;
				case "<":
					if (operand instanceof String || operand instanceof Integer) {
						if (getTuples().get(i).getAttribute(whereAttr.getName()).toString().compareTo(operand.toString()) < 0 )
							curValue = getTuples().get(i).getAttribute(setAttr.getName());
					}
					else if(operand instanceof Attribute) {
						Attribute a = (Attribute)operand ;
						if (getTuples().get(i).getAttribute(whereAttr.getName()).toString().compareTo(getTuples().get(i).getAttribute(a.getName()).toString()) < 0)
							curValue = getTuples().get(i).getAttribute(whereAttr.getName());
					} else
						throw new IllegalArgumentException("Type of the operand is incompatible with attribute " + whereAttr.getName());
					break;
				case ">":
					if (operand instanceof String || operand instanceof Integer) {
						if (getTuples().get(i).getAttribute(whereAttr.getName()).toString().compareTo(operand.toString()) > 0 )
							curValue = getTuples().get(i).getAttribute(setAttr.getName());
					}
					else if(operand instanceof Attribute) {
						Attribute a = (Attribute) operand;
						if (getTuples().get(i).getAttribute(whereAttr.getName()).toString().compareTo(getTuples().get(i).getAttribute(a.getName()).toString()) > 0)
							curValue = getTuples().get(i).getAttribute(whereAttr.getName());
					} else
						throw new IllegalArgumentException("Type of the operand is incompatible with attribute " + whereAttr.getName());
					break;
			}
			if(curValue != null) {
				try {
					tmp.get(i).replaceAttributeValue(setAttr, newValue);
					if(trigger_Constraint_after_Insert(tmp)) { //no constraint is violated
						this.tuples = tmp;
						trigger_Constraint_after_Update(setAttr, curValue, newValue); //update foreign key to new value
						System.out.printf("Update tuple in %s where %s %s %s set %s = %s successfully.%n", this.name, whereAttr.getName(), condition, operand.toString(), setAttr.getName(), newValue.toString());
					}
				} catch (IllegalArgumentException e) { //contraint is violated, roll back to the old tuple
					tmp.get(i).replaceAttributeValue(setAttr, curValue);
					System.out.printf("Update tuple in %s where %s %s %s set %s = %s failed due to %s%n", this.name, whereAttr.getName(), condition, operand.toString(), setAttr.getName(), newValue.toString(), e.getMessage());				}

			}
		}

	}

	//check for the foreign key that references to the tuple that has been updated and update that foreign key's value to the new value
	public void trigger_Constraint_after_Update(Attribute attr, Object curValue, Object newValue) {
			for(Map.Entry<Attribute, Relation> entry : referencingRelation.entrySet()) {
				if(entry.getKey().equals(attr)) {
					Relation r = entry.getValue();
					for (int i = 0; i < r.getTuples().size(); i++) {
						if (r.getTuples().get(i).getAttribute(attr.getName()).equals(curValue))
							r.getTuples().get(i).replaceAttributeValue(attr, newValue);
					}
				}
			}
//		}
	}



	/*-----------------------Data Manipulation-----------------------*/

	//print relations
	public void printRelation() {
		if(this.getName() != null) {
			System.out.println("Relation Name: " + this.name);
		}
		for(Attribute attr : attributes) {
			System.out.printf("%-18s", attr.getName());
		}
		System.out.println();
		for (Tuple tuple : tuples) {
			for(Attribute attr : attributes) {
				System.out.printf("%-18s", tuple.getAttribute(attr.getName()));
			}
			System.out.println();
		}
		System.out.println();
	}
     
}
