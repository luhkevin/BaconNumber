import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class KBNumber {
	private HashSet<String> vertexSet = new HashSet<String>();	
	private HashMap<String, ArrayList<String>> edgeToMovies = new HashMap<String, ArrayList<String>>();
	private HashMap<String, HashSet<String>> nbdVertices = new HashMap<String, HashSet<String>>();
	
	private HashSet<String> marked = new HashSet<String>();
	private HashMap<String, String> backTrace = new HashMap<String, String>();
	/**
	 * Constructor
	 * 
	 * @param fileName The name of the file (e.g. "movies.txt")
	 */
	public KBNumber(String fileName) {
		BufferedReader r = null;
		String line = "";
		try {
			r = new BufferedReader(new FileReader(fileName));
			line = r.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		String [] split;
		HashSet<String> actorsByLine = new HashSet<String>();
		while(line != null){
			split = line.split("/");

			//First adds all the actors into a set
			//Index from 1, since split[0] is the movie name
			for(int i = 1; i < split.length; i++){		
				actorsByLine.add(split[i]);					
			}
			
			//Adds all of the actors in the same movie as split[j] as neighboring vertices of split[j]
			for(int j = 1; j < split.length; j++){
				
				actorsByLine.remove(split[j]);
				vertexSet.add(split[j]);
					
				for(String actor : actorsByLine){					
					addToNbrVert(actor, split[j]);
					addToEdges(actor, split[j], split[0]);
				}
				
				actorsByLine.add(split[j]);	
			}
			actorsByLine.clear();
			try {
				line = r.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	//Creates the hashmap of vertices to their neighbors
	private void addToNbrVert(String toAdd, String actorKey){
		if(nbdVertices.containsKey(actorKey)){
			nbdVertices.get(actorKey).add(toAdd);
		} else {
			HashSet<String> nbd = new HashSet<String>();
			nbd.add(toAdd);
			nbdVertices.put(actorKey, nbd);
		}
	}
	
	//Creates the hashmap of edges to their movies
	private void addToEdges(String toAdd, String actorKey, String movie){
		Edge ed = new Edge(actorKey, toAdd);
			
		if(edgeToMovies.containsKey(ed.toString())){
			edgeToMovies.get(ed.toString()).add(movie);
		} else {
			ArrayList<String> movies = new ArrayList<String>();
			edgeToMovies.put(ed.toString(), movies);
			movies.add(movie);
		}
	}
	/**
	 * Find the pair(s) of actors that cooperate in most movies, return the
	 * number of movies they have collaborated in
	 * 
	 * @return the number of movies they have collaborated in
	 */
	public int mostCollaboration() {
		int collabCount = 0;
		int max = 0;
		for(String edActors : edgeToMovies.keySet()){
			collabCount = edgeToMovies.get(edActors).size();
			if(collabCount > max){
				max = collabCount;
			}
		}		
		return max;
	}

	/**
	 * Given the name of an actor, output a list of all other actors that he/she
	 * has been in a movie with
	 * 
	 * @param actor
	 *            The name of the actor
	 * @return A list of UNIQUE actor names in any order that actor has been in
	 *         a movie with. Do not include the actor himself. In the case that
	 *         an actor is in all movies alone, return an empty list.
	 * @throws IllegalArgumentException
	 *             If the name of the actor is null or not contained in the
	 *             graph
	 */
	public List<String> findCostars(String actor) throws IllegalArgumentException {
		if(actor == null || !vertexSet.contains(actor)){
			throw new IllegalArgumentException();
		}
		
		List<String> coStars = new ArrayList<String>();  
		HashSet<String> tempStars = new HashSet<String>();	
		tempStars = nbdVertices.get(actor);
		
		//null --> no neighboring vertices
		if(tempStars != null){
			for(String vStr : tempStars){
				coStars.add(vStr);
			}
		}
		return coStars;
	}

	/**
	 * Implement a BFS on your graph to calculate the Kevin Bacon number of a
	 * given actor
	 * 
	 * @param actor
	 *            The name of the actor
	 * @return If actor is bacon, return 0; if bacon cannot be found from graph,
	 *         return -1; else return bacon number
	 * @throws IllegalArgumentException
	 *             If the name of the actor is null or not contained in the
	 *             graph
	 */
	public int findBaconNumber(String actor) throws IllegalArgumentException {
		if(actor == null || !vertexSet.contains(actor)){
			throw new IllegalArgumentException();
		}
		
		int bNum = 0;
		LinkedList<String> graphQ = new LinkedList<String>();
		LinkedList<String> childrenPerLvl = new LinkedList<String>();
		
		//Adds the actor to the queue, marks the actor so it is not traversed again
		graphQ.offer(actor);	
		marked.add(actor);				
		while (graphQ.size() > 0){	
			String deq = graphQ.remove();
			HashSet<String> childrenVert = nbdVertices.get(deq);			
			if(deq.equals("Bacon, Kevin")){	
				marked.clear();
				return bNum;
			} else if(childrenVert == null){
				marked.clear();
				return -1;
			}
			
			addToMaps(childrenVert, graphQ, childrenPerLvl, deq);
			
			//Determines if we have finished checking one "level" of the tree for Kevin Bacon
			//I.e., counts the edge depths
			if(childrenPerLvl.toString() != null && childrenPerLvl.toString().equals(graphQ.toString())){
				childrenPerLvl.clear();
				bNum++;
			}
		}
		marked.clear();
		return -1;
	}

	//Adds child in childrenVertices to various necessary maps
	private void addToMaps(HashSet<String> childrenVert, LinkedList<String> graphQ, LinkedList<String>
						childrenPerLvl, String deq){
		for(String childVer : childrenVert){
			if(!isMarked(childVer)){
				marked.add(childVer);
				graphQ.offer(childVer);
				backTrace.put(childVer, deq);
				childrenPerLvl.offer(childVer);
			}
		}
	}
	
	//checks if the vertex is marked;
	private boolean isMarked(String actor){
		return marked.contains(actor);
	}
	/**
	 * Given the name of an actor, return a list of strings representing the
	 * path along your BFS from the given actor to Kevin Bacon, starting from
	 * the actor and following an actor->movie->actor->movie pattern.
	 * 
	 * If two actors have appeared in multiple movies together, it does not
	 * matter which of those movies links them in the list.
	 * 
	 * If there are multiple paths in the BFS from a given actor to Kevin Bacon,
	 * it does not matter which path is returned as long as it is accurate and 
	 * there is no shorter path (i.e. your path provides the correct Bacon number).
	 * 
	 * If the actor is Kevin Bacon, the list should contain one string (Bacon, Kevin) only.
	 * 
	 * If there is no path to Kevin Bacon from the given actor, return null.
	 * 
	 * @param actor
	 *            The name of the actor
	 * @return A list of strings showing the path from actor to Kevin Bacon as
	 *         strings alternating between actor and movie, starting from the
	 *         original actor and ending at Bacon.
	 * 
	 *         example List (NOT A TEST CASE) for actor = "Damon, Matt": (Bacon
	 *         number = 2)
	 * 
	 *         Damon, Matt 
	 *         The Informant! (2009) 
	 *         Pistor, Ludger 
	 *         X-Men: First Class (2011) 
	 *         Bacon, Kevin
	 * 
	 * @throws IllegalArgumentException
	 *             If the name of the actor is null or not contained in the
	 *             graph
	 */
	public List<String> findBaconPath (String actor) throws IllegalArgumentException {
		if(actor == null || !vertexSet.contains(actor)){
			throw new IllegalArgumentException();
		}
		
		int bN = findBaconNumber(actor);
		List<String> bPath = new ArrayList<String>();
		if(bN == -1){
			return null;
		} else if (bN == 0){
			bPath.add("Bacon, Kevin");
			return bPath;
		}
		
		return backTrack(actor);
	}
	
	//Method written given that there exists a bacon path (i.e. "Bacon, Kevin" will be in the list)
	//Makes a "backTracking" hashmap starting with Kevin Bacon as the key and parent nodes as values
	public LinkedList<String> backTrack(String root){
		LinkedList<String> back = new LinkedList<String>();
		
		String firstActor = backTrace.get("Bacon, Kevin");
		back.add("Bacon, Kevin");
		back.offerFirst(edgeToMovies.get("Bacon, Kevin" + " " + firstActor).get(0));
		
		String sndActor = "";
		while(!firstActor.equals(root)){
			back.offerFirst(firstActor);
			sndActor = backTrace.get(firstActor);
			back.offerFirst(edgeToMovies.get(firstActor + " " + sndActor).get(0));	
			firstActor = sndActor;
		}
		
		back.offerFirst(root);
		return back;
	}

	
}
