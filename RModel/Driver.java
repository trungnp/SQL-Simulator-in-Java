package RModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Driver {

	public static void main(String[] args) throws Exception {


		Read_Write_Data rw = new Read_Write_Data();
		Query query = new Query();
		//create a map to store foreign keys of ORDERS
		HashMap<Relation, Attribute> orders_fk = new HashMap<>();

		//create an empty relation AGENTS with primary key is AGENT_CODE
		Relation agents = new Relation("AGENTS", getAgentsAttributes(), getAgentsAttributes().get(0));

		//create an empty relation CUSTOMERS with primary key is CUST_CODE
		Relation customers = new Relation("CUSTOMERS", getCustomersAttributes(), getCustomersAttributes().get(0));

		//store foreign key attribute of ORDERS and what relation that attribute comes from
		orders_fk.put(agents, getAgentsAttributes().get(0)); // ORDERS(AGENT_CODE) references AGENTS(AGENT_CODE)
		orders_fk.put(customers, getCustomersAttributes().get(0)); //ORDERS(CUST_CODE) references CUSTOMERS(CUST_CODE)

		//create an empty relation ORDERS with primary key is ORD_NUM and foreign keys are AGENT_CODE and CUST_CODE
		Relation orders = new Relation("ORDERS", getOrdersAttributes(), getOrdersAttributes().get(0), orders_fk);

		ArrayList<String> constraints = new ArrayList<>();
		constraints.add("AGENT_CODE is the primary key of AGENTS");
		constraints.add("CUST_CODE is the primary key of CUSTOMERS");
		constraints.add("ORD_NUM is the primary key of ORDERS");
		constraints.add("ORDERS(CUST_CODE) references CUSTOMERS(CUST_CODE)");
		constraints.add("ORDERS(AGENT_CODE) references AGENTS(CUST_CODE)");
		Set<Relation> relations = new HashSet<>();
		relations.add(agents);
		relations.add(customers);
		relations.add(orders);

		//Create a schema SALES with 3 relations and constraints above
		Schema schema = new Schema("SALES", relations, constraints);
		schema.printSchema();
		schema.printConstraints();
		System.out.println();

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

		for(Tuple t : rw.readAllTuplesOfRelation(orders)) {
			try {
				orders.insertTuple(t);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		orders.printRelation();
		System.out.println("------------------------------------------");

//		System.out.println("Retrieve the names of all customers");
//		ArrayList<Attribute> a = new ArrayList<Attribute>();
//		a.add(customers.getAttributes().get(1));
//		query.project(a, customers).printRelation(); //project CUST_NAME column of relation CUSTOMERS

//		System.out.println("Retrieve the names and phone numbers of all agents in Bangalore");
//		ArrayList<Attribute> b = new ArrayList<Attribute>();
//		b.add(agents.getAttributes().get(1));
//		b.add(agents.getAttributes().get(4));
//		/*
//		select AGENT_NAME, PHONE_NO
//		from AGENTS
//		where WORKING_AREA = 'Bangalore';
//		 */
//		query.select(b, agents, agents.getAttributes().get(2), "=", "Bangalore").printRelation();
//
		//Natural join three relation ORDERS, CUSTOMERS, and AGENTS
//		Relation orders_customers = query.naturalJoin(orders, customers);
//		Relation orders_customers_agents = query.naturalJoin(orders_customers, agents);
//		System.out.println("Natural join three relation ORDERS, CUSTOMERS, and AGENTS.");
//		orders_customers_agents.printRelation();
//		ArrayList<Attribute> a = new ArrayList<>();
//		a.add(orders_customers_agents.getAttributes().get(6));
//		a.add(orders_customers_agents.getAttributes().get(11));
//		System.out.println("Retrieve the names of all customers and agents");
//		query.project(a, orders_customers_agents).printRelation(); //project columns CUST_NAME and AGENT_NAME of the joined relation orders_customers_agents above

		//Natural join two relation ORDERS and CUSTOMERS
//		Relation orders_customers = query.naturalJoin(orders, customers);
//		System.out.println("Natural join two relation ORDERS and CUSTOMERS");
//		orders_customers.printRelation();
//
//		/*
//		select *
//		from orders_customers
//		where CUST_COUNTRY = "USA"
//		 */
//		Relation cust_from_USA = query.select(orders_customers.getAttributes(), orders_customers, orders_customers.getAttributes().get(8), "=", "USA");
//		System.out.println("Retrieve all orders made by customers from USA");
//		cust_from_USA.printRelation();
//
//
//		System.out.println("Retrieve the orders of all customers who are from USA");
//		ArrayList<Attribute> a = new ArrayList<>();
//		a.add(cust_from_USA.getAttributes().get(0));
//		query.project(a, cust_from_USA).printRelation(); //project column CUST_COUNTRY of relation cust_from_USA above

//
//		System.out.println("Test cross join");
//		ArrayList<Attribute> c = new ArrayList<Attribute>();
//		c.add(agents.getAttributes().get(2));
//		c.add(agents.getAttributes().get(1));
//		c.add(agents.getAttributes().get(3));
//		c.add(agents.getAttributes().get(4));
//
//		query.select(c, agents, agents.getAttributes().get(2), "=", "London").printRelation();
//		query.crossJoin(query.select(c, agents, agents.getAttributes().get(2), "=", "London"), query.union(query.select(b, agents, agents.getAttributes().get(2), "=", "Bangalore"), query.select(b, agents, agents.getAttributes().get(2), "=", "Madrid"))).printRelation();
//
//
//		System.out.println("Test equijoin");
//		query.equiJoin(query.select(c, agents, agents.getAttributes().get(2), "=", "London"), query.union(query.select(b, agents, agents.getAttributes().get(2), "=", "Bangalore"), query.select(b, agents, agents.getAttributes().get(2), "=", "Madrid")), agents.getAttributes().get(3)).printRelation();
//
//		System.out.println("Test natural join");
//		query.naturalJoin(orders, agents).printRelation();

//		Relation select = query.select(orders.getAttributes(), orders, orders.getAttributes().get(1), ">", 4000);
//		select.printRelation();
//		Relation naturalJoin = query.naturalJoin(select, agents);
//		naturalJoin.printRelation();
//		Relation naturalJoin1 = query.naturalJoin(naturalJoin, customers);
//		naturalJoin1.printRelation();
//		ArrayList<Attribute> arr = new ArrayList<>();
//		System.out.println(naturalJoin.getAttributes().toString());
//		arr.add(naturalJoin1.getAttributes().get(10));
//		arr.add(naturalJoin1.getAttributes().get(6));
//		query.project(arr,naturalJoin1).printRelation();
//
//
//		ArrayList<Attribute> test = new ArrayList<>();
//		test.add(customers.getAttributes().get(5));
//		query.select(test, customers).printRelation();
//
//		System.out.println("Test aggregate");
//		query.select(customers.getAttributes().get(5), customers, "min").printRelation();
//		query.select(customers.getAttributes().get(5), customers, "max").printRelation();
//		query.select(customers.getAttributes().get(5), customers, "avg").printRelation();
//		query.select(customers.getAttributes().get(5), customers, "sum").printRelation();
//		query.select(customers.getAttributes().get(5), customers, "count").printRelation();
//
		//Natural join 3 relations CUSTOMERS, ORDERS, and AGENTS
		Relation customers_orders_agents = query.naturalJoin(customers, query.naturalJoin(orders,agents));
		customers_orders_agents.printRelation();

		//Count number of customers for each agent
		Relation count_cust = query.select_groupby("count", customers_orders_agents.getAttributes().get(0), customers_orders_agents, customers_orders_agents.getAttributes().get(10));

		//Sum total order amount for each agent
		Relation sum_ord_amount = query.select_groupby("sum", customers_orders_agents.getAttributes().get(7), customers_orders_agents, customers_orders_agents.getAttributes().get(10));

		count_cust.printRelation();
		sum_ord_amount.printRelation();

		ArrayList<Attribute> bbb = new ArrayList<>();
		bbb.add(customers_orders_agents.getAttributes().get(10));
		bbb.add(customers_orders_agents.getAttributes().get(14));
		//Get phone number of each agent
		Relation AGENT_PHONE = query.project(bbb, customers_orders_agents);

		//List phone number, number of customers, and total order amount for each agent
		query.naturalJoin(AGENT_PHONE, query.naturalJoin(count_cust, sum_ord_amount)).printRelation();



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
	


	/*
	get all the attributes and domains of relation AGENTS

	return - an arraylist of attributes
	 */
	public static ArrayList<Attribute> getAgentsAttributes() {
		ArrayList<Attribute> agentAttrs = new ArrayList<>();
		agentAttrs.add(new Attribute("AGENT_CODE", String.class));
		agentAttrs.add(new Attribute("AGENT_NAME", String.class));
		agentAttrs.add(new Attribute("WORKING_AREA", String.class));
		agentAttrs.add(new Attribute("COMMISSION_PER", Integer.class));
		agentAttrs.add(new Attribute("PHONE_NO", Integer.class));

		return agentAttrs;
    }

	/*
    get all the attributes and domains of relation CUSTOMERS

    return - an arraylist of attributes
     */
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

	/*
	get all the attributes and domains of relation ORDERS

	return - an arraylist of attributes
	 */
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
