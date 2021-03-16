package RModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Driver {

	public static void main(String[] args) throws Exception {

		Read_Write_Data rw = new Read_Write_Data();
		Query query = new Query();

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

		for(Tuple t : rw.readAllTuplesOfRelation(agents)) {
			try {
				agents.insertTuple(t);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		agents.printRelation();

		for(Tuple t : rw.readAllTuplesOfRelation(customers)) {
			try {
				customers.insertTuple(t);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		customers.printRelation();
//
		for(Tuple t : rw.readAllTuplesOfRelation(orders)) {
			try {
				orders.insertTuple(t);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		orders.printRelation();
		System.out.println("------------------------------------------");
		ArrayList<Attribute> a = new ArrayList<Attribute>();
		a.add(customers.getAttributes().get(1));
		ArrayList<Attribute> b = new ArrayList<Attribute>();
		b.add(agents.getAttributes().get(1));
		b.add(agents.getAttributes().get(4));
		b.add(agents.getAttributes().get(3));
//		System.out.println("Retrieve the names of all customers");
//		query.project(a, customers).printRelation();

		System.out.println("Retrieve the names and phone numbers of all agents in Bangalore");
		query.select(b, agents, agents.getAttributes().get(2), "=", "Bangalore").printRelation();

		System.out.println("Retrieve the names and phone numbers of all agents in London");
		query.select(b, agents, agents.getAttributes().get(2), "=", "London").printRelation();

		System.out.println("Retrieve the names and phone numbers of all agents in Madrid or Bangalore");
		query.union(query.select(b, agents, agents.getAttributes().get(2), "=", "Bangalore"), query.select(b, agents, agents.getAttributes().get(2), "=", "Madrid")).printRelation();

		System.out.println("Test cross join");
		ArrayList<Attribute> c = new ArrayList<Attribute>();
		c.add(agents.getAttributes().get(2));
		c.add(agents.getAttributes().get(1));
		c.add(agents.getAttributes().get(3));
		c.add(agents.getAttributes().get(4));

		query.select(c, agents, agents.getAttributes().get(2), "=", "London").printRelation();
		query.crossJoin(query.select(c, agents, agents.getAttributes().get(2), "=", "London"), query.union(query.select(b, agents, agents.getAttributes().get(2), "=", "Bangalore"), query.select(b, agents, agents.getAttributes().get(2), "=", "Madrid"))).printRelation();


		System.out.println("Test equijoin");
		query.equiJoiun(query.select(c, agents, agents.getAttributes().get(2), "=", "London"), query.union(query.select(b, agents, agents.getAttributes().get(2), "=", "Bangalore"), query.select(b, agents, agents.getAttributes().get(2), "=", "Madrid")), agents.getAttributes().get(3)).printRelation();
//
//		System.out.println("Interesction test");
//		ArrayList<Attribute> c = new ArrayList<Attribute>();
//		c.add(agents.getAttributes().get(0));
//		ArrayList<Attribute> d = new ArrayList<Attribute>();
//		d.add(orders.getAttributes().get(5));
//		query.intersect(query.project(c, agents), query.project(d, orders)).printRelation();
//
//		System.out.println("Difference test");
//		query.project(c, agents).printRelation();
//		query.select(c, agents, agents.getAttributes().get(2), "=", "Bangalore").printRelation();
//		query.differ(query.project(c, agents), query.select(c, agents, agents.getAttributes().get(2), "=", "Bangalore")).printRelation();





//		agents.updateTuple(agents.getAttributes().get(0), "=", "A007", agents.getAttributes().get(0), "A017");
//		orders.updateTuple(orders.getAttributes().get(0), "=", 200222, orders.getAttributes().get(1), 3400);
//		orders.updateTuple(orders.getAttributes().get(0), "=", 200222, orders.getAttributes().get(4), "C1000");
//
//		agents.printRelation();
//		customers.printRelation();
//		orders.printRelation();
//
//		System.out.println("------------------------------------------");
//		orders.deleteTuple(orders.getAttributes().get(0), "=", 200222);
//		agents.deleteTuple(agents.getAttributes().get(0), "=", "A017");
//
//
//		agents.printRelation();
//		customers.printRelation();
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
