package es.uvigo.esei.daa.web.pages;

import static es.uvigo.esei.daa.util.AdditionalConditions.jQueryAjaxCallsHaveCompleted;
import static java.util.stream.Collectors.toList;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElement;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import es.uvigo.esei.daa.entities.Pet;

public class PetsPage {
	private static final String TABLE_ID = "pets-list";
	private static final String FORM_ID = "pets-form";

	private static final String ID_PREFIX = "pet-";

	private final WebDriver driver;

	private final WebDriverWait wait;

	private final String baseUrl;

	public PetsPage(WebDriver driver, String baseUrl) {
		this.driver = driver;
		this.baseUrl = baseUrl;

		this.wait = new WebDriverWait(driver, 1);
	}

	public void navigateTo() {
		this.driver.get(this.baseUrl + "main.html");

		this.wait.until(presenceOfElementLocated(By.id("pets-list")));
	}

	public int countPets() {
		return new PetsTable(this.driver).countPets();
	}

	public List<Pet> listPets() {
		return new PetsTable(this.driver).listPets();
	}

	public Pet getLastPet() {
		return new PetsTable(this.driver).getPetInLastRow();
	}

	public Pet getPet(int id) {
		return new PetsTable(this.driver).getPetById(id);
	}

	public boolean hasPet(int id) {
		return new PetsTable(this.driver).hasPet(id);
	}

	public Pet addPet(String name, String owner) {
		final PetForm form = new PetForm(this.driver);

		form.clear();
		form.setName(name);
		form.setOwner(owner);
		form.submit();

		final PetsTable table = new PetsTable(driver);
		return table.getPet(name, owner);
	}

	public void editPet(Pet pet) {
		final PetsTable table = new PetsTable(this.driver);
		table.editPet(pet.getId());

		final PetForm form = new PetForm(this.driver);
		form.setName(pet.getName());
		form.setOwner(Integer.toString(pet.getOwner()));
		form.submit();
	}

	public void deletePet(int id) {
		final PetsTable table = new PetsTable(this.driver);

		table.deletePet(id);

		wait.until(jQueryAjaxCallsHaveCompleted());
	}

	private final static class PetsTable {
		private final WebDriver driver;

		private final WebElement table;

		public PetsTable(WebDriver driver) {
			this.driver = driver;

			this.table = this.driver.findElement(By.id(TABLE_ID));
		}

		public boolean hasPet(int id) {
			try {
				return this.getPetRow(id) != null;
			} catch (NoSuchElementException nsee) {
				return false;
			}
		}

		public void editPet(int id) {
			final WebElement petRow = this.getPetRow(id);

			petRow.findElement(By.className("edit")).click();
		}

		public void deletePet(int id) {
			final WebElement petRow = this.getPetRow(id);

			petRow.findElement(By.className("delete")).click();

			this.acceptDialog();
		}

		public Pet getPetById(int id) {
			return rowToPet(getPetRow(id));
		}

		public Pet getPet(String name, String owner) {
			return rowToPet(getPetRow(name, owner));
		}

		public Pet getPetInLastRow() {
			final WebElement row = this.table.findElement(By.cssSelector("tbody > tr:last-child"));

			return rowToPet(row);
		}

		private WebElement getPetRow(int id) {
			return this.table.findElement(By.id(ID_PREFIX + id));
		}

		public WebElement getPetRow(String name, String owner) {
			final List<WebElement> rows = table.findElements(By.cssSelector("tbody > tr"));

			for (WebElement row : rows) {
				final String rowName = row.findElement(By.className("name")).getText();
				final String rowOwner = row.findElement(By.className("owner")).getText();

				if (rowName.equals(name) && rowOwner.equals(owner)) {
					return row;
				}
			}

			throw new IllegalArgumentException(
					String.format("No row found with name '%s' and owner '%s'", name, owner));
		}

		public int countPets() {
			return getRows().size();
		}

		public List<Pet> listPets() {
			return getRows().stream().map(this::rowToPet).collect(toList());
		}

		private List<WebElement> getRows() {
			final String xpathQuery = "//tbody/tr[starts-with(@id, '" + ID_PREFIX + "')]";

			return this.table.findElements(By.xpath(xpathQuery));
		}

		private Pet rowToPet(WebElement row) {
			return new Pet(Integer.parseInt(row.getAttribute("id").substring(ID_PREFIX.length())),
					row.findElement(By.className("name")).getText(),
					Integer.parseInt(row.findElement(By.className("owner")).getText()));
		}

		private void acceptDialog() {
			driver.switchTo().alert().accept();
		}
	}

	public final static class PetForm {
		private final WebDriverWait wait;

		private final WebElement fieldName;
		private final WebElement fieldOwner;
		private final WebElement buttonClear;
		private final WebElement buttonSubmit;

		public PetForm(WebDriver driver) {
			this.wait = new WebDriverWait(driver, 1);

			final WebElement form = driver.findElement(By.id(FORM_ID));

			this.fieldName = form.findElement(By.name("name"));
			this.fieldOwner = form.findElement(By.name("owner"));
			this.buttonClear = form.findElement(By.id("btnClear"));
			this.buttonSubmit = form.findElement(By.id("btnSubmit"));
		}

		public void submit() {
			this.buttonSubmit.click();

			this.waitForCleanFields();
		}

		public void clear() {
			this.buttonClear.click();

			this.waitForCleanFields();
		}

		public void setName(String name) {
			this.fieldName.clear();
			this.fieldName.sendKeys(name);
		}

		public void setOwner(String owner) {
			this.fieldOwner.clear();
			this.fieldOwner.sendKeys(owner);
		}

		public String getName() {
			return this.fieldName.getText();
		}

		public String getOwner() {
			return this.fieldOwner.getText();
		}

		private void waitForCleanFields() {
			wait.until(textToBePresentInElement(fieldName, ""));
			wait.until(textToBePresentInElement(fieldOwner, ""));
		}
	}
}
