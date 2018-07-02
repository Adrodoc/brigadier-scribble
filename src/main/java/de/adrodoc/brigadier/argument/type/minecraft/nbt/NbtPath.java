package de.adrodoc.brigadier.argument.type.minecraft.nbt;

import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.concurrent.Immutable;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

@Immutable
public class NbtPath implements Iterable<String> {
  private final ImmutableList<String> elements;

  public NbtPath(String... elements) {
    this.elements = ImmutableList.copyOf(elements);
  }

  public NbtPath(Iterable<String> elements) {
    this.elements = ImmutableList.copyOf(elements);
  }

  public NbtPath with(String... elements) {
    return with(Arrays.asList(elements));
  }

  public NbtPath with(Iterable<String> elements) {
    return new NbtPath(Iterables.concat(this.elements, elements));
  }

  @Override
  public Iterator<String> iterator() {
    return elements.iterator();
  }

  @Override
  public String toString() {
    return Joiner.on('.').join(elements);
  }
}
