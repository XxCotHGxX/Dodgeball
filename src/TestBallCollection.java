import java.awt.Color;
import java.util.Iterator;
import java.util.function.Supplier;

import edu.uwm.cs351.Ball;
import edu.uwm.cs351.BallCollection;
import edu.uwm.cs351.Point;
import edu.uwm.cs351.Vector;


public class TestBallCollection extends TestCollection<Ball> {

	private Iterator<Ball> it;
	
	
	@Override
	protected void initCollections() {
		c = new BallCollection();
		e = new Ball[] {
				new Ball(new Point(0,0), new Vector(), Color.BLACK),
				new Ball(new Point(1,1), new Vector(), Color.RED),
				new Ball(new Point(2,2), new Vector(), Color.BLUE),
				new Ball(new Point(3,3), new Vector(), Color.GREEN),
				new Ball(new Point(4,4), new Vector(), Color.MAGENTA),
				new Ball(new Point(5,5), new Vector(), Color.CYAN),
				new Ball(new Point(6,6), new Vector(), Color.YELLOW),
				new Ball(new Point(7,7), new Vector(), Color.ORANGE),
				new Ball(new Point(8,8), new Vector(), Color.GRAY),
				new Ball(new Point(9,9), new Vector(), Color.WHITE)
		};
		// permitNulls = true;
		// preserveOrder = true;
		// permitDuplicates = true;
		// failFast = true;
		// hasRemove = true;
	}

	protected void assertExceptionName(String name, Runnable r) {
		try {
			r.run();
			assertEquals(name, "");
		} catch (RuntimeException ex) {
			assertEquals(name, ex.getClass().getSimpleName());
		}
	}
	
	protected void assertEqualsOrException(String expected, Supplier<?> supp) {
		try {
			Object result = supp.get();
			assertEquals(expected, ""+result);
		} catch (RuntimeException ex) {
			assertEquals(expected, ex.getClass().getSimpleName());
		}
		
	}
	
	
	/// Locked tests
	
	public void test66() {
		BallCollection bc = new BallCollection();
		// Give the string of the result or name of exception
		// "it" was never assigned and so it is null; 
		assertEqualsOrException("NullPointerException",() -> it.hasNext());
		it = bc.iterator();
		assertEqualsOrException(Ts(12763851),() -> it.hasNext());
		assertEqualsOrException(Ts(775338767),() -> it.next());
		bc.add(e[1]); // added a ball
		assertEqualsOrException(Ts(268544907),() -> it.hasNext());
		it = bc.iterator();
		assertEqualsOrException(Ts(1125408703),() -> it.hasNext());
	}
	
	
}
