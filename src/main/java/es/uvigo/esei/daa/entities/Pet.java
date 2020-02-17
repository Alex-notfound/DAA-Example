package es.uvigo.esei.daa.entities;

public class Pet {

	private int id;
	private String name;
	private Person owner;

	Pet() {}

	public Pet(int id, String name, Person owner) {
		super();
		this.id = id;
		this.name = name;
		this.owner = owner;
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

	public Person getOwner() {
		return owner;
	}

	public void setOwner(Person owner) {
		this.owner = owner;
	}

}
