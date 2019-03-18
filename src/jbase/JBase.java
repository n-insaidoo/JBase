/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jbase;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.*;

/**
 *
 * @author Nana
 */
public class JBase implements Serializable {

    private List<Table> tables;
    private String fileName;
    private final String operatorRegExp = "(\\s<[=]?\\s|\\s>[=]?\\s|\\s[!]?=\\s|\\sEQUALSIGNORECASE\\s|\\sCONTAINS\\s|\\sCONTAINSIGNORECASE\\s)";

    /**
     * Constructor
     *
     * @param fileName Binary file's path
     */
    public JBase(String fileName) {
        this.fileName = fileName;
        if ((new File(fileName).exists())) {
            load();
        } else {
            tables = new ArrayList<>();
        }
    }

    /**
     * @param tableName This argument is a String. It can contain letters,
     * digits, and underscores. It may not contain spaces or other special
     * characters. It must be unique. You can not have two tables with the same
     * name. The table name is not case sensitive (cats and CATS are the same
     * table name). A field name can not be one of the reserved words for this
     * project (tinyint, smallint, int, bigint, double, text, and, or,
     * equalsignorecase, contains, containsignorecase).
     * @param fields This argument is a String. Each field has two parts;
     * fieldname and type. The fieldname (like a tablename) may contain letters,
     * digits, and underscores. It can not contain spaces or other special
     * characters. Field names must be unique. Field names are not case
     * sensitive. A field name can not be one of the reserved words for this
     * project (tinyint, smallint, int, bigint, double, text, and, or,
     * equalsignorecase, contains, containsignorecase). The types must be one of
     * the following: (See specification for types) SQL types are not case
     * sensitive. The fieldname and type are separated by a space. Fields are
     * separated by commas.
     */
    public void createTable(String tableName, String fields) {
        tables.add(new Table(tableName, fields));
        persist();
    }

    /**
     * Methods that returns a Table object searching by a given table name
     *
     * @param tableName Table's name
     * @return Table object corresponding to the table name
     */
    private Table getTableByName(String tableName) {
        int index = 0;
        for (Table tb : tables) {
            if (tb.getName().equals(tableName)) {
                break;
            }
            index++;
        }
        tables.get(index).setParentDb(this);
        return tables.get(index);
    }

    public void insert(String tableName, String values) {
        Table tb = getTableByName(tableName.toLowerCase());
        tb.addRow(values);

        persist();
    }

    /**
     * Method to retrieve the logic operator
     *
     * @param regExp Regular Expression pattern
     * @param originalClause The initial WHERE clause containing the operands
     * and the logic operation
     * @return The logic operator
     */
    private String getLogicOperator(String regExp, String originalClause) {
        Pattern pat = Pattern.compile(regExp);
        Matcher matcher = pat.matcher(originalClause);
        String operator = null;
        while (matcher.find()) {
            operator = matcher.group();
            break;
        }

        return operator.trim();
    }

    /**
     * Method to infer or parse the operands to a Java-suitable form for
     * following operations
     *
     * @param ends Operands in a Sting and/or un-inferred format
     * @param row Affected row
     * @param availableColumns Row's columns involved
     * @return Array of generic elements containing the operands
     */
    private Object[] workOutEnds(String[] ends, Row row, List<String> availableColumns) {
        Object[] fEnds = new Object[2];
        String numberRegex = "^\\d+(\\.\\d)?\\d*$";
        for (int i = 0; i < 2; i++) {
            if (ends[i].matches(numberRegex)) {
                fEnds[i] = Double.parseDouble(ends[i]);
            } else {
                if (availableColumns.contains(ends[i].toLowerCase())) {
                    int index = availableColumns.indexOf(ends[i].toLowerCase());
                    fEnds[i] = row.getColumn(index);
                    if (((String) fEnds[i]).matches(numberRegex)) {
                        fEnds[i] = Double.parseDouble(((String) fEnds[i]));
                    }
                    else{
                        fEnds[i] = ((String) fEnds[i]).split("'")[1];
                    }
                } else {
                    fEnds[i] = ends[i].split("'")[1];
                }
            }
        }
        return fEnds;
    }

