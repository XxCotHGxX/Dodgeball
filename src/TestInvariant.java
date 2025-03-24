import java.awt.Color;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.uwm.cs351.Ball;
import edu.uwm.cs351.BallCollection;
import edu.uwm.cs351.Point;
import edu.uwm.cs351.Vector;
import junit.framework.TestCase;

/**
 * Tests of the BallCOllection invariant and its iterator invariant.
 */
public class TestInvariant extends TestCase {
	protected BallCollection.Spy spy;
	protected int reports;
	protected BallCollection r;
	protected Iterator<Ball> i;
	
	Ball b1 = new Ball(new Point(1.0,20.0),new Vector(),Color.RED);
	Ball b2 = new Ball(new Point(2.0,40.0),new Vector(),Color.BLUE);
	Ball b3 = new Ball(new Point(3.0,60.0),new Vector(),Color.YELLOW);
	
	protected void assertReporting(boolean expected, Supplier<Boolean> test) {
		reports = 0;
		Consumer<String> savedReporter = spy.getReporter();
		try {
			spy.setReporter((String message) -> {
				++reports;
				if (message == null || message.trim().isEmpty()) {
					assertFalse("Uninformative report is not acceptable", true);
				}
				if (expected) {
					assertFalse("Reported error incorrectly: " + message, true);
				}
			});
			assertEquals(expected, test.get().booleanValue());
			if (!expected) {
				assertEquals("Expected exactly one invariant error to be reported", 1, reports);
			}
			spy.setReporter(null);
		} finally {
			spy.setReporter(savedReporter);
		}
	}
	
	protected void assertWellFormed(boolean expected, BallCollection r) {
		assertReporting(expected, () -> spy.wellFormed(r));
	}
	
	protected void assertWellFormed(boolean expected, Iterator<Ball> r) {
		assertReporting(expected, () -> spy.wellFormed(r));
	}

	@Override // implementation
	protected void setUp() {
		spy = new BallCollection.Spy();
	}

	public void testA0() {
		r = spy.newInstance(null, 0, 0);
		assertWellFormed(false, r);
	}
	
	public void testA1() {
		r = spy.newInstance(new Ball[0], 0, 0);
		assertWellFormed(true, r);
	}
	
	public void testA2() {
		r = spy.newInstance(new Ball[3], -1, 0);
		assertWellFormed(false, r);
	}
	
	public void testA3() {
		r = spy.newInstance(new Ball[3],4,0);
		assertWellFormed(false, r);
	}
	
	public void testA4() {
		r = spy.newInstance(new Ball[4],4,0);
		assertWellFormed(true, r);
	}
	
	public void testA5() {
		r = spy.newInstance(new Ball[10],0,0);
		assertWellFormed(true, r);
	}
	
	public void testA6() {
		r = spy.newInstance(new Ball[5],4,-1);
		assertWellFormed(true, r);
	}
	
	public void testA7() {
		r = spy.newInstance(new Ball[3],3,3);
		assertWellFormed(true, r);
	}
	
	public void testA8() {
		r = spy.newInstance(new Ball[5],3,4);
		assertWellFormed(true, r);
	}
	
	public void testA9() {
		r = spy.newInstance(new Ball[5],4,400);
		assertWellFormed(true, r);
	}

	
	public void testB0() {
		r = spy.newInstance(new Ball[0], 0, 42);
		i = spy.newIterator(r, -1, false, 42);
		assertWellFormed(true, i);
	}
	
	public void testB1() {
		r = spy.newInstance(new Ball[0], 0, 42);
		i = spy.newIterator(r, -1, true, 42);
		assertWellFormed(false, i);
	}
	
	public void testB5() {
		r = spy.newInstance(new Ball[0], 0, 42);
		i = spy.newIterator(r, 0, false, 42);
		assertWellFormed(false, i);
	}
	
