/*Michael Hernandez
 * Got help from my lab group and the walk-in tutoring as well as Prof. Boyland on Piazza
 * My TA also helped me understand iterators better in our Lab
 */ 


package edu.uwm.cs351;

import java.util.AbstractCollection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * An implementation of the collection interface 
 * specialized to {@link Ball} objects.
 * The implementation uses dynamic arrays.
 */
public class BallCollection extends AbstractCollection<Ball>
{

	Ball[] data;
	int manyItems;
	int version;
	
	private static Consumer<String> reporter = (s) -> System.out.println("Invariant error: "+ s);
	
	private boolean report(String error) 
	{
		reporter.accept(error);
		return false;
	}
	
	private BallCollection(boolean ignored) {} // do not change this constructor
	
	public BallCollection() //non-specifying constructor
	{
		manyItems = 0;
		version = 0;
		data = new Ball[manyItems];
	}
	
	private boolean wellFormed() 
	{
		//check the invariants
		
		if (data == null) throw new IllegalStateException(); //if data was never instantiated somehow
		
		if (manyItems > data.length) return report("data is not long enough");	//if manyItems is larger than the data array
		
		if (manyItems < 0) return report("manyItems is negative"); //if manyItems is below zero (I left them separate so they would have different report messages)
		
		return true; //no problems found
	}
	/**
	 * Change the current capacity of this sequence if needed.
	 * @param minimumCapacity
	 *   the new capacity for this sequence
	 * @postcondition
	 *   This sequence's capacity has been changed to at least minimumCapacity.
	 *   If the capacity was already at or greater than minimumCapacity,
	 *   then the capacity is left un changed.
	 *   If the capacity is changed, it must be at least twice as big as before.
	 * @exception OutOfMemoryError
	 *   Indicates insufficient memory for: new array of minimumCapacity elements.
	 **/
	private void ensureCapacity(int minimumCapacity)
	{

		if(minimumCapacity<=data.length) {return;} //if length is ok, do nothing
		
		int newSize = data.length*2;
		
		if (newSize <= manyItems) {newSize = minimumCapacity+1;}
		
		Ball[] newArray = new Ball[newSize];
		
		for (int i = 0; i <= manyItems-1; i++) 
		{
			newArray[i]=data[i];
		}
	    data=newArray;
		
	}
	
	@Override // required
	public Iterator<Ball> iterator() {
		
		assert wellFormed() : "invariant broken at start of iterator()";
		
		return new MyIterator();
	}
	
	@Override //implementation 
	public boolean add(Ball element) {
		
		assert wellFormed() : "Invariant failed at start of add";
		
		ensureCapacity(manyItems+1);
		
		if(manyItems==0) //and data is empty, the new Ball is in the first position		
		{
			data = new Ball[1];
			data[0] = element;
		} 
		else {data[manyItems]=element;} // and the sequence has stuff in it, the new Ball is at the end of the sequence
		
		manyItems++;
		version++;
		
		assert wellFormed() : "Invariant failed at end of add";
		
		return true;
	}
	
	@Override //implementation
	public int size() 
	{		
		return manyItems;
	}
	
	@Override //efficiency
	public void clear() 
	{
		assert wellFormed(): "Failed at start of clear()";
		
		if(manyItems==0) 
		{
			return;
		}
		data = new Ball[manyItems];
		manyItems = 0;
		version++;
		
		assert wellFormed(): "Failed at end of clear()";
	}
	
