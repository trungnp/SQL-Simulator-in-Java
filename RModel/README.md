------Create relation------

/**
Create a new relation with a name, a set of attributes, a set of tuples, a primary key, and a set of foreign keys
If any constraint is violated, it will throw a message indicating which constraint is violated
*/
Relation(String name, Collection<Attribute> attrs, Collection<Tuple> tuples, Attribute PK, Map<Relation, Attribute> FKs)

/**
Create a new empty relation with a name, a set of attribute,  a primary key, and a set of foreign key.
*/
Relation(String name, Collection<Attribute> attrs, Attribute PK, Map<Relation, Attribute> FKs)

/**
Create a new empty relation with a name, a set of attribute,  and a primary key
*/
Relation(String name, Collection<Attribute> attrs, Attribute PK)

/**
Create a new empty relation with a name, a set of attribute,  a set of tuples, and a primary key
*/
Relation(String name, Collection<Attribute> attrs, Collection<Tuple> tuples, Attribute PK)

Example:
//create an empty relation CUSTOMERS with primary key is CUST_CODE = getCustomersAttributes().get(0)
Relation customers = new Relation("CUSTOMERS", getCustomersAttributes(), getCustomersAttributes().get(0));

------Print a relation------
printRelation()

Example:
//print relation agents
agents.printRelation()


------Create schema------
/**
Create a schema with a set of relation and a set of user-defined constraints (string type)
*/
Schema(String name, Set<Relation> relations, ArrayList<String> constraints)


Example:
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

------Insert a new tuple into a relation------
/**
insert a new tuple into the relation. Throw a message indicating whether inserting successful or failed.
after insertion, check whether any constraint is violated or not.
*/
insertTuple(Tuple newTuple)

Example:
//insert a new tuple into relation agents
agents.insertTuple(newTuple);

------Delete tuples from a relation------
/**
Delete tuples based on condition applying on attribute attr
After deleting a tuple, it needs to check whether any foreign of other relation is referencing to the deleted primary.
If yes, change its value to null.
*/
deleteTuple(Attribute attr, String condition, Object operand)

Example:
//detele tuples which have ORD_NUM = 200222
orders.deleteTuple(orders.getAttributes().get(0), "=", 200222);

------Update tuples of a relation------
/**
Update a tuple based on condition and operand applying on attribuet whereAttr
After update, it will check whether any constraint is violated or not.
If no constraint violated, it will continue to check whether any foreign key of other relations is affected or not.
If yes, change its value to the new value that just has been updated.
*/
updateTuple(Attribute whereAttr, String condition, Object operand, Attribute setAttr, Object newValue)

Example:
//update AGENT_CODE to "A017" of tuples which have AGENT_CODE = "A007"
agents.updateTuple(agents.getAttributes().get(0), "=", "A007", agents.getAttributes().get(0), "A017");


------Project Query------
/*
Project a set of attributes on relation r. Only print distinct tuples
Return result as a relation
*/
project(ArrayList<Attribute> attributes, Relation r)

Example:
ArrayList<Attribute> a = new ArrayList<Attribute>();
a.add(customers.getAttributes().get(1));
//project CUST_NAME column on relation customers
query.project(a, customers).printRelation();

------Select Query------
/**
Select a set of attributes from relation r
Return result as a relation
*/
select(ArrayList<Attribute> attributes, Relation r)

Example:
ArrayList<Attribute> test = new ArrayList<>();
test.add(customers.getAttributes().get(5));
//select BALANCE column from relation customers
query.select(test, customers);

/**
Select a set of attributes from relation r with a condition
Return result as a relation
*/
select(ArrayList<Attribute> attributes, Relation r, Attribute whereAttrs, String condition, Object operand)

Example:
/**
select AGENT_NAME, PHONE_NO
from AGENTS
where WORKING_AREA = 'Bangalore';
*/
ArrayList<Attribute> b = new ArrayList<Attribute>();
b.add(agents.getAttributes().get(1));
b.add(agents.getAttributes().get(4));
query.select(b, agents, agents.getAttributes().get(2), "=", "Bangalore");

------Select Query With Aggregate Function------
/**
Select aggrFunction(attr) from r. Min, Max, Avg, Sum work for only numerical column
Return result as a relation with only one attribute aggrFunction_attr.getName()
*/
select(Attribute attr, Relation r, String aggrFunction)

