package es.uvigo.esei.daa.dataset;

import static java.util.Arrays.binarySearch;
import static java.util.Arrays.stream;

import java.util.Arrays;
import java.util.function.Predicate;

import es.uvigo.esei.daa.entities.Pet;

public final class PetsDataset {
	private PetsDataset() {
	}

	public static Pet[] pets() {
		return new Pet[] { 
				new Pet(1, "Firulais", 1), 
				new Pet(2, "Terry", 1), 
				new Pet(3, "Luna", 3),
				new Pet(4, "Toby", 3), 
				new Pet(5, "Pancho", 4), 
				new Pet(6, "León", 4), 
				new Pet(7, "Sasha", 5),
				new Pet(8, "Rubén", 6) };
	}

	public static Pet[] petWithout(int... ids) {
		Arrays.sort(ids);

		final Predicate<Pet> hasValidId = pet -> binarySearch(ids, pet.getId()) < 0;

		return stream(pets()).filter(hasValidId).toArray(Pet[]::new);
	}

	public static Pet pet(int id) {
		return stream(pets()).filter(pet -> pet.getId() == id).findAny().orElseThrow(IllegalArgumentException::new);
	}

	public static int existentId() {
		return 4;
	}

	public static int nonExistentId() {
		return 1234;
	}

	public static Pet existentPet() {
		return pet(existentId());
	}

	public static Pet nonExistentPet() {
		return new Pet(nonExistentId(), "Can", 9);
	}

	public static String newName() {
		return "John";
	}

	public static int newOwner() {
		return 1;
	}

	public static Pet newPet() {
		return new Pet(pets().length + 1, newName(), newOwner());
	}
}