	public void testB6() {
		r = spy.newInstance(new Ball[0], 0, 42);
		i = spy.newIterator(r, 0, true, 42);
		assertWellFormed(false, i);
	}
	
	public void testC0() {
		r = spy.newInstance(new Ball[1], 0, 76);
		i = spy.newIterator(r, -1, true, 76);
		assertWellFormed(false, i);
	}

	public void testC1() {
		r = spy.newInstance(new Ball[1], 0, 76);
		i = spy.newIterator(r, -1, false, 76);
		assertWellFormed(true, i);
	}

	public void testC2() {
		r = spy.newInstance(new Ball[1], 0, 76);
		i = spy.newIterator(r, 0, true, 76);
		assertWellFormed(false, i);
	}

	public void testC3() {
		r = spy.newInstance(new Ball[1], 0, 76);
		i = spy.newIterator(r, 0, false, 76);
		assertWellFormed(false, i);
	}
	
	public void testC4() {
		r = spy.newInstance(new Ball[1], 1, 76);
		i = spy.newIterator(r, -1, true, 76);
		assertWellFormed(false, i);
	}

	public void testC5() {
		r = spy.newInstance(new Ball[1], 1, 76);
		i = spy.newIterator(r, -1, false, 76);
		assertWellFormed(true, i);
	}

	public void testC6() {
		r = spy.newInstance(new Ball[1], 1, 76);
		i = spy.newIterator(r, 0, true, 76);
		assertWellFormed(true, i);
	}

	public void testC7() {
		r = spy.newInstance(new Ball[1], 1, 76);
		i = spy.newIterator(r, 0, false, 76);
		assertWellFormed(true, i);
	}

	public void testC8() {
		r = spy.newInstance(new Ball[]{b1}, 0, 76);
		i = spy.newIterator(r, 0, false, 76);
		assertWellFormed(false, i);
	}

	public void testC9() {
		r = spy.newInstance(new Ball[] {b1}, 1, 42);
		i = spy.newIterator(r, 0, true, 42);
		assertWellFormed(true, i);
	}

	

	public void testD0() {
		r = spy.newInstance(new Ball[2], 0, 42);
		i = spy.newIterator(r, -1, true, 42);
		assertWellFormed(false, i);
	}
	
	public void testD1() {
		r = spy.newInstance(new Ball[2], 2, 42);
		i = spy.newIterator(r, -1, false, 42);
		assertWellFormed(true, i);
	}
	
	public void testD2() {
		r = spy.newInstance(new Ball[2], 1, 42);
		i = spy.newIterator(r, 0, true, 42);
		assertWellFormed(true, i);
	}
	
	public void testD3() {
		r = spy.newInstance(new Ball[2], 0, 42);
		i = spy.newIterator(r, 0, false, 42);
		assertWellFormed(false, i);
	}
	
	public void testD4() {
		r = spy.newInstance(new Ball[2], 0, 42);
		i = spy.newIterator(r, 1, true, 42);
		assertWellFormed(false, i);
	}
	
	public void testD5() {
		r = spy.newInstance(new Ball[2], 2, 42);
		i = spy.newIterator(r, 1, false, 42);
		assertWellFormed(true, i);
	}
	
	public void testD6() {
		r = spy.newInstance(new Ball[2], 2, 42);
		i = spy.newIterator(r, 2, true, 42);
		assertWellFormed(false, i);
	}
	
	public void testD7() {
		r = spy.newInstance(new Ball[2], 2, 42);
		i = spy.newIterator(r, 2, false, 42);
		assertWellFormed(false, i);
	}
	
	public void testD8() {
		r = spy.newInstance(new Ball[] {b1, b2}, 2, 42);
		i = spy.newIterator(r, 1, false, 42);
		assertWellFormed(true, i);
	}
	
	public void testD9() {
		r = spy.newInstance(new Ball[] {b1, b2}, 2, 42);
		i = spy.newIterator(r, 2, true, 42);
		assertWellFormed(false, i);
	}

