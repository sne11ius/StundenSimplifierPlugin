package nu.wasis.stunden.plugins.simplifier.config;

import nu.wasis.stunden.util.DateUtils;

import org.joda.time.DateTime;

public class StundenSimplifierPluginConfiguration {

	private String startTime;
	
	public DateTime getStartTime() {
		return DateUtils.TIME_FORMATTER.parseDateTime(startTime);
	}
	
}