Example:
//find minimum balance from relation customers
query.select(customers.getAttributes().get(5), customers, "min");

------Select Query With Groupby and Aggregate Function------
/**
Group by the relation r by the attribute groupbyAttr and then apply aggregate function aggrFunction on the attribute selectAttr
Return result as a relation with two attributes groupbyAttr and aggrFunction_selectAttr.getName()
*/
select_groupby(String aggrFunction, Attribute selectAttr, Relation r, Attribute groupbyAttr)

Example:
//Count number of customers for each agent
query.select_groupby("count", customers_orders_agents.getAttributes().get(0), customers_orders_agents, customers_orders_agents.getAttributes().get(10));

------Union two relations------
/*
Union two relations r1 and r2
return result as a relation
*/
union(Relation r1, Relation r2)

Example:
ArrayList<Attribute> b = new ArrayList<Attribute>();
b.add(agents.getAttributes().get(1));
b.add(agents.getAttributes().get(4));
//Get name of phone number of agents in Bangalore
Relation bangalore = query.select(b, agents, agents.getAttributes().get(2), "=", "Bangalore");
//Get name of phone number of agents in Madrid
Relation madrid = query.select(b, agents, agents.getAttributes().get(2), "=", "Madrid");
//Get name and phone number of agents who either in Bangalore or Madrid
query.union(bangalore, madrid);

------Intersect two relations------
/*
Intersect two relation r1 and r2
Return result as a relation
*/
intersect(Relation r1, Relation r2)

Example:
ArrayList<Attribute> c = new ArrayList<Attribute>();
c.add(agents.getAttributes().get(0));
ArrayList<Attribute> d = new ArrayList<Attribute>();
d.add(orders.getAttributes().get(5));
Relation r1 = query.project(c, agents); //project column AGENT_CODE on relation agents
Relation r2 = query.project(d, orders); //project column AGENT_CODE on relation orders
query.intersect(r1, r2); //find AGENT_CODE that appear on both relation r1 and r2

------Differ two relations------
ArrayList<Attribute> c = new ArrayList<Attribute>();
c.add(agents.getAttributes().get(0));
Relation r1 = query.project(c, agents); //project column AGENT_CODE on relation agents
Relation r2 = query.select(c, agents, agents.getAttributes().get(2), "=", "Bangalore"); //find AGENT_CODE in Bangalore
query.differ(r1, r2); //find AGENT_CODE that not in Bangalore

------Cross join two relations------
/*
cross join two relation r1 and r2
return result as a relation
*/
crossJoin(Relation r1, Relation r2)

Example:
ArrayList<Attribute> b = new ArrayList<Attribute>();
b.add(agents.getAttributes().get(1));
b.add(agents.getAttributes().get(4));
ArrayList<Attribute> c = new ArrayList<Attribute>();
c.add(agents.getAttributes().get(1));
c.add(agents.getAttributes().get(4));
//select AGENT_CODE and PHONE_NO of agent in London
Relation r1 = query.select(c, agents, agents.getAttributes().get(2), "=", "London");
//select AGENT_CODE and PHONE_NO of agent in Bangalore
Relation r2 = query.select(b, agents, agents.getAttributes().get(2), "=", "Bangalore");
query.crossJoin(r1, r2); //cross join two relation r1 and r2 above


------Equi-join two relations------
/*
equi-join two relations r1 and r2 on the attribute onAttr
return result as a relation
*/
equiJoin(Relation r1, Relation r2, Attribute onAttr)

Example:
ArrayList<Attribute> b = new ArrayList<Attribute>();
b.add(agents.getAttributes().get(1));
b.add(agents.getAttributes().get(4));
ArrayList<Attribute> c = new ArrayList<Attribute>();
c.add(agents.getAttributes().get(1));
c.add(agents.getAttributes().get(4));
//select AGENT_CODE and PHONE_NO of agent in London
Relation r1 = query.select(c, agents, agents.getAttributes().get(2), "=", "London");
//select AGENT_CODE and PHONE_NO of agent in Bangalore
Relation r2 = query.select(b, agents, agents.getAttributes().get(2), "=", "Bangalore"); 
query.equiJoin(r1, r2, agents.getAttributes().get(3));

------Natural join two relations------
/*
natural join two relation r1 and r2
return result as a relation
*/
naturalJoin(Relation r1, Relation r2)

Example:
query.naturalJoin(orders, agents); //natural join two relation orders and agents

