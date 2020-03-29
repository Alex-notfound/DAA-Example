package es.uvigo.esei.daa.entities;

import static java.util.Objects.requireNonNull;

public class Pet {

	private int id;
	private String name;
	private int owner;

	Pet() {
	}

	public Pet(int id, String name, int owner) {
		super();
		this.id = id;
		this.setName(name);
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
		this.name = requireNonNull(name, "Name can't be null");
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Pet))
			return false;
		Pet other = (Pet) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
