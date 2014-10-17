package nkid.tini.data;

public class Poll {
	private int id;
	private String name;
	private String description;
	private int vote;
	private String datetime;
	
	public Poll(String name, String description, int vote,
			String datetime) {
		this.name = name;
		this.description = description;
		this.vote = vote;
		this.datetime = datetime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getVote() {
		return vote;
	}

	public void setVote(int vote) {
		this.vote = vote;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	
	
}