    /**
     * Method to work out the outcome between two operands
     *
     * @param ends Array containing 2 and only 2 operands
     * @param originalClause The initial WHERE clause containing the operands
     * and the logic operation
     * @return
     */
    private int matchApplyToEnds(Object[] ends, String originalClause) {
        String operator = getLogicOperator(operatorRegExp, originalClause);

        Object a = ends[0], b = ends[1];

        boolean ult;
        int res = -1;

        switch (operator) {
            case "<":
                if (a instanceof Double && b instanceof Double) {
                    ult = (((Double) a) < ((Double) b));
                    if (ult) {
                        res = 1;
                    } else {
                        res = 0;
                    }
                }
                break;
            case ">":
                if (a instanceof Double && b instanceof Double) {
                    ult = (((Double) a) > ((Double) b));
                    if (ult) {
                        res = 1;
                    } else {
                        res = 0;
                    }
                }
                break;
            case ">=":
                if (a instanceof Double && b instanceof Double) {
                    ult = (((Double) a) >= ((Double) b));
                    if (ult) {
                        res = 1;
                    } else {
                        res = 0;
                    }
                }
                break;
            case "<=":
                if (a instanceof Double && b instanceof Double) {
                    ult = (((Double) a) <= ((Double) b));
                    if (ult) {
                        res = 1;
                    } else {
                        res = 0;
                    }
                }
                break;
            case "!=":
                if (a instanceof Double && b instanceof Double) {
                    ult = (!Objects.equals((Double) a, (Double) b));
                    if (ult) {
                        res = 1;
                    } else {
                        res = 0;
                    }
                }
                if (a instanceof String && b instanceof String) {
                    ult = (!((String) a).equals(((String) b)));
                    if (ult) {
                        res = 1;
                    } else {
                        res = 0;
                    }
                }
                break;
            case "=":
                if (a instanceof Double && b instanceof Double) {
                    ult = (Objects.equals((Double) a, (Double) b));
                    if (ult) {
                        res = 1;
                    } else {
                        res = 0;
                    }
                }
                if (a instanceof String && b instanceof String) {
                    ult = (((String) a).equals(((String) b)));
                    if (ult) {
                        res = 1;
                    } else {
                        res = 0;
                    }
                }
                break;
            case "EQUALSIGNORECASE":
                if (a instanceof String && b instanceof String) {
                    ult = (((String) a).equalsIgnoreCase(((String) b)));
                    if (ult) {
                        res = 1;
                    } else {
                        res = 0;
                    }
                }
                break;
            case "CONTAINS":
                if (a instanceof String && b instanceof String) {
                    ult = (((String) a).contains(((String) b)));
                    if (ult) {
                        res = 1;
                    } else {
                        res = 0;
                    }
                }
                break;
            case "CONTAINSIGNORECASE":
                if (a instanceof String && b instanceof String) {
                    ult = (((String) a).toLowerCase().contains(((String) b).toLowerCase()));
                    if (ult) {
                        res = 1;
                    } else {
                        res = 0;
                    }
                }
                break;
        }
        return res;
    }

    /**
     * Method that solves logic expression according to the rules highlighted in
     * the specification
     *
     * @param whereClause Logic expression
     * @param row Affected row
     * @param availableColumns Row's columns involved
     * @return
     */
    private int solveWhereClause(String whereClause, Row row, List<String> availableColumns) {
        int result = -1;
        String firstSupEnd, secondSupEnd, boolRegExp4Match = "^.*\\s(AND|OR)\\s.*$", boolRegExp4Split = "(\\sAND\\s|\\sOR\\s)", operator;
        if (whereClause.matches(boolRegExp4Match)) {
            String[] superEnds = whereClause.split(boolRegExp4Split);
            firstSupEnd = superEnds[0]; // something operation something
            secondSupEnd = superEnds[1]; // something operation something

            String[] firstSubEnds = firstSupEnd.split(operatorRegExp);
            String[] secondSubEnds = secondSupEnd.split(operatorRegExp);

            int a = matchApplyToEnds(workOutEnds(firstSubEnds, row, availableColumns), firstSupEnd);
            int b = matchApplyToEnds(workOutEnds(secondSubEnds, row, availableColumns), secondSupEnd);

            if (a != -1 && b != -1) {
                operator = getLogicOperator(boolRegExp4Split, whereClause);

                switch (operator) {
                    case "OR":
                        if ((a != 0) || (b != 0)) {
                            result = 1;
                        } else {
                            result = 0;
                        }
                        break;
                    case "AND":
                        if ((a != 0) && (b != 0)) {
                            result = 1;
                        } else {
                            result = 0;
                        }
                        break;
                }
            }
        } else {
            //^[\w']+\s([<>]{1}=?|!?=|EQUALSIGNORECASE|CONTAINS|CONTAINSIGNORECASE)\s[\w']+$
            String[] subEnds = whereClause.split(operatorRegExp);
            result = matchApplyToEnds(workOutEnds(subEnds, row, availableColumns), whereClause);
        }

        return result;
    }

    public void update(String tableName, String field, String value, String whereClause) {
        Table tb = getTableByName(tableName.toLowerCase());
        int columnIndex = tb.getColumnIndexByName(field.toLowerCase());
        for (Row r : tb.getTableRows()) {
            if ((this.solveWhereClause(whereClause, r, tb.getColumnsNames())) == 1) {
                r.updateColumn(columnIndex, value);
            }
        }
        persist();
    }

    public void update(String tableName, String field, String value) {
        Table tb = getTableByName(tableName.toLowerCase());
        int columnIndex = tb.getColumnIndexByName(field.toLowerCase());
        for (Row r : tb.getTableRows()) {
            r.updateColumn(columnIndex, value);
        }
        persist();
    }