	private class MyIterator //################### ITERATOR CLASS START  #############
	implements Iterator<Ball> 
	{
				
		int colVersion;
		boolean canRemove;
		int index;
		
		MyIterator(boolean ignored) {}// should only be used by Spy
		
		public MyIterator()
		{
			colVersion = version;
			index = -1;
			canRemove = false;
			
			assert wellFormed() : "Failed after constructor";
		}

		
		public boolean wellFormed() 
		{
			//check iterator class invariants
			if(!(BallCollection.this.wellFormed())) return false;
			
			if(colVersion != version) {return true;}
			
			if (canRemove && index==-1) {throw new ConcurrentModificationException();}
			
			if(manyItems == 0) {return true;} //don't check the next "if" data is empty
			
			if(!(index >= -1 && index < manyItems)) {return report ("Index is out of bounds.");}
						
			return true; //no problems found
		}

		@Override //required
		public boolean hasNext() {
			
			assert wellFormed() : "Failed at hasNext()";
			
			if (colVersion != version) 
			{
				throw new ConcurrentModificationException(); //stale iterator
			}
			
			if(manyItems == 0) {return false;} //empty data
			
			if ((index+1) < manyItems) {return true;} //at the end of data already
			
			else 
			{
				return false;
			}
			//nothing was changed so no wellFormed() check necessary
		}

		@Override //required
		public Ball next() {
			
			assert wellFormed(): "Failed at start of next(): ";

			if (!hasNext()) {throw new NoSuchElementException();}
			
			index++;
			canRemove=true;
			
			assert wellFormed(): "Failed at end of next()";
			
			return data[index];
			
			
		}

		@Override //implementation
		public void remove() {
			
			assert wellFormed(): "Failed at remove(): ";
			
			if(colVersion != version) throw new ConcurrentModificationException(); //stale iterator check
			
			if (manyItems == 0) throw new IllegalStateException();//empty data check
			
			if (!canRemove) throw new IllegalStateException(); //can the item be removed?
			
			if(index < manyItems-1) //if there is other things in the data array that need to be copied
			{
				for(int i = index; i < manyItems-1; i++) //copy them over to the left a space
				{
					data[i]=data[i+1];
				}
			}
			
			if(manyItems < 1) // if there are no items, do not make changes
			{
				return;
			} 
			
			manyItems--;
			index--;
			colVersion++;
			version=colVersion; //keep iterator current			
			
			canRemove=false;
			
			assert wellFormed() : "invariant failed at end of remove";
		}
		
	}
	
	/**
	 * Used for testing the invariant.  Do not change this code.
	 */
	public static class Spy {//################### SPY CLASS START #######################
		/**
		 * Return the sink for invariant error messages
		 * @return current reporter
		 */
		public Consumer<String> getReporter() {
			return reporter;
		}

		/**
		 * Change the sink for invariant error messages.
		 * @param r where to send invariant error messages.
		 */
		public void setReporter(Consumer<String> r) {
			reporter = r;
		}

		/**
		 * Create a debugging instance of the main class
		 * with a particular data structure.
		 * @param a static array to use
		 * @param m size to use
		 * @param v current version
		 * @return a new instance with the given data structure
		 */
		public BallCollection newInstance(Ball[] a, int m, int v) {
			BallCollection result = new BallCollection(false);
			result.data = a;
			result.manyItems = m;
			result.version = v;
			return result;
		}
		
		/**
		 * Return an iterator for testing purposes.
		 * @param bc main class instance to use
		 * @param i index of iterator
		 * @param r the value of 'canRemove'
		 * @param v the value of colVersion
		 * @return iterator with this data structure
		 */
		public Iterator<Ball> newIterator(BallCollection bc, int i, boolean r, int v) {
			MyIterator result = bc.new MyIterator(false);
			result.index = i;
			result.canRemove = r;
			result.colVersion = v;
			return result;
		}
		
		/**
		 * Return whether debugging instance meets the 
		 * requirements on the invariant.
		 * @param bs instance of to use, must not be null
		 * @return whether it passes the check
		 */
		public boolean wellFormed(BallCollection bs) {
			return bs.wellFormed();
		}
		
		/**
		 * Return whether debugging instance meets the 
		 * requirements on the invariant.
		 * @param i instance of to use, must not be null
		 * @return whether it passes the check
		 */
		public boolean wellFormed(Iterator<Ball> i) {
			return ((MyIterator)i).wellFormed();
		}
	}
}
