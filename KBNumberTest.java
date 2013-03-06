import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class KBNumberTest {
	
	/*
	 * Instantiated only once for all tests. Make sure multiple calls to each
	 * method work as expected, and leftover data/variables from the last call
	 * is handled properly.
	 */
	KBNumber bacon = new KBNumber("movies_tiny.txt");
	KBNumber bacon2 = new KBNumber("moviesTest.txt");
	KBNumber baconNoCollab = new KBNumber("moviesTestNone.txt");
	KBNumber baconOneCollab = new KBNumber("moviesTestOne.txt");
	KBNumber baconSameCollab = new KBNumber("same.txt");
	KBNumber baconDisCon = new KBNumber("disconnected.txt");
	KBNumber baconDisCon2 = new KBNumber("discon2.txt");
	KBNumber noBacon = new KBNumber("nobacon.txt");
	KBNumber longB = new KBNumber("long.txt");
	KBNumber cycle = new KBNumber("cycle.txt");
	KBNumber multiple = new KBNumber("multiple.txt");


	@Before
	public void setUp() throws Exception {
		//nothing here.
	}
		
	//Zero and One collaboration between any two actors
	@Test
	public void testCollaborationNoneOne(){
		assertEquals(baconNoCollab.mostCollaboration(), 0);
		assertEquals(baconOneCollab.mostCollaboration(), 1);
	}
	
	//Same amount of collaboration between every actor
	@Test
	public void testCollaborationSame(){
		assertEquals(baconOneCollab.mostCollaboration(), 1);
		assertEquals(baconSameCollab.mostCollaboration(), 4);
	}
	
	//Only collaboration greater than 1 is between two actors
	@Test
	public void testCollaborationLarge(){
		assertEquals(bacon2.mostCollaboration(), 7);
	}
	
	@Test
	public void testCollaboration() {
		assertEquals(bacon.mostCollaboration(), 3);
	}
	
	//Costars with no actors (empty list)
	@Test 
	public void testCostarsNone(){
		List<String> result = baconNoCollab.findCostars("A");
		assertEquals(result.size(), 0);
		assertFalse(result.contains("A"));
		assertFalse(result.contains("Bacon, Kevin"));
		List<String> result2 = baconNoCollab.findCostars("Bacon, Kevin");
		assertEquals(result2.size(), 0);
		assertFalse(result.contains("A"));
		assertFalse(result.contains("Bacon, Kevin"));
		assertFalse(result.contains("D"));
	}
	
	//Costars with only one other actor 
	@Test
	public void testCostarsOne(){
		List<String> result = baconOneCollab.findCostars("Z");
		assertEquals(result.size(), 1);
		assertTrue(result.contains("Bacon, Kevin"));
		assertFalse(result.contains("A"));
		assertFalse(result.contains("B"));
		List<String> result2 = baconOneCollab.findCostars("Bacon, Kevin");
		assertEquals(result2.size(), 1);
		assertFalse(result2.contains("Bacon, Kevin"));
		assertTrue(result2.contains("Z"));		
	}
	
	//Costars every actor
	@Test
	public void testCostarsEvery(){
		List<String> result = baconSameCollab.findCostars("A");
		assertEquals(result.size(), 5);
		assertTrue(result.contains("Bacon, Kevin"));
		assertTrue(result.contains("D"));
		assertTrue(result.contains("E"));
		assertTrue(result.contains("F"));
		assertTrue(result.contains("G"));
		assertFalse(result.contains("A"));
	}
	
	//Costars with no actors, but other actors are costars (disconnected)
	@Test
	public void testCostarsDisconnected(){
		List<String> result = baconDisCon.findCostars("Z");
		assertEquals(result.size(), 0);
		assertFalse(result.contains("Z"));
		
		List<String> result2 = baconDisCon.findCostars("A");
		assertEquals(result2.size(), 5);
		assertFalse(result2.contains("Z"));
	}
	
	//Exception tests--actor is null and actor is not in list
	@Test
	public void testCostarsEx(){
		try {
			List<String> result = baconDisCon.findCostars("Nick Cage");
			assertEquals(result.size(), 0);
			fail();
		} catch(IllegalArgumentException e){
			assertTrue(true);
		}
		
		try {
			List<String> result2 = baconDisCon.findCostars(null);
			assertEquals(result2.size(), 0);
			fail();
		} catch(IllegalArgumentException e){
			assertTrue(true);
		}

	}
	
	@Test
	public void testCostars(){
		List<String> result = bacon.findCostars("C");
		assertEquals(result.size(), 3);
		assertTrue(result.contains("A"));
		assertTrue(result.contains("B"));
		assertTrue(result.contains("D"));
	}
	
	//Exception Tests--null and if actor not in list
	@Test
	public void testNumEx(){
		try {
			int baconnum0 = baconDisCon.findBaconNumber("Nick Cage");
			assertEquals(baconnum0, -1);
			fail();
		} catch(IllegalArgumentException e){
			assertTrue(true);
		}
		
		int baconnum = baconDisCon.findBaconNumber("Z");
		assertEquals(baconnum, -1);
		
		try {
			int baconnum2 = baconDisCon.findBaconNumber(null);
			assertEquals(baconnum2, 0);
			fail();
		} catch(IllegalArgumentException e){
			assertTrue(true);
		}

	}
	
	//Disconnected--Bacon Number: -1
	@Test
	public void testFindBaconNumDis(){
		int baconnum = baconDisCon.findBaconNumber("Z");
		assertEquals(baconnum, -1);
		int baconnum2 = baconDisCon2.findBaconNumber("Z");
		assertEquals(baconnum2, -1);
	}
	
	//Bacon himself--Bacon # : 0
	@Test
	public void testFindBaconNum0(){
		int baconN = bacon.findBaconNumber("Bacon, Kevin");
		assertEquals(baconN, 0);
		int baconN2 = baconOneCollab.findBaconNumber("Bacon, Kevin");
		assertEquals(baconN2, 0);
	}
	
	//Bacon not in movie list: -1
	@Test
	public void testFindBaconNumNeg1(){
		int bN = noBacon.findBaconNumber("Z");
		assertEquals(bN, -1);
		bN = noBacon.findBaconNumber("D");
		assertEquals(bN, -1);
		bN = noBacon.findBaconNumber("E");
		assertEquals(bN, -1);
		bN = noBacon.findBaconNumber("G");
		assertEquals(bN, -1);
	}
	
	//Bacon # where Bacon is at the end of a long path, but all other paths are short
	//Plus, Bacon shows up at the end of a longer path
	@Test
	public void testFindBaconNumlong(){
		int bN = longB.findBaconNumber("F");
		assertEquals(bN, 5);
	}
	
	//All Bacon numbers the same
	@Test
	public void testFindBaconNumberSame(){
		int baconnum = baconSameCollab.findBaconNumber("A");
		assertEquals(baconnum, 1);
		
		baconnum = baconSameCollab.findBaconNumber("D");
		assertEquals(baconnum, 1);
		
		baconnum = baconSameCollab.findBaconNumber("E");
		assertEquals(baconnum, 1);
	}

	
	@Test
	public void testFindBaconNumber(){
		int baconnum = bacon.findBaconNumber("N");
		assertEquals(baconnum, 6);
	}
	
	//When the actor parameter is Bacon
	@Test
	public void testFindBaconToBaconPath () {
		List<String> path = bacon.findBaconPath("Bacon, Kevin");
		assertEquals(path.size(), 1);
		
		Iterator<String> it = path.iterator();
		String s;
		
		s=it.next();
		assertEquals(s, "Bacon, Kevin");
	}
	
	
	//When there isn't a bacon path (disconnected)
	@Test
	public void testFindBaconDis () {
		try {
			List<String> path = baconDisCon.findBaconPath("Nick Cage");
			assertEquals(path.size(), 0);
			fail();
		} catch(IllegalArgumentException e){
			assertTrue(true);
		}
		
		
		try {
			List<String> path2 = baconDisCon2.findBaconPath("Z");
			assertEquals(path2, null);
		} catch(IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	//When there isn't a bacon path b/c bacon is not in the file 	
	@Test
	public void testFindBaconNoBacon () {
		try {
			List<String> path = noBacon.findBaconPath("Bacon, Kevin");
			assertEquals(path.size(), 0);
			fail();
		} catch(IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	//When there's a possible cycle "within" the bacon path
	@Test
	public void testFindBaconCycle () {
		List<String> path = cycle.findBaconPath("D");
		assertEquals(path.size(), 7);
		
		Iterator<String> it = path.iterator();
		String s;
		
		s=it.next();
		assertEquals(s, "D");
		s=it.next();
		assertTrue(s.equals("Movie 6") && !s.equals("Movie 1"));
		s=it.next();
		assertEquals(s, "G");
		s=it.next();
		assertTrue(s.equals("Movie 7"));
		s=it.next();
		assertEquals(s, "A");
		s=it.next();
		assertEquals(s, "Movie 0");
		s=it.next();
		assertEquals(s, "Bacon, Kevin");	
	}
	
	//Kevin Bacon in multiple movies
	@Test
	public void testFindBaconMult () {
		List<String> path = multiple.findBaconPath("E");
		assertEquals(path.size(), 7);
		
		Iterator<String> it = path.iterator();
		String s;
		
		s=it.next();
		assertEquals(s, "E");
		s=it.next();
		assertTrue(s.equals("Movie 3") || s.equals("Movie 2") || s.equals("Movie 1"));
		s=it.next();
		assertEquals(s, "D");
		s=it.next();
		assertTrue(s.equals("Movie 9"));
		s=it.next();
		assertEquals(s, "I");
		s=it.next();
		assertTrue(s.equals("Movie 14"));
		s=it.next();
		assertEquals(s, "Bacon, Kevin");	
	}
		
	//When the name of the actor is not in the graph (exception tests)
	@Test
	public void testFindBaconNickCage () {
		try {
			List<String> path = bacon.findBaconPath("Cage, Nicolas");
			assertEquals(path.size(), 0);
			fail();
		} catch(IllegalArgumentException e){
			assertTrue(true);
		}
		
		try {
			List<String> path = bacon.findBaconPath(null);
			assertEquals(path.size(), 0);
			fail();
		} catch(IllegalArgumentException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testFindBaconPath () {
		List<String> path = bacon.findBaconPath("D");
		assertEquals(path.size(), 5);
		
		Iterator<String> it = path.iterator();
		String s;
		
		s=it.next();
		assertEquals(s, "D");
		s=it.next(); 
		assertTrue(s.equals("Movie 2") || s.equals("Movie 7"));				
		s=it.next();
		assertEquals(s, "A");
		s=it.next();
		assertEquals(s, "Movie 0");
		s=it.next();
		assertEquals(s, "Bacon, Kevin");		
		
	}

}
