package Components;

import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentKeyI;

public class Author implements ContentKeyI {
	private static final long serialVersionUID = 1L;

	private String auth;
	
	public Author (String name) {
		auth = name;
	}
	
	public String getGame() {
		return auth;
	}
	
	@Override
    public String toString() {
        return "Author {" +
                "Name =" + auth +
                "}"
                ;
    }
}
