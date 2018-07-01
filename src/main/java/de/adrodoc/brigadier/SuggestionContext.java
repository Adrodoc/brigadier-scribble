package de.adrodoc.brigadier;

import java.util.Collections;
import java.util.Set;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;

public interface SuggestionContext {
  default Set<String> getBlockTags() {
    return Collections.emptySet();
  }

  default Set<String> getBlockTypes() {
    return Collections.emptySet();
  }

  default Set<String> getTeams() {
    return Collections.emptySet();
  }

  default SetMultimap<String, String> getPossibleBlockProperties(String id) {
    return ImmutableSetMultimap.of();
  }
}
