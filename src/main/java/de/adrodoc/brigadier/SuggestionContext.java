package de.adrodoc.brigadier;

import java.util.Collections;
import java.util.Set;

public interface SuggestionContext {
  default Set<String> getTeams() {
    return Collections.emptySet();
  }
}
