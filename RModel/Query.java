package RModel;

import java.util.*;

public class Query {
    private Relation relation;


    public Relation select(ArrayList<Attribute> attributes, Relation r) {
        Map<String, Object> value = new HashMap<>();
        ArrayList<Tuple> tuples = new ArrayList<>();
        if(attributes.size() < 1) throw new IllegalArgumentException("Set of attribute must not be empty");
        for(Attribute attr : attributes) {
            if(!r.getAttributeNames().contains(attr.getName()))
                throw new IllegalArgumentException("Set of attribute must be a subset of relation " + r.getName());
        }
        int no_Of_Attrs = 0;
        int no_Of_Rows = 0;


        while(no_Of_Rows < r.getValuesOfColumn(r.getTuples(), attributes.get(0)).size()) {
            while (no_Of_Attrs < attributes.size()) {
                value.put(attributes.get(no_Of_Attrs).getName(), r.getValuesOfColumn(r.getTuples(), attributes.get(no_Of_Attrs)).get(no_Of_Rows));
                no_Of_Attrs++;
            }
            tuples.add(new Tuple(value));
            no_Of_Rows++;
            no_Of_Attrs = 0;
        }
        relation = new Relation(attributes, tuples);
        relation.printTuple();
        return relation;
    }

    //projection of a set of attributes on relation r. Only print distinct tuples
    public Relation project(ArrayList<Attribute> attributes, Relation r) {
        Map<String, Object> value = new HashMap<>();
        ArrayList<Tuple> tuples = new ArrayList<>();
        if(attributes.size() < 1) throw new IllegalArgumentException("Set of attribute must not be empty");
         for(Attribute attr : attributes) {
            if(!r.getAttributeNames().contains(attr.getName()))
                throw new IllegalArgumentException("Set of attribute must be a subset of relation " + r.getName());
        }
        int no_Of_Attrs = 0;
        int no_Of_Rows = 0;


        while(no_Of_Rows < r.getValuesOfColumn(r.getTuples(), attributes.get(0)).size()) {
            while (no_Of_Attrs < attributes.size()) {
                value.put(attributes.get(no_Of_Attrs).getName(), r.getValuesOfColumn(r.getTuples(), attributes.get(no_Of_Attrs)).get(no_Of_Rows));
                no_Of_Attrs++;
            }
//            Tuple t = new Tuple(value);
            if(tuples.size() == 0)
                tuples.add(new Tuple(value));
            else {
                int l = tuples.size();
                boolean isDuplicate = false;
                for(int i = 0; i < l; i++) {
                    if(tuples.get(i).equals(new Tuple(value)))
                        isDuplicate = true;
                }
                if(!isDuplicate)
                    tuples.add(new Tuple(value));
            }
            no_Of_Rows++;
            no_Of_Attrs = 0;
        }
        relation = new Relation(attributes, tuples);
        relation.printTuple();
        return relation;
    }

    public void union() {

    }

    public void intersect() {

    }

    public void differ() {

    }

//    public ArrayList<Tuple> crossJoin () {
//
//    }

//    public ArrayList<Tuple> equiJoiun () {
//
//    }

//    public ArrayList<Tuple> naturalJoin () {
//
//    }
}
