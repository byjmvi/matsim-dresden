package org.matsim.run.scenarios;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

public class RealisticScenarioAgeGroups {

	public static void main(String[] args) {
		Config config = ConfigUtils.loadConfig("input/v1.0/dresden-v1.0-1pct.config.xml");
		Scenario scenario = ScenarioUtils.loadScenario(config);
		Population population = scenario.getPopulation();

		int[][] classes = { {6, 14, -169},
							{15, 17, -118},
							{18, 24, 184},
							{25, 29, 336},
							{30, 44, -20},
							{45, 59, 134},
							{60, 64, 46},
							{65, 74, 55},
							{75, 84, 2},
							{85, 99, 28}
		};

		int id2 = 134949;

		ChangePopulation(population, classes, id2);

		PopulationWriter populationWriter = new PopulationWriter(population);
		populationWriter.write("input/v1.0/population_realistic_scenario.xml");

	}

	public static void ChangePopulation(Population population, int[][] classes, int id2){
		PopulationFactory pf = population.getFactory();
		Scenario scenario2 = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		Population population2 = scenario2.getPopulation();

		List<Id> RemoveIdList = new ArrayList<>();
		int random;
		int ageasint;
		String agestring;
		//int counter = 0;
		for (Person person : population.getPersons().values()) {
			// set LinkIds to null, so only the coordinates are used for modelling
			for (Plan plan : person.getPlans()){
				ExtremeScenarioDoubleAndDelete.cleanPlans(plan);
			}

			// ----------------------------------------------------------
			// changing population
			Object age = person.getAttributes().getAttribute("age");
			if (age != null){
				agestring = age.toString();
				ageasint = Integer.parseInt(agestring);

				for (int row=0;row<classes.length;row++){
					if (ageasint > classes[row][0] && ageasint < classes[row][1]){
						// counter = 0;
						// while (counter != classes[row][2]){
							random = RandomGenerator.getDefault().nextInt(1000);
							if (classes[row][2]<0 && random < -classes[row][2]){
								Id id = person.getId();
								RemoveIdList.add(id);
							}
							if (classes[row][2]>0 && random < classes[row][2]){
								id2 = id2 + 1;
								Person person2 = ExtremeScenarioDoubleAndDelete.createDuplicate(person, pf, id2);
								population2.addPerson(person2);
							}
						//}
					}
				}
			}
		}
		for (Id idtoremove : RemoveIdList){
			population.removePerson(idtoremove);
		}
		for (Person person: population2.getPersons().values()){
			population.addPerson(person);
		}
	}

	public static void ChangePopulationStatically(Population population, int[][] classes, int id2){
		PopulationFactory pf = population.getFactory();
		Scenario scenario2 = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		Population population2 = scenario2.getPopulation();

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
					if (ageasint > classes[row][0] && ageasint < classes[row][1]){
						counter = 0;
						while (counter != classes[row][2]){
							if (classes[row][2]<0){
								Id id = person.getId();
								RemoveIdList.add(id);
							}
							if (classes[row][2]>0){
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
		for (Person person: population2.getPersons().values()){
			population.addPerson(person);
		}
	}

}
