package RModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Tuple {

	private Map<String, Object> tValues;
	
	public Tuple(Map<String, Object> attrValues) {
		
		this.tValues = new HashMap<>(attrValues);
		
	}
	
	public Object getAttribute(String attrName) {
		return tValues.get(attrName);
	}
	
	public String toString() {
		String str = "";
		for (Map.Entry<String, Object> entry : tValues.entrySet()) {
		    str += entry.getKey()+" : "+entry.getValue() + '\t';
		}
		return str;
	}
	
	public Map<String, Object> getTupleValues(){
		return tValues;
	}
	
	public Set<String> getAttributeNames(){
		return tValues.keySet();
		
	}

	//replace value of attribute attr by new value
	public void replaceAttributeValue(Attribute attr, Object value) {
		this.tValues.replace(attr.getName(), value);
	}

	public ArrayList<Object> getAttributeValues(){
		return new ArrayList<Object>(tValues.values());
	}

	//overwrite equals method to compare tuples
	public boolean equals(Tuple t) {
		for(Map.Entry<String, Object> entry : tValues.entrySet()) {
			if(!entry.getValue().equals(t.getAttribute(entry.getKey())))
				return false;
		}
		return true;
	}

	//delete a value of the tuple
	public void deleteAnAttribute(String key) {
		tValues.remove(key);
	}

}
