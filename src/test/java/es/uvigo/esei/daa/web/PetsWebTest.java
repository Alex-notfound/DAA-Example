package es.uvigo.esei.daa.web;

import static es.uvigo.esei.daa.dataset.PetsDataset.existentId;
import static es.uvigo.esei.daa.dataset.PetsDataset.existentPet;
import static es.uvigo.esei.daa.dataset.PetsDataset.newName;
import static es.uvigo.esei.daa.dataset.PetsDataset.newOwner;
import static es.uvigo.esei.daa.dataset.PetsDataset.newPet;
import static es.uvigo.esei.daa.dataset.PetsDataset.pets;
import static es.uvigo.esei.daa.matchers.IsEqualToPet.containsPetsInAnyOrder;
import static es.uvigo.esei.daa.matchers.IsEqualToPet.equalsToPet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;

import es.uvigo.esei.daa.entities.Pet;
import es.uvigo.esei.daa.listeners.ApplicationContextBinding;
import es.uvigo.esei.daa.listeners.ApplicationContextJndiBindingTestExecutionListener;
import es.uvigo.esei.daa.listeners.DbManagement;
import es.uvigo.esei.daa.listeners.DbManagementTestExecutionListener;
import es.uvigo.esei.daa.web.pages.MainPage;
import es.uvigo.esei.daa.web.pages.PetsPage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:contexts/hsql-context.xml")
@TestExecutionListeners({ DbUnitTestExecutionListener.class, DbManagementTestExecutionListener.class,
		ApplicationContextJndiBindingTestExecutionListener.class })
@ApplicationContextBinding(jndiUrl = "java:/comp/env/jdbc/daaexample", type = DataSource.class)
@DbManagement(create = "classpath:db/hsqldb.sql", drop = "classpath:db/hsqldb-drop.sql")
@DatabaseSetup("/datasets/dataset.xml")
@ExpectedDatabase("/datasets/dataset.xml")
public class PetsWebTest {
	private static final int DEFAULT_WAIT_TIME = 1;

	private WebDriver driver;
	private PetsPage petsPage;

	@Before
	public void setUp() throws Exception {
		final String baseUrl = "http://localhost:9080/DAAExample/";

		final FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("browser.privatebrowsing.autostart", true);

		final FirefoxOptions options = new FirefoxOptions(DesiredCapabilities.firefox());
		options.setProfile(profile);

		final FirefoxDriver firefoxDriver;
		driver = firefoxDriver = new FirefoxDriver();
		driver.get(baseUrl);

		// Driver will wait DEFAULT_WAIT_TIME if it doesn't find and element.
		driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_TIME, TimeUnit.SECONDS);
		driver.manage().window().maximize();

		// Login as "admin:adminpass"
		final LocalStorage localStorage = firefoxDriver.getLocalStorage();
		localStorage.setItem("authorization-token", "YWRtaW46YWRtaW5wYXNz");

		petsPage = new PetsPage(driver, baseUrl);
		petsPage.navigateTo();
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
		driver = null;
		petsPage = null;
	}

	@Test
	public void testList() throws Exception {
		assertThat(petsPage.listPets(), containsPetsInAnyOrder(pets()));
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-add.xml")
	public void testAdd() throws Exception {
		final Pet newPet = petsPage.addPet(newName(), Integer.toString(newOwner()));

		assertThat(newPet, is(equalsToPet(newPet())));
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-modify.xml")
	public void testEdit() throws Exception {
		final Pet pet = existentPet();
		pet.setName(newName());
		pet.setOwner(newOwner());

		petsPage.editPet(pet);

		final Pet webPet = petsPage.getPet(pet.getId());

		assertThat(webPet, is(equalsToPet(pet)));
	}

	@Test
	@ExpectedDatabase("/datasets/dataset-delete.xml")
	public void testDelete() throws Exception {
		petsPage.deletePet(existentId());

		assertFalse(petsPage.hasPet(existentId()));
	}
}
