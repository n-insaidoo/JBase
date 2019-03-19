# JBase
JBase is a simple `Java` project that primarily mimics a `MySQL` database using a binary file as means of storage.


JBase consists of three Classes (`JBase`, `Table` and `Row`) related through association and allows to:

* Create tables of data and store column names and data types
```Java
    JBase db = new JBase("tests.dat");
    db.createTable("inventory", "item TEXT, prod_id INT, price DOUBLE, stock SMALLINT");
```
* Insert rows
```Java
    db.insert("inventory", "'Pencil', 153852, 0.49, 800");
    db.insert("inventory", "'Sponge', 648375, 0.99, 50");
```

* Execute a simple Select function to fetch data
```Java
    db.select("inventory", "item, stock, price");
```

The Select function supports the Where clause, hence it can return more selective data according to the query.
```Java
    db.select("inventory", "item, stock, price", "item = '1' OR price > 0");
```
Queries are provided in the form of Stings following a specific format in order to be parsed by the application through the usage of specific Regular Expression patterns (e.g. a pattern to detect and select the two operands of a logic operation).
