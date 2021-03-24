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

		//insert tuples from file into relation AGENTS
		for(Tuple t : rw.readAllTuplesOfRelation(agents)) {
			try {
				agents.insertTuple(t);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		agents.printRelation();

		//insert tuples from file into relation CUSTOMERS
		for(Tuple t : rw.readAllTuplesOfRelation(customers)) {
			try {
				customers.insertTuple(t);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		customers.printRelation();

		//insert tuples from file into relation ORDERS
		for(Tuple t : rw.readAllTuplesOfRelation(orders)) {
			try {
				orders.insertTuple(t);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		orders.printRelation();
		System.out.println("------------------------------------------");

		System.out.println("1. Retrieve the names of all customers");
		ArrayList<Attribute> a = new ArrayList<Attribute>();
		a.add(customers.getAttributes().get(1));
		query.project(a, customers).printRelation(); //project CUST_NAME column of relation CUSTOMERS
		System.out.println("------------------------------------------");

		System.out.println("2. Retrieve the names and phone numbers of all agents in Bangalore");
		ArrayList<Attribute> b = new ArrayList<Attribute>();
		b.add(agents.getAttributes().get(1));
		b.add(agents.getAttributes().get(4));
//		/*
//		select AGENT_NAME, PHONE_NO
//		from AGENTS
//		where WORKING_AREA = 'Bangalore';
//		 */
		query.select(b, agents, agents.getAttributes().get(2), "=", "Bangalore").printRelation();
		System.out.println("------------------------------------------");


		//Natural join three relation ORDERS, CUSTOMERS, and AGENTS
		Relation orders_customers = query.naturalJoin(orders, customers);
		Relation orders_customers_agents = query.naturalJoin(orders_customers, agents);
//		System.out.println("Natural join three relation ORDERS, CUSTOMERS, and AGENTS.");
//		orders_customers_agents.printRelation();
		ArrayList<Attribute> cust_agent = new ArrayList<>();
		cust_agent.add(orders_customers_agents.getAttributes().get(6));
		cust_agent.add(orders_customers_agents.getAttributes().get(11));
		System.out.println("3. Retrieve the names of all customers and agents");
		query.project(cust_agent, orders_customers_agents).printRelation(); //project columns CUST_NAME and AGENT_NAME of the joined relation orders_customers_agents above
		System.out.println("------------------------------------------");

		//Natural join two relation ORDERS and CUSTOMERS
//		Relation orders_customers = query.naturalJoin(orders, customers);
//		System.out.println("Natural join two relation ORDERS and CUSTOMERS");
//		orders_customers.printRelation();
//		/*
//		select *
//		from orders_customers
//		where CUST_COUNTRY = "USA"
//		 */
		Relation cust_from_USA = query.select(orders_customers.getAttributes(), orders_customers, orders_customers.getAttributes().get(8), "=", "USA");
//		System.out.println("Retrieve all orders made by customers from USA");
//		cust_from_USA.printRelation();


		System.out.println("4. Retrieve the orders of all customers who are from USA");
		ArrayList<Attribute> cust_country = new ArrayList<>();
		cust_country.add(cust_from_USA.getAttributes().get(0));
		cust_country.add(cust_from_USA.getAttributes().get(8));
		query.project(cust_country, cust_from_USA).printRelation(); //project columns ORD_NUM CUST_COUNTRY of relation cust_from_USA above
		System.out.println("------------------------------------------");
//
		//Natural join 3 relations CUSTOMERS, ORDERS, and AGENTS
		Relation customers_orders_agents = query.naturalJoin(customers, query.naturalJoin(orders,agents));
//		customers_orders_agents.printRelation();

		//Count number of customers for each agent
		Relation count_cust = query.select_groupby("count", customers_orders_agents.getAttributes().get(0), customers_orders_agents, customers_orders_agents.getAttributes().get(10));

		//Sum total order amount for each agent
		Relation sum_ord_amount = query.select_groupby("sum", customers_orders_agents.getAttributes().get(7), customers_orders_agents, customers_orders_agents.getAttributes().get(10));

//		count_cust.printRelation();
//		sum_ord_amount.printRelation();

		ArrayList<Attribute> agent_phone_no = new ArrayList<>();
		agent_phone_no.add(customers_orders_agents.getAttributes().get(10));
		agent_phone_no.add(customers_orders_agents.getAttributes().get(11));
		agent_phone_no.add(customers_orders_agents.getAttributes().get(14));
		//Get name and phone number of each agent
		Relation agent_phone = query.project(agent_phone_no, customers_orders_agents);
		agent_phone.printRelation();

		System.out.println("5. Retrieve the total number of customers and total order amount (ORD_AMOUNT) for each\n" +
				"agent. List names and phone numbers for agents.");
		//List phone number, number of customers, and total order amount for each agent
		query.naturalJoin(agent_phone, query.naturalJoin(count_cust, sum_ord_amount)).printRelation();
		System.out.println("------------------------------------------");

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
