package simpledb;

import java.io.Serializable;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {
	
	private TDItem[] Items;
    /**
     * A help class to facilitate organizing the information of each field
     * */
    public static class TDItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * The type of the field
         * */
        Type fieldType;
        
        /**
         * The name of the field
         * */
        String fieldName;

        public TDItem(Type t, String n) {
            this.fieldName = n;
            this.fieldType = t;
        }

        public String toString() {
            return fieldName + "(" + fieldType + ")";
        }
    }

    /**
     * @return
     *        An iterator which iterates over all the field TDItems
     *        that are included in this TupleDesc
     * */
    public Iterator<TDItem> iterator() {
        // hdj:
    	Iterator<TDItem> iter = new Iterator<TDItem>(){
    		
    		private int currentIndex = 0;
    		/**
    		*@Override
    		**/
    		public boolean hasNext(){
    			return currentIndex < Items.length && Items[currentIndex]!=null;
    		}
    		/**
    		*@Override
    		**/	
    		public TDItem next(){
    			return Items[currentIndex++];
    		}
    		
    		/**
    		*@Override
    		**/
    		public void remove(){
    			throw new UnsupportedOperationException("unimplemented");
    		}
    	};
    	
    	return iter;
    }

    private static final long serialVersionUID = 1L;

    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     * @param fieldAr
     *            array specifying the names of the fields. Note that names may
     *            be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        // some code goes here
    	//assert typeAr.length >= 1;
    	
    	Items = new TDItem[typeAr.length];
    	
    	for(int i = 0; i<typeAr.length ; i++){
    		Items[i] = new TDItem(typeAr[i], "null");
    	
    		if(i < fieldAr.length)
    			Items[i].fieldName = fieldAr[i];
    		else
    			Items[i].fieldName = "null";
    	}
    }

    /**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        // hdj:
    	//assert typeAr.length >= 1;
     	
    	Items = new TDItem[typeAr.length];
    	for(int i = 0; i<typeAr.length ; i++){
    		Items[i] = new TDItem(typeAr[i], "null");
    	}
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        //hdj
        return Items.length;
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     * 
     * @param i
     *            index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        // hdj
    	if(i < Items.length){
    		return Items[i].fieldName;
    	}
    	else
    		throw new NoSuchElementException();
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     * 
     * @param i
     *            The index of the field to get the type of. It must be a valid
     *            index.
     * @return the type of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public Type getFieldType(int i) throws NoSuchElementException {
        // hdj
    	if(i < Items.length){
    		return Items[i].fieldType;
    	}
    	else
    		throw new NoSuchElementException();
    }

    /**
     * Find the index of the field with a given name.
     * 
     * @param name
     *            name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException
     *             if no field with a matching name is found.
     */
    public int fieldNameToIndex(String name) throws NoSuchElementException {
        // some code goes here
    	for(int i = 0; i<Items.length ; i++){
    		if(Items[i].fieldName.equals(name)){
    			return i;
    		}
    	}
        
    	throw new NoSuchElementException();
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        // some code goes here
    	int sum=0;
        for(TDItem it:Items){
        	sum += it.fieldType.getLen();
        }
        return sum;
    }

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     * 
     * @param td1
     *            The TupleDesc with the first fields of the new TupleDesc
     * @param td2
     *            The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        // hdj
    	Type[] typeAr = new Type[td1.numFields()+td2.numFields()];
    	String[] fieldAr = new String[td1.numFields() + td2.numFields()];
    	int i=0;
    	for(TDItem it : td1.Items){
    		fieldAr[i] = it.fieldName;
    		typeAr[i++] = it.fieldType;
    	}
    	for(TDItem it : td2.Items){
    		fieldAr[i] = it.fieldName;
    		typeAr[i++] = it.fieldType;
    	}
        return new TupleDesc(typeAr, fieldAr);
    }
    //! hdj : not clear enough
    //still need to figure out "every element in items are equal" or "every element has the same 'Type'" is correct
    /**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they are the same size and if the n-th
     * type in this TupleDesc is equal to the n-th type in td.
     * 
     * @param o
     *            the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */
    public boolean equals(Object o) {
        // hdj
    	if(o == this) return true;
   
    	if(o instanceof TupleDesc){
    		TupleDesc td = (TupleDesc)o;
    		if(this.numFields() != td.numFields())
    			return false;
    		else{
    			for(int i=0; i<this.numFields();i++){
    				if(this.getFieldType(i) == td.getFieldType(i))
    					continue;
    				else
    					return false;
    			}
    		}
    	}
    	else
    		return false;
    	
		return true;
    	
    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
    	// hdj
    	Type[] array = new Type[this.numFields()];
    	for(int i=0; i<array.length; i++){
    		array[i] = this.getFieldType(i);
    	}
    	return array.hashCode(); 
    	
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     * 
     * @return String describing this descriptor.
     */
    public String toString() {
        // hdj
    	String s = null;
    	for(int i=0; i<Items.length ; i++){
    		s += Items[i].toString() ;
    		if(i < Items.length - 1){
    			s += ", ";
    		}
    	}
        return s;
    }
}