    public void delete(String tableName, String whereClause) {
        Table tb = getTableByName(tableName.toLowerCase());
        List<Row> rowsToDelete = new ArrayList<>();
        for (Row r : tb.getTableRows()) {
            if ((this.solveWhereClause(whereClause, r, tb.getColumnsNames())) == 1) {
                rowsToDelete.add(r);
            }
        }
        for (Row r : rowsToDelete) {
            tb.deleteRow(r);
        }
        persist();
    }

    public Table select(String tableName, String fields) {
        Table tb = getTableByName(tableName);

        if (fields.equals("*")) {
            return tb;
        }

        Table newTable = new Table(tableName, fields, this);

        List<String> newTableCols = newTable.getColumnsNames();
        for (Row r : tb.getTableRows()) {
            String currFields = "";
            for (String s : newTableCols) {
                currFields += (String) (r.getColumn(tb.getColumnIndexByName(s))) + ", ";
            }
            currFields = currFields.substring(0, currFields.length() - 2);
            newTable.addRow(currFields);
        }
        return newTable;
    }

    public Table select(String tableName, String fields, String whereClause) {
        Table tb = getTableByName(tableName);

        if (fields.equals("*")) {
            fields = "";
            for (String colName : tb.getColumnsNames()) {
                fields += colName + ", ";
            }
            fields = fields.substring(0, fields.length() - 2);
        }
        Table newTable = new Table(tableName, fields, this);

        List<String> newTableCols = newTable.getColumnsNames();
        for (Row r : tb.getTableRows()) {
            if ((this.solveWhereClause(whereClause, r, tb.getColumnsNames())) == 1) {
                String currFields = "";
                for (String s : newTableCols) {
                    currFields += (String) (r.getColumn(tb.getColumnIndexByName(s))) + ", ";
                }
                currFields = currFields.substring(0, currFields.length() - 2);
                newTable.addRow(currFields);
            }
        }
        return newTable;
    }

    /**
     * Routine method to call whenever CRUD take place within the DB. It updates
     * the eventual DB's binary file
     */
    public void persist() {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Internal method used to load the DB from a binary file given the path is
     * existent (see constructor)
     */
    private void load() {
        JBase obj = null;
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            obj = (JBase) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.tables = obj.tables;
    }

    @Override
    public String toString() {
        String output = "";
        for (Table table : tables) {
            output += table.toString();
        }
        return output;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JBase db = new JBase("tests.dat");
        //db.createTable("cats", "name TEXT, teeth TINYINT, length TINYINT");
        /*db.insert("cats", "'Bob', 24, 35");
        db.insert("cats", "'Tiffany', 15, 27");
        db.insert("cats", "'Sir Puffsalot', 30, 19");*/
        //db.update("cats", "teeth", "58");
        /*String tbname = "fox";
        db.createTable(tbname, "name TEXT, teeth TINYINT, length TINYINT");
        db.insert(tbname, "'Bob', 24, 35");
        db.insert(tbname, "'Tiffany', 15, 27");
        db.insert(tbname, "'Sir Puffsalot', 30, 19");*/
        //db.delete("cats", "length > 20");
        /*Table result = db.select("cats", "*");
        result.addColumns("color TEXT");*/
        /*db.getTableByName("cats").addColumns("colour TEXT");
        db.getTableByName("cats").addColumns("race TEXT, agility INT");
        System.out.println("\n"+db.getTableByName("cats").getColumns()+"\n");
        db.getTableByName("cats").deleteColumn(1);*/
        
        String tableName = "inventory";/*
        db.createTable(tableName, "item TEXT, prod_id INT, price DOUBLE, stock SMALLINT");
        db.insert(tableName, "'Toothbrush', 186445, 1.99, 20");
        db.insert(tableName, "'Snickers', 253748, 0.97, 283");
        db.insert(tableName, "'Bearcat hat', 748492, 12.49, 117");
        db.insert(tableName, "'Pencil', 153852, 0.49, 800");
        db.insert(tableName, "'Sponge', 648375, 0.99, 50");
        db.insert(tableName, "'Toothpick', 693571, 0.10, 325");
        db.insert(tableName, "'Jetpack', 396989, 8567.99, 2");
        db.insert(tableName, "'Flashlight', 648396, 4.49, 23");
        db.insert(tableName, "'Guitar Pick', 106473, 0.39, 250");
        db.insert(tableName, "'Escher Poster', 765439, 19.49, 7");*/
        
        System.out.println(db.select(tableName, "item, stock, price", "item = '1' OR price > 0").toString());
        
        System.out.println(db.toString());
        System.out.println("\n");
        /*System.out.println(db.getTableByName("cats").getColumnMeta(0));
        System.out.println(result.toString());*/
    }

}
