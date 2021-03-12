package RModel;

public class Attribute {
    private String name;
    private Class type;

    public Attribute(String name, Class type) {
        this.name = name;
        if(type.equals(String.class) | type.equals(Integer.class)) {
        	this.type = type;
        } else
            System.out.println("Attribute type must be either Integer of String");
    }
    
    public String getName() {
    	return name;
    }
    
    public Class getType() {
    	return this.type;
    }
    
    public String toString() {
    	return String.format("Attribute %s <{%s}>", this.name, this.type.toString());
    }

//    public boolean equals(Attribute a) {
//        return this.getName().equals(a.getName());
//    }
}