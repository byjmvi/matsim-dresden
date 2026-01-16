package org.matsim.run.scenarios;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.utils.DresdenUtils;
import org.matsim.utils.objectattributes.attributable.Attributes;

public class AgeModifyPopulation {
	public static void main(String[] args) {
		Config config = ConfigUtils.loadConfig("input/v1.0/dresden-v1.0-10pct.config.xml");
		Scenario scenario = ScenarioUtils.loadScenario(config);
		PopulationFactory pf = scenario.getPopulation().getFactory();

		Scenario scenario2 = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		Population population2 = scenario2.getPopulation();

		Population population = scenario.getPopulation();
		int newid = 134949;
		for (Person person : population.getPersons().values()) {
			Object age = person.getAttributes().getAttribute("age");
			if (age != null){
				String agestring = age.toString();
				int ageasint = Integer.parseInt(agestring);
				if (ageasint > 65) {
					newid = newid + 1;
					String strid = String.valueOf(newid);


					Person newperson = pf.createPerson(Id.createPersonId("99" + strid));

					newperson.addPlan(person.getSelectedPlan());

					population2.addPerson(newperson);
				}
			}



		}



		PopulationWriter populationWriter = new PopulationWriter(population2);
		populationWriter.write("input/v1.0/population_doubled_old_people.xml");


	}
}
