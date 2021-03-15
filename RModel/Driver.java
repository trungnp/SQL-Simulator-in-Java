package RModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Driver {

	public static void main(String[] args) throws Exception {

		Read_Write_Data rw = new Read_Write_Data();

		HashMap<Relation, Attribute> agents_fk = new HashMap<>();

		Relation agents = new Relation("AGENTS", getAgentsAttributes(), getAgentsAttributes().get(0));
		Relation customers = new Relation("CUSTOMERS", getCustomersAttributes(), getCustomersAttributes().get(0));
		agents_fk.put(agents, getAgentsAttributes().get(0));
		agents_fk.put(customers, getCustomersAttributes().get(0));
		Relation orders = new Relation("ORDERS", getOrdersAttributes(), getOrdersAttributes().get(0), agents_fk);

//		rw.createRelation(agents);
//		rw.createRelation(customers);
//		rw.createRelation(orders);

//		System.out.println(rw.readAllTuplesOfRelation(agents));

//		for(Tuple t : rw.readAllTuplesOfRelation(agents)) {
//			try {
//				agents.insertTuple(t);
//			} catch (IllegalArgumentException e) {
//				e.printStackTrace();
//			}
//		}
//		agents.printRelation();

		for(Tuple t : rw.readAllTuplesOfRelation(customers)) {
			try {
				customers.insertTuple(t);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				continue;
			}
		}
		customers.printRelation();

//		for(Tuple t : rw.readAllTuplesOfRelation(orders)) {
//			try {
//				orders.insertTuple(t);
//			} catch (IllegalArgumentException e) {
//				e.printStackTrace();
//			}
//		}
//		orders.printRelation();
	}
	



	public static ArrayList<Attribute> getAgentsAttributes() {
		ArrayList<Attribute> agentAttrs = new ArrayList<>();
		agentAttrs.add(new Attribute("AGENT_CODE", String.class));
		agentAttrs.add(new Attribute("AGENT_NAME", String.class));
		agentAttrs.add(new Attribute("WORKING_AREA", String.class));
		agentAttrs.add(new Attribute("COMMISSION_PER", Integer.class));
		agentAttrs.add(new Attribute("PHONE_NO", Integer.class));

		return agentAttrs;
    }

	public static ArrayList<Attribute> getCustomersAttributes() {
		ArrayList<Attribute> customerAttrs = new ArrayList<>();
		customerAttrs.add(new Attribute("CUST_CODE", String.class));
		customerAttrs.add(new Attribute("CUST_NAME", String.class));
		customerAttrs.add(new Attribute("CUST_CITY", String.class));
		customerAttrs.add(new Attribute("CUST_COUNTRY", String.class));
		customerAttrs.add(new Attribute("GRADE", Integer.class));
		customerAttrs.add(new Attribute("BALANCE", Integer.class));

		return customerAttrs;
	}

	public static ArrayList<Attribute> getOrdersAttributes() {
		ArrayList<Attribute> orderAttrs = new ArrayList<>();
		orderAttrs.add(new Attribute("ORD_NUM", Integer.class));
		orderAttrs.add(new Attribute("ORD_AMOUNT", Integer.class));
		orderAttrs.add(new Attribute("ADVANCE_AMOUNT", Integer.class));
		orderAttrs.add(new Attribute("ORD_DATE", String.class));
		orderAttrs.add(new Attribute("CUST_CODE", String.class));
		orderAttrs.add(new Attribute("AGENT_CODE", String.class));

		return orderAttrs;
	}
}
