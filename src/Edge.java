
public class Edge {
	
	private String actorA = "";
	private String actorB = "";
	
	public Edge(String actor1, String actor2){ 
		actorA = actor1;
		actorB = actor2;
	}

	public String toString(){
		return actorA + " " + actorB;
	}
	
}
