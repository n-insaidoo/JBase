/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jbase;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Nana
 */
public class Table implements Serializable{
    
    private String name;
    private List<Row> rows;
    private List<String[]> columns;
    private JBase parentDb;
    
    /**
     * Constructor
     * @param tableName Table's name
     * @param columnsTypes Comma separated set of names and types. E.g. [name Type, name Type,...,nameN TypeN]
     */
    public Table(String tableName,String columnsTypes){
        name = tableName;
        parentDb = null;
        rows = new ArrayList<>();
        columns = new ArrayList<>();
        
        String colsTypes = columnsTypes;
        String[] tuples = colsTypes.split(",");
        for(String tuple: tuples){
            String[] colType = tuple.trim().split("\\s");
            columns.add(colType);
        }
    }
    
    public Table(String tableName,String columnsTypes, JBase jb){
        name = tableName;
        parentDb = jb;
        rows = new ArrayList<>();
        columns = new ArrayList<>();
        
        String colsTypes = columnsTypes;
        String[] tuples = colsTypes.split(",");
        for(String tuple: tuples){
            String[] colType = tuple.trim().split("\\s");
            columns.add(colType);
        }
    }
    
    public void setParentDb(JBase jb){
        parentDb = jb;
    }
    
    public String getName(){
        return name;
    }
    
    /**
     * Returns a list of all the column names in the table
     * @return List of all the column names in the table
     */
    public List<String> getColumnsNames(){
        List<String> cols = new ArrayList<>();
        for(String[] s: columns){
            cols.add(s[0].toLowerCase()); // due to case sensitivity needs; do it here or where the method it's called
        }
        return cols;
    }
    
    /**
     * Adds columns according to comma separated format specified in the specs.
     * @param namesTypes Comma separated set of names and types. E.g. [name Type, name Type,...,nameN TypeN]
     */
    public void addColumns(String namesTypes){
        String colsTypes = namesTypes;
        String[] tuples = colsTypes.split(",");
        for(String tuple: tuples){
            String[] colType = tuple.trim().split("\\s");
            columns.add(colType);
            // new column added; every row has to be initialised in that spot
            for (Row r : rows){
                r.addValue("NULL");
            }
        }
        parentDb.persist();
    }
    
    public void deleteColumn(int index){
        for (Row r : rows){
          r.removeValue(index);
        }
        columns.remove(index);
        parentDb.persist();
    }
    
    public int getColumns(){
        return columns.size();
    }
    
    /**
     * Finds a column's index by it's name
     * @param name Column's name
     * @return Column's index
     */
    public int getColumnIndexByName(String name){
        int result = -1;
        List<String> cols = getColumnsNames();
        result = cols.indexOf(name);
        cols.indexOf(name);
        return result;
    }
    
    public void deleteRow(Row r){
        rows.remove(r);
    }
    
    public List<Row> getTableRows(){
        return rows;
    }
    
    public int getRows(){
        return rows.size();
    }
    
    /**
     * Returns info (name and type) concerning a specific column
     * @param index Column's index
     * @return Column's name and type
     */
    public String getColumnMeta(int index){
        return Arrays.toString(columns.get(index));
    }
    
    public void addRow(String data){
        Row r = new Row();
        String[] rowValues = data.split(",");
        for(String val : rowValues){
            r.addValue(val.trim());
        }
        rows.add(r);
    }
    
    @Override
    public String toString(){
        String output="";
        output = name+"\n";
        for(String[] column : columns){
            //output+=(column[0]+"|["+column[1]+"]|");
            output+=(column[0]+" | ");
        }
        output=output.substring(0, output.length()-3)+"\n";
        for(Row r : rows)
            output+=r.toString()+"\n";
        output+="\n";
        return output;
    }
    
}
