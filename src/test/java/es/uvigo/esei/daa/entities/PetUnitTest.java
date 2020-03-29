package es.uvigo.esei.daa.entities;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class PetUnitTest {
	@Test
	public void testPetIntStringString() {
		final int id = 1;
		final String name = "John";
		final int owner = 1;

		final Pet pet = new Pet(id, name, owner);

		assertThat(pet.getId(), is(equalTo(id)));
		assertThat(pet.getName(), is(equalTo(name)));
		assertThat(pet.getOwner(), is(equalTo(owner)));
	}

	@Test(expected = NullPointerException.class)
	public void testPetIntStringIntNullName() {
		new Pet(1, null, 1);
	}

	@Test
	public void testSetName() {
		final int id = 1;
		final Person owner = new Person(1, "Antón", "Pérez");

		final Pet pet = new Pet(id, "John", owner.getId());
		pet.setName("Juan");

		assertThat(pet.getId(), is(equalTo(id)));
		assertThat(pet.getName(), is(equalTo("Juan")));
		assertThat(pet.getOwner(), is(equalTo(owner.getId())));
	}

	@Test(expected = NullPointerException.class)
	public void testSetNullName() {
		final Pet pet = new Pet(1, "John", 1);
		pet.setName(null);
	}

	@Test
	public void testSetOwner() {
		final int id = 1;
		final String name = "John";

		final Pet pet = new Pet(id, name, new Person(1, "Antón", "Pérez").getId());
		pet.setOwner(new Person(2, "Manuel", "Martínez").getId());

		assertThat(pet.getId(), is(equalTo(id)));
		assertThat(pet.getName(), is(equalTo(name)));
		assertThat(pet.getOwner(), is(equalTo(new Person(2, "Manuel", "Martínez").getId())));
	}

	@Test
	public void testEqualsObject() {
		final Pet petA = new Pet(1, "Name A", new Person(1, "Antón", "Pérez").getId());
		final Pet petB = new Pet(1, "Name B", new Person(2, "Manuel", "Martínez").getId());

		assertTrue(petA.equals(petB));
	}

	@Test
	public void testEqualsHashcode() {
		EqualsVerifier.forClass(Pet.class).withIgnoredFields("name", "owner").suppress(Warning.STRICT_INHERITANCE)
				.suppress(Warning.NONFINAL_FIELDS).verify();
	}
}
