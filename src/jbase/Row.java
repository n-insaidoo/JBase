/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jbase;

import java.io.*;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Nana
 */
public class Row implements Serializable{
    private List<Object> fields;
    
    public Row(){
        fields = new ArrayList<>();
    }
    
    public void addValue(Object value){
        fields.add(value);
    }
    
    public void removeValue(int index){
        fields.remove(index);
    }
    
    public Object getColumn(int index){
        return fields.get(index);
    }
    
    public void updateColumn(int colIndex, Object value){
        fields.set(colIndex, value);
    }
    
    @Override
    public String toString(){
        String output = "";
        for(Object val : fields){
            output+= val+", ";
        }
        return output.substring(0,output.length()-2);
    }
}
