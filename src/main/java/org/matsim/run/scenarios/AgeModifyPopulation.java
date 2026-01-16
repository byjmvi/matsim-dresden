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
		Config config = ConfigUtils.loadConfig("input/v1.0/dresden-v1.0-1pct.config.xml");
		Scenario scenario = ScenarioUtils.createScenario(config);

		Population population = scenario.getPopulation();
		int newid = 134949;
		for (Person person : population.getPersons().values()) {
			int age = (int) person.getAttributes().getAttribute("age");
			if (age > 65) {
				newid = newid + 1;
				String strid = String.valueOf(newid);
				PopulationFactory pf = scenario.getPopulation().getFactory();

				Person newperson = pf.createPerson(Id.createPersonId("99" + strid));

				for (Plan plan : person.getPlans()) {
					person.setSelectedPlan(plan);
					newperson.createCopyOfSelectedPlanAndMakeSelected();
				}
				for (Plan plan : newperson.getPlans()) {
					for (PlanElement planElement : plan.getPlanElements()) {
						if (planElement instanceof Activity) {
							Activity activity = (Activity) planElement;
							//activity.setLinkId(null);
						} else { // Plans consist of Activities and Legs, so if !Activity => Leg
							Leg leg = (Leg) planElement;
							//leg.setRoute(null);
						}
					}
				}
			}


		}

		PopulationWriter populationWriter = new PopulationWriter(population);
		populationWriter.write("input/v1.0/dresden-v1.0-1pct.config.xml");


	}
}
