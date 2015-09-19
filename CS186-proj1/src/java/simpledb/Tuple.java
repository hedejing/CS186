package simpledb;

import java.io.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import simpledb.TupleDesc.TDItem;

/**
 * Tuple maintains information about the contents of a tuple. Tuples have a
 * specified schema specified by a TupleDesc object and contain Field objects
 * with the data for each field.
 */
public class Tuple implements Serializable {
	private Field[] Items;
	private RecordId id;
	private TupleDesc td;
    private static final long serialVersionUID = 1L;

    /**
     * Create a new tuple with the specified schema (type).
     * 
     * @param td
     *            the schema of this tuple. It must be a valid TupleDesc
     *            instance with at least one field.
     */
    public Tuple(TupleDesc td) {
    	// hdj
    	Items = new Field[td.numFields()];
    	this.td = td;
    }

    /**
     * @return The TupleDesc representing the schema of this tuple.
     */
    public TupleDesc getTupleDesc() {
        // hdj
        return this.td;
    }

    /**
     * @return The RecordId representing the location of this tuple on disk. May
     *         be null.
     */
    public RecordId getRecordId() {
        // hdj
    	return id;
    }

    /**
     * Set the RecordId information for this tuple.
     * 
     * @param rid
     *            the new RecordId for this tuple.
     */
    public void setRecordId(RecordId rid) {
        // hdj
    	id = rid;
    }

    /**
     * Change the value of the ith field of this tuple.
     * 
     * @param i
     *            index of the field to change. It must be a valid index.
     * @param f
     *            new value for the field.
     */
    public void setField(int i, Field f) {
        // hdj
    	if(i >= Items.length)
    		throw new NoSuchElementException();
    	else{
    		Items[i] = f;
    	}
    }

    /**
     * @return the value of the ith field, or null if it has not been set.
     * 
     * @param i
     *            field index to return. Must be a valid index.
     */
    public Field getField(int i) {
    	// hdj
    	if(i >= Items.length)
    		throw new NoSuchElementException();
    	else{
    		return Items[i];
    	}
    }

    /**
     * Returns the contents of this Tuple as a string. Note that to pass the
     * system tests, the format needs to be as follows:
     * 
     * column1\tcolumn2\tcolumn3\t...\tcolumnN\n
     * 
     * where \t is any whitespace, except newline, and \n is a newline
     */
    public String toString() {
        // hdj
    	String s= "";
    	for(Field field:Items){
    		s += field.toString()+'\t';
    	}
    	s +='\n';
    	return s;
    }
    
    /**
     * @return
     *        An iterator which iterates over all the fields of this tuple
     * */
    public Iterator<Field> fields()
    {
        // hdj
    	Iterator<Field> iter = new Iterator<Field>(){
    		private int currentIndex = 0;
    		/**
    		*@Override
    		**/
    		public boolean hasNext(){
    			return currentIndex < Items.length && Items[currentIndex] != null;
    		}
    		/**
    		*@Override
    		**/
    		public void remove(){
    			throw new UnsupportedOperationException("unimplemented");
    		}
    		/**
    		*@Override
    		**/
    		public Field next(){
    			return Items[currentIndex++];
    		}
    		
    	};
    	
        return iter;
    }
}
