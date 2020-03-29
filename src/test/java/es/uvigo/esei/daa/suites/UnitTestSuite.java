package es.uvigo.esei.daa.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import es.uvigo.esei.daa.entities.PersonUnitTest;
import es.uvigo.esei.daa.entities.PetUnitTest;

@SuiteClasses({ PersonUnitTest.class, PetUnitTest.class })
@RunWith(Suite.class)
public class UnitTestSuite {
}
