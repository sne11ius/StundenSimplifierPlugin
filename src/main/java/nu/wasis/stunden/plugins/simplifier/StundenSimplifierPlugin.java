package nu.wasis.stunden.plugins.simplifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import nu.wasis.stunden.exception.InvalidConfigurationException;
import nu.wasis.stunden.model.Day;
import nu.wasis.stunden.model.Entry;
import nu.wasis.stunden.model.WorkPeriod;
import nu.wasis.stunden.plugin.ProcessPlugin;
import nu.wasis.stunden.plugins.simplifier.config.StundenSimplifierPluginConfiguration;
import nu.wasis.stunden.plugins.simplifier.exception.ImpossibleSimplificationException;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

@PluginImplementation
public class StundenSimplifierPlugin implements ProcessPlugin {

	private static final Logger LOG = Logger.getLogger(StundenSimplifierPlugin.class);
	
	public WorkPeriod process(final WorkPeriod workPeriod, final Object configuration) {
		LOG.info("Simplifying entries...");
		if (null == configuration || !(configuration instanceof StundenSimplifierPluginConfiguration)) {
			throw new InvalidConfigurationException("Configuration null or wrong type. You probably need to fix your configuration file.");
		}
		final StundenSimplifierPluginConfiguration myConfig = (StundenSimplifierPluginConfiguration) configuration;
		DateTime startTime = null;
		try {
			startTime = myConfig.getStartTime();
		} catch (final IllegalArgumentException e) {
			throw new InvalidConfigurationException("Parameter `startTime' is required and must contain a start time like in `hh:mm'.");
		}
		LOG.debug("Merging entries...");
		final WorkPeriod simplifiedWorkPeriod = new WorkPeriod();
		for (final Day oldDay : workPeriod.getDays()) {
			final List<Entry> simplifiedEntries = mergeEntries(oldDay.getEntries());
			simplifiedWorkPeriod.getDays().add(new Day(oldDay.getDate(), simplifiedEntries));
		}
		LOG.debug("...done");
		final WorkPeriod finalWorkPeriod = adjustStartTimes(simplifiedWorkPeriod, startTime);
		LOG.info("...done");
		return finalWorkPeriod;
	}

	private List<Entry> mergeEntries(final List<Entry> entries) {
		final Map<String, Entry> simplifiedEntries = new HashMap<>();
		for (final Entry oldEntry : entries) {
			Entry simplifiedEntry = simplifiedEntries.get(oldEntry.getProject().getName());
			if (null == simplifiedEntry) {
				simplifiedEntry = new Entry(oldEntry);
				simplifiedEntries.put(simplifiedEntry.getProject().getName(), simplifiedEntry);
			} else {
				final DateTime newEnd = simplifiedEntry.getEnd().plus(oldEntry.getDuration());
				if (oldEntry.isBreak() && !simplifiedEntry.isBreak()) {
					throw new ImpossibleSimplificationException("Cannot simplify two entries of one is tagged as break while the other is not D:");
				}
				simplifiedEntry.setEnd(newEnd);
			}
		}
		return new ArrayList<>(simplifiedEntries.values());
	}
	
	private WorkPeriod adjustStartTimes(final WorkPeriod workPeriod, final DateTime startTime) {
		LOG.debug("Adjusting start times...");
		final WorkPeriod result = new WorkPeriod();
		
		for (final Day oldDay : workPeriod.getDays()) {
			final List<Entry> adjustedEntries = new LinkedList<>();
			DateTime currentStartTime = startTime;
			for (final Entry oldEntry : oldDay.getEntries()) {
				final DateTime currentEndTime = currentStartTime.plus(oldEntry.getDuration());
				adjustedEntries.add(new Entry(currentStartTime, currentEndTime, oldEntry.getProject(), oldEntry.isBreak()));
				currentStartTime = currentEndTime; 
			}
			result.getDays().add(new Day(oldDay.getDate(), adjustedEntries));
		}
		
		LOG.debug("...done");
		return result;
	}

	public Class<?> getConfigurationClass() {
		return StundenSimplifierPluginConfiguration.class;
	}

}
