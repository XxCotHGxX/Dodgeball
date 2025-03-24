import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import edu.uwm.cs.junit.EfficiencyTestCase;
import edu.uwm.cs351.Ball;
import edu.uwm.cs351.BallCollection;
import edu.uwm.cs351.Point;
import edu.uwm.cs351.Vector;


public class TestEfficiency extends EfficiencyTestCase {
	Vector v = new Vector();
	Ball p1 = new Ball(new Point(1.0,20.0),v,Color.RED);
	Ball p2 = new Ball(new Point(2.0,40.0),v,Color.BLUE);
	Ball p3 = new Ball(new Point(3.0,60.0),v,Color.YELLOW);
	Ball p4 = new Ball(new Point(4.0,80.0),v,Color.GREEN);
	Ball p5 = new Ball(new Point(5.0,100.0),v,Color.CYAN);
	Ball p6 = new Ball(new Point(6.0,120.0),v,Color.MAGENTA);
	Ball p7 = new Ball(new Point(7.0,140.0),v,Color.ORANGE);
	Ball p8 = new Ball(new Point(8.0,150.0),v,Color.BLACK);

	Ball p[] = {null, p1, p2, p3, p4, p5, p6, p7, p8};
	
	BallCollection s;
	Collection<Ball> c;
	Iterator<Ball> it;
	Random r;
	
	@Override
	public void setUp() {
		c = s = new BallCollection();
		r = new Random();
		try {
			assert 1/(int)(p5.getLoc().x()-5.0) == 42 : "OK";
			assertTrue(true);
		} catch (ArithmeticException ex) {
			System.err.println("Assertions must NOT be enabled to use this test suite.");
			System.err.println("In Eclipse: remove -ea from the VM Arguments box under Run>Run Configurations>Arguments");
			assertFalse("Assertions must NOT be enabled while running efficiency tests.",true);
		}
		super.setUp();
	}

	private static final int MAX_LENGTH = 1000000;
	private static final int SAMPLE = 100;	
	private static final int MAX_WIDTH = 100000;
	
	
	public void test0() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertEquals(i, c.size());
			c.add(p[i%p.length]);
		}
	}

	public void test1() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			c.add(p[i%p.length]);
		}
		assertEquals(true, c.remove(p[0]));
	}

	
	public void test2() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			c.add(p[i % (p.length-1)]);
		}
		c.add(p[p.length-1]);
		assertEquals(true, c.remove(p[p.length-1]));
	}
		
	public void test3() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			c.add(p[i%p.length]);
			assertSame(p[0], c.iterator().next());
		}
	}

	public void test4() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			c.add(p[i%p.length]);
		}
		it = c.iterator();
		for (int i=0; i < MAX_LENGTH; ++i) {
			assertSame(p[i%p.length], it.next());
		}
	}
	
	public void test5() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			c.add(p[i%p.length]);
		}
		c.clear();
		assertEquals(0, c.size());
	}
	
	public void test6() {
		for (int i=0; i < MAX_LENGTH; ++i) {
			c.add(p[i%p.length]);
		}
		for (int i=0; i < MAX_LENGTH; ++i) {
			c.clear();
		}
	}

	public void test7() {
		BallCollection[] a = new BallCollection[MAX_WIDTH];
		for (int i=0; i < MAX_WIDTH; ++i) {
			a[i] = s = new BallCollection();
			int n = r.nextInt(SAMPLE);
			for (int j=0; j < n; ++j) {
				s.add(p[j%6]);
			}
		}
		
		for (int j = 0; j < SAMPLE; ++j) {
			int i = r.nextInt(a.length);
			s = a[i];
			if (s.size() == 0) continue;
			int n = r.nextInt(s.size());
			Iterator<Ball> it = s.iterator();
			Ball current = it.next();
			for (int k=0; k < n; ++k) {
				current = it.next();
			}
			assertSame(p[n%6],current);
		}
	}

}