	public void testE0() {
		r = spy.newInstance(new Ball[1], 2, 55);
		i = spy.newIterator(r, -1, true, 55);
		assertWellFormed(false, i);
	}

	public void testE1() {
		r = spy.newInstance(new Ball[1], 2, 55);
		i = spy.newIterator(r, -1, false, 55);
		assertWellFormed(false, i);
	}

	public void testE2() {
		r = spy.newInstance(new Ball[1], 2, 55);
		i = spy.newIterator(r, 0, true, 55);
		assertWellFormed(false, i);
	}

	public void testE3() {
		r = spy.newInstance(new Ball[1], 2, 55);
		i = spy.newIterator(r, 0, false, 55);
		assertWellFormed(false, i);
	}

	public void testE4() {
		r = spy.newInstance(new Ball[1], 2, 55);
		i = spy.newIterator(r, 1, true, 55);
		assertWellFormed(false, i);
	}

	public void testE5() {
		r = spy.newInstance(new Ball[1], 2, 55);
		i = spy.newIterator(r, 1, false, 55);
		assertWellFormed(false, i);
	}

	public void testE6() {
		r = spy.newInstance(new Ball[] {b1}, 2, 55);
		i = spy.newIterator(r, 0, true, 55);
		assertWellFormed(false, i);
	}

	public void testE7() {
		r = spy.newInstance(new Ball[] {b1}, 2, 55);
		i = spy.newIterator(r, 0, false, 55);
		assertWellFormed(false, i);
	}

	public void testE8() {
		r = spy.newInstance(new Ball[] {b1}, 2, 55);
		i = spy.newIterator(r, 1, true, 55);
		assertWellFormed(false, i);
	}

	public void testE9() {
		r = spy.newInstance(new Ball[] {b1}, 2, 55);
		i = spy.newIterator(r, 1, false, 55);
		assertWellFormed(false, i);
	}
	
	public void testF0() {
		r = spy.newInstance(new Ball[3], 2, 55);
		i = spy.newIterator(r, 2, false, 42);
		assertWellFormed(true, i);
	}
	
	public void testF1() {
		r = spy.newInstance(new Ball[3], 2, 55);
		i = spy.newIterator(r, 2, true, 42);
		assertWellFormed(true, i);
	}
	
	public void testF2() {
		r = spy.newInstance(new Ball[3], 2, 55);
		i = spy.newIterator(r, -1, true, 42);
		assertWellFormed(true, i);
	}
	
	public void testF3() {
		r = spy.newInstance(new Ball[3], 2, 55);
		i = spy.newIterator(r, -3, true, 42);
		assertWellFormed(true, i);
	}
	
	public void testF4() {
		r = spy.newInstance(new Ball[3], 2, 55);
		i = spy.newIterator(r, 1, false, 42);
		assertWellFormed(true, i);
	}
	
	public void testF5() {
		r = spy.newInstance(new Ball[1], 2, 55);
		i = spy.newIterator(r, 1, false, 42);
		assertWellFormed(false, i);
	}
	
	public void testF6() {
		r = spy.newInstance(new Ball[1], 2, 55);
		i = spy.newIterator(r, 0, false, 42);
		assertWellFormed(false, i);
	}
	
	public void testF7() {
		r = spy.newInstance(new Ball[] {b1}, 2, 55);
		i = spy.newIterator(r, 0, true, 42);
		assertWellFormed(false, i);
	}
	
	public void testF8() {
		r = spy.newInstance(new Ball[] {b1}, 1, 55);
		i = spy.newIterator(r, 0, true, 42);
		assertWellFormed(true, i);
	}
	
	public void testF9() {
		r = spy.newInstance(new Ball[] {b1}, 1, 55);
		i = spy.newIterator(r, 2, true, 42);
		assertWellFormed(true, i);
	}
}
