package org.matsim.run.scenarios;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import scala.Int;

import java.util.ArrayList;
import java.util.List;

public class RealisticScenarioAgeGroups {

	public static void main(String[] args) {
		Config config = ConfigUtils.loadConfig("input/v1.0/dresden-v1.0-1pct.config.xml");
		Scenario scenario = ScenarioUtils.loadScenario(config);
		Population population = scenario.getPopulation();
		PopulationFactory pf = scenario.getPopulation().getFactory();
		Scenario scenario2 = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		Population population2 = scenario2.getPopulation();

		int[][] classes = { {0, 17, -15},
							{18, 27, -2},
							{28, 45, 15}};

		int id2 = 134949;
		List<Id> RemoveIdList = new ArrayList<>();
		for (Person person : population.getPersons().values()) {
			// set LinkIds to null, so only the coordinates are used for modelling
			for (Plan plan : person.getPlans()){
				ExtremeScenarioDoubleAndDelete.cleanPlans(plan);
			}

			// ----------------------------------------------------------
			// changing population
			Object age = person.getAttributes().getAttribute("age");
			if (age != null){
				String agestring = age.toString();
				int ageasint = Integer.parseInt(agestring);
				int counter = 0;


				for(int row=0;row<classes.length;row++){
					if (ageasint > classes[row][1] && ageasint < classes[row][2]){
						counter = 0;
						while (counter != classes[row][3]){
							if (classes[row][3]<0){
								Id id = person.getId();
								RemoveIdList.add(id);
							}
							if (classes[row][3]>0){
								id2 = id2 + 1;
								Person person2 = ExtremeScenarioDoubleAndDelete.createDuplicate(person, pf, id2);
								population2.addPerson(person2);
							}
						}

					}
				}
			}
		}
		for (Id idtoremove : RemoveIdList){
			population.removePerson(idtoremove);
		}


		PopulationWriter populationWriter = new PopulationWriter(population);
		populationWriter.write("input/v1.0/population_extreme_scenario.xml");

	}


}
