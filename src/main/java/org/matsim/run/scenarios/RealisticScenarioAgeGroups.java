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
		Config config = ConfigUtils.loadConfig("input/v1.0/dresden-v1.0-10pct.config.xml");
		Scenario scenario = ScenarioUtils.loadScenario(config);
		Population population = scenario.getPopulation();

		int[][] classes = { {6, 14, -169, 0},
							{15, 17, -118, 0},
							{18, 24, 184, 0},
							{25, 29, 336, 0},
							{30, 44, -20, 0},
							{45, 59, 134, 0},
							{60, 64, 46, 0},
							{65, 74, 55, 0},
							{75, 84, 2, 0},
							{85, 99, 280, 0}
		};

		int id2 = 134949;

		ChangePopulationStatically(population, classes, id2);

		PopulationWriter populationWriter = new PopulationWriter(population);
		populationWriter.write("input/v1.0/population_static_scenario-10pct.xml");

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
		Object age;
		Id id;
		Person person2;
		int ageasint;
		int counter = 0;
		String agestring;
		for(int row=0;row<classes.length;row++){
			for (Person person : population.getPersons().values()) {
				age = person.getAttributes().getAttribute("age");
				if (age != null){
					agestring = age.toString();
					ageasint = Integer.parseInt(agestring);
					if (ageasint > classes[row][0] && ageasint < classes[row][1]){
						classes[row][3]++;
					}
				}
			}
		}


		for(int row=0;row<classes.length;row++) {
			counter = 0;
			while (counter <= (Math.abs(classes[row][2]) / 1000) * classes[row][3]) {
				for (Person person : population.getPersons().values()) {
					// set LinkIds to null, so only the coordinates are used for modelling
					for (Plan plan : person.getPlans()){
						ExtremeScenarioDoubleAndDelete.cleanPlans(plan);
					}

			// ----------------------------------------------------------
			// changing population
			age = person.getAttributes().getAttribute("age");
			if (age != null){
				agestring = age.toString();
				ageasint = Integer.parseInt(agestring);


				for(int row=0;row<classes.length;row++){
					if (ageasint > classes[row][0] && ageasint < classes[row][1]){
						counter = 0;
						while (counter <= Math.abs(classes[row][2]/classes[row][3])){
							if (classes[row][2]<0){
								id = person.getId();
								RemoveIdList.add(id);
							}
							if (classes[row][2]>0){
								id2 = id2 + 1;
								person2 = ExtremeScenarioDoubleAndDelete.createDuplicate(person, pf, id2);
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
