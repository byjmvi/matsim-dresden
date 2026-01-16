package org.matsim.run.scenarios;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.api.core.v01.population.PopulationWriter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.utils.DresdenUtils;

public class AgeModifyPopulation {
	public static void main(String[] args) {
		Config config = ConfigUtils.loadConfig("input/v1.0/dresden-v1.0-1pct.config.xml");
		Scenario scenario = ScenarioUtils.createScenario(config);

		Population population = scenario.getPopulation();
		for (Person person : population.getPersons().values()){
			int age = (int) person.getAttributes().getAttribute( "age");
			if (age < 27){
				Id id = person.getId();
				population.removePerson(id);
			}
		}
		PopulationWriter populationWriter = new PopulationWriter(population);
		populationWriter.write("input/v1.0/dresden-v1.0-1pct.config.xml");


	}
}
