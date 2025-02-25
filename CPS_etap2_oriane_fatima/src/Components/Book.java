package Components;

import fr.sorbonne_u.cps.dht_mapreduce.interfaces.content.ContentDataI;

public class Book implements ContentDataI {
	private static final long serialVersionUID = 1L;

	private String title;
	
	public Book(String title) {
		this.title = title;
	}
	
	public String getBook() {
		return title;
	}
	
	@Override
    public String toString() {
        return "Book {" +
                "Title =" + title +
                "}"
                ;
    }
}
