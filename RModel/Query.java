package RModel;

import java.util.*;

public class Query {
    private Relation relation;

    //select * from relation r
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
//        relation.printTuple();
        return relation;
    }

    //select a set of attributes from relation r with a condition
    public Relation select(ArrayList<Attribute> attributes, Relation r, Attribute whereAttrs, String condition, Object operand) throws Exception {
        Relation tmp = new Relation(r.getName(), r.getAttributes(), r.getTuples(), r.getPK(), r.getFKs());
        if(condition.equals("=")) {
            tmp.getTuples().removeIf(t->!t.getAttribute(whereAttrs.getName()).equals(operand));
        } else {
            tmp.getTuples().removeIf(t -> t.getAttribute(whereAttrs.getName()).equals(operand));
            if(condition.equals("<")) {
                if(operand instanceof Integer) {
                    tmp.getTuples().removeIf(t -> (Integer)t.getAttribute(whereAttrs.getName()) > (Integer)operand);
                } else if(operand instanceof String) {
                    tmp.getTuples().removeIf(t -> t.getAttribute(whereAttrs.getName()).toString().compareTo(operand.toString()) > 0);
                }
            } else if (condition.equals(">")) {
                if(operand instanceof Integer) {
                    tmp.getTuples().removeIf(t -> (Integer)t.getAttribute(whereAttrs.getName()) < (Integer)operand);
                } else if(operand instanceof String) {
                    tmp.getTuples().removeIf(t -> t.getAttribute(whereAttrs.getName()).toString().compareTo(operand.toString()) < 0);
                }
            }
        }

        Relation result = new Relation(attributes, tmp.getTuples());
        return result;
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
//        relation.printTuple();
        return relation;
    }

    //Union two relations r1 and r2
    public Relation union(Relation r1, Relation r2) {
        if(r1.getAttributes().size() != r2.getAttributes().size()) throw new IllegalArgumentException("Numbers of attributes do not match.");
        else {
            for(int i = 0; i < r1.getTuples().size(); i++) {
                if(!r1.getAttributes().get(i).equals(r2.getAttributes().get(i))) throw new IllegalArgumentException("Attribute types do not match.");
            }
        }

        r1.getTuples().addAll(r2.getTuples());
//        Relation tmp = new Relation(r1.getAttributes(), r1.getTuples());

        return this.project(r1.getAttributes(), r1);
    }

    //Intersect two relation r1 and r2
    public Relation intersect(Relation r1, Relation r2) {
        if(r1.getAttributes().size() != r2.getAttributes().size()) throw new IllegalArgumentException("Numbers of attributes do not match.");
        else {
            for(int i = 0; i < r1.getAttributes().size(); i++) {
                if(!r1.getAttributes().get(i).equals(r2.getAttributes().get(i))) throw new IllegalArgumentException("Attribute types do not match.");
            }
        }

        ArrayList<Tuple> smallerTuples = r1.getTuples().size() < r2.getTuples().size() ? r1.getTuples() : r2.getTuples();
        ArrayList<Tuple> biggerTuples = r1.getTuples().size() < r2.getTuples().size() ? r2.getTuples() : r1.getTuples();
        ArrayList<Tuple> result = new ArrayList<>();

        for(int i = 0; i < smallerTuples.size(); i++) {
            for(int j = 0; j < biggerTuples.size(); j++) {
                if(biggerTuples.get(j).equals(smallerTuples.get(i))) {
                    result.add(smallerTuples.get(i));
                    break;
                }
            }
        }

        return new Relation(r1.getAttributes(), result);
    }

    //Return new relation containing tuples which are in relation r1 and not in relation r2
    public Relation differ(Relation r1, Relation r2) {
        if(r1.getAttributes().size() != r2.getAttributes().size()) throw new IllegalArgumentException("Numbers of attributes do not match.");
        else {
            for(int i = 0; i < r1.getAttributes().size(); i++) {
                if(!r1.getAttributes().get(i).equals(r2.getAttributes().get(i))) throw new IllegalArgumentException("Attribute types do not match.");
            }
        }

//        ArrayList<Tuple> smallerTuples = r1.getTuples().size() < r2.getTuples().size() ? r1.getTuples() : r2.getTuples();
//        ArrayList<Tuple> biggerTuples = r1.getTuples().size() < r2.getTuples().size() ? r2.getTuples() : r1.getTuples();
        ArrayList<Tuple> result = new ArrayList<>();

        for(int i = 0; i < r1.getTuples().size(); i++) {
            if(i >= r2.getTuples().size()) break;
            Boolean isInBoth = false;
            for(int j = 0; j < r2.getTuples().size(); j++) {
                if(r1.getTuples().get(i).equals(r2.getTuples().get(j))) {
                    isInBoth = true;
                    break;
                }
            }
            if(!isInBoth)
                result.add(r1.getTuples().get(i));
        }

        return new Relation(r1.getAttributes(), result);
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
