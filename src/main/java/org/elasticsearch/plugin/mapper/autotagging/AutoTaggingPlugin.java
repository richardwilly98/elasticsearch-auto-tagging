package org.elasticsearch.plugin.mapper.autotagging;

import static org.elasticsearch.common.collect.Lists.newArrayList;

import java.util.Collection;

import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.mapper.autotagging.AutoTaggingIndexModule;
import org.elasticsearch.plugins.AbstractPlugin;

public class AutoTaggingPlugin extends AbstractPlugin {

	@Override
	public String description() {
		return "Adds the auto tagging type allowing to parse and extract tags";
	}

	@Override
	public String name() {
		return "mapper-auto-tagging";
	}

	@Override
	public Collection<Class<? extends Module>> indexModules() {
		Collection<Class<? extends Module>> modules = newArrayList();
		modules.add(AutoTaggingIndexModule.class);
		return modules;
	}
}
