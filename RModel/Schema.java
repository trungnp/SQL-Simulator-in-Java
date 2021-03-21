package RModel;

import java.util.*;

public class Schema {
    String name;
    Set<Relation> relations;
    ArrayList<String> constraints;

    //create a schema with a set of relation and a set of user-defined constraints (string type)
    public Schema(String name, Set<Relation> relations, ArrayList<String> constraints) {
        this.name = name;
        this.relations = relations;
        this.constraints = constraints;
    }

    public void printSchema(){
        System.out.printf("Schema name: %s%n", this.name);
        for(Relation r : this.relations)
            r.printRelation();
    }

    public void printConstraints() {
        for(String s : constraints)
            System.out.println(s);
    }
}
