package simpledb;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

import sun.misc.IOUtils;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 * 
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {

    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
	private File f;
	private TupleDesc td;
	
	
	
    public HeapFile(File f, TupleDesc td) {
        // hdj
    	this.f = f;
    	this.td = td;
    	
    }

    /**
     * Returns the File backing this HeapFile on disk.
     * 
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // hdj
    	return this.f;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     * 
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
        // hdj
        return f.getAbsolutePath().hashCode();
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * 
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        // hdj
    	return this.td;
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // hdj
    	try{
    		int PageOffset = pid.pageNumber();
    		if(PageOffset >= numPages() )
    			throw new IllegalArgumentException();
    		else{
	    		RandomAccessFile file = new RandomAccessFile(f, "r");
	    		file.seek(PageOffset * BufferPool.PAGE_SIZE);
	    		byte[] data = new byte[BufferPool.PAGE_SIZE];
	    		file.readFully(data);
	    		file.close();
	    		HeapPageId hpid = new HeapPageId(pid.getTableId(), pid.pageNumber());
	    		Page hp = new HeapPage(hpid, data);
	    		return hp;
	    	}
    	}
    	catch(IOException e){
    		System.out.println("IOException :" + e.getMessage());
    		return null;
    	}
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for proj1
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // hdj
    	int num = (int) ((f.length() + Database.getBufferPool().PAGE_SIZE - 1) / Database.getBufferPool().PAGE_SIZE);
    	return num;
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // hdj
        return null;
        // not necessary for proj1
    }

    // see DbFile.java for javadocs
    public Page deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for proj1
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // hdj
    	final TransactionId Tid = tid;
    	DbFileIterator it = new DbFileIterator(){
	    	/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			/**
		     * Opens the iterator
		     * @throws DbException when there are problems opening/accessing the database.
		     */
    		private int PageOffset = 0;
    		private int tableId = getId();
    		private Iterator<Tuple> TupleIter;
    		private boolean IsOpen = false;
    		
			public void open() throws DbException, TransactionAbortedException{
				IsOpen = true;
				PageId pId = new HeapPageId(tableId, 0);
				Page page = Database.getBufferPool().getPage(Tid, pId, Permissions.READ_WRITE);
/*				if(page instanceof HeapPage){
    	    		page = (HeapPage)page;
    	    		TupleIter = ((HeapPage) page).iterator();
    	    	}
    	    	else{
    	    		throw new DbException("Page is not a HeapPage!");
    	    		}*/
				TupleIter = page.iterator();
    	    	
			}
		
			/** @return true if there are more tuples available. */
    		public boolean hasNext(){
    			if(IsOpen){
	    			if(TupleIter.hasNext())
	    				return true;
	    			else{
	    				if(PageOffset + 1< numPages())
	    					return true;
	    				else
	    					return false;
	    			}
    			}
    			else{
    				return false;
    			}
    		} 	

    	    /**
    	     * Gets the next tuple from the operator (typically implementing by reading
    	     * from a child operator or an access method).
    	     *
    	     * @return The next tuple in the iterator.
    	     * @throws NoSuchElementException if there are no more tuples
    	     */
    	    public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException{
	    	    if(IsOpen){	
	    	    	if(TupleIter.hasNext()){
	    	    		return TupleIter.next();
	    	    	}
	    	    	else{
	    	    		if(PageOffset + 1 < numPages()){
	    	    			PageOffset++;
	    	    			PageId pId = new HeapPageId(tableId, PageOffset);
	    					Page page = Database.getBufferPool().getPage(Tid, pId, Permissions.READ_WRITE);
	    					TupleIter = page.iterator();
	    					return TupleIter.next();
	    	    		}
	    	    		else{
	    	    			throw new NoSuchElementException();
	    	    		}
	    	    		
	    	    	}
	    	    }
	    	    else{
	    	    	throw new NoSuchElementException();
	    	    }
    	    }
    	    
    	    /**
    	     * Resets the iterator to the start.
    	     * @throws DbException When rewind is unsupported.
    	     */
    	    public void rewind() throws DbException, TransactionAbortedException{
    	    	PageOffset = 0;
    	    	PageId pId = new HeapPageId(tableId, PageOffset);
				Page page = Database.getBufferPool().getPage(Tid, pId, Permissions.READ_WRITE);
/*				if(page instanceof HeapPage){
    	    		page = (HeapPage)page;
    	    		TupleIter = ((HeapPage) page).iterator();
    	    	}
    	    	else{
    	    		throw new DbException("Page is not a HeapPage!");
    	    	}*/
				TupleIter = page.iterator();
    	    }
    	    
    	    /**
    	     * Closes the iterator.
    	     */
    	    public void close(){
    	    	IsOpen = false;
    	    	
    	    }
    	    
    	};
        return it;
    }

}

