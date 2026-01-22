package org.matsim.run.scenarios;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;
import scala.Int;

import java.util.ArrayList;
import java.util.List;

import java.util.random.RandomGenerator;

public class ExtremeScenarioDoubleAndDelete {
	public static void main(String[] args) {
		Config config = ConfigUtils.loadConfig("input/v1.0/dresden-v1.0-1pct.config.xml");
		Scenario scenario = ScenarioUtils.loadScenario(config);
		Population population = scenario.getPopulation();
		PopulationFactory pf = scenario.getPopulation().getFactory();
		Scenario scenario2 = ScenarioUtils.createScenario(ConfigUtils.createConfig());
		Population population2 = scenario2.getPopulation();

		int id2 = 134949;
		List<Id> RemoveIdList = new ArrayList<>();
		for (Person person : population.getPersons().values()) {
			// set LinkIds to null, so only the coordinates are used for modelling
			for (Plan plan : person.getPlans()){
				for (PlanElement planElement : plan.getPlanElements()) {
					if (planElement instanceof Activity){
						Activity activity = (Activity) planElement;
						activity.setLinkId(null);
					} else { // Plans consist of Activities and Legs, so if !Activity => Leg
						Leg leg = (Leg) planElement;
						leg.setRoute(null);
					}
				}
			}

			// ----------------------------------------------------------
			// changing population
			Object age = person.getAttributes().getAttribute("age");
			if (age != null){
				String agestring = age.toString();
				int ageasint = Integer.parseInt(agestring);

				// all persons above 65 yo are doubled in the extreme scenario
				if (ageasint > 65) {
					id2 = id2 + 1;
					String strid = String.valueOf(id2);
					// creating a second person (copying)
					Person person2 = pf.createPerson(Id.createPersonId("99" + strid));

					// copying attributes of first person to person2
					person2.getAttributes().putAttribute("age", person.getAttributes().getAttribute("age"));
					person2.getAttributes().putAttribute("gender", person.getAttributes().getAttribute("gender"));
					person2.getAttributes().putAttribute("sex", person.getAttributes().getAttribute("sex"));
					person2.getAttributes().putAttribute("carAvail", person.getAttributes().getAttribute("carAvail"));
					person2.getAttributes().putAttribute("ptTicket", person.getAttributes().getAttribute("ptTicket"));
					person2.getAttributes().putAttribute("income", person.getAttributes().getAttribute("income"));
					person2.getAttributes().putAttribute("hhIncome", person.getAttributes().getAttribute("hhIncome"));
					person2.getAttributes().putAttribute("hhSize", person.getAttributes().getAttribute("hhSize"));
					person2.getAttributes().putAttribute("homeRegioStaR17", person.getAttributes().getAttribute("homeRegioStaR17"));

					// ------------------------------------------------------
					// copying plans of first person to person2
					person2.addPlan(person.getSelectedPlan());

					// manipulating plans: changing coordinates of activities randomly
					moveActivities(person2, person, 0.2);
					// changing home coordinates to be considered when changing activities


					population2.addPerson(person2);
				}
				// all minors are deleted in this extreme scenario
				if (ageasint < 18){
					Id id = person.getId();
					RemoveIdList.add(id);
				}
			}
		}
		for (Id idtoremove : RemoveIdList){
			population.removePerson(idtoremove);
		}


		PopulationWriter populationWriter = new PopulationWriter(population);
		populationWriter.write("input/v1.0/population_extreme_scenario.xml");

	}


	private static void moveActivities(Person copy, Person original, double changepar) {
		double x_diff_home = RandomGenerator.getDefault().nextGaussian(changepar,0.5*changepar);
		double y_diff_home = RandomGenerator.getDefault().nextGaussian(changepar,0.5*changepar);
		copy.getAttributes().putAttribute("home_x", ((double) original.getAttributes().getAttribute("home_x") + x_diff_home));
		copy.getAttributes().putAttribute("home_y", ((double) original.getAttributes().getAttribute("home_y") + y_diff_home));

		for (Plan plan : copy.getPlans()){
			for (PlanElement planElement : plan.getPlanElements()) {
				if (planElement instanceof Activity){
					Activity activity = (Activity) planElement;
					if (activity.getCoord().getX() == (double) original.getAttributes().getAttribute("home_x") && (activity.getCoord().getY() == (double) original.getAttributes().getAttribute("home_y"))){
						double x = activity.getCoord().getX() + x_diff_home;
						double y = activity.getCoord().getY() + y_diff_home;
						Coord coord = new Coord(x, y);
						activity.setCoord(coord);
					}
					else {
						double x = activity.getCoord().getX() + RandomGenerator.getDefault().nextGaussian(changepar,0.5*changepar);
						double y = activity.getCoord().getY() + RandomGenerator.getDefault().nextGaussian(changepar,0.5*changepar);
						Coord coord = new Coord(x, y);
						activity.setCoord(coord);
					}
				}
			}
		}
	}







}
