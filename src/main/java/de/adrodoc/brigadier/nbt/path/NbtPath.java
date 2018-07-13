package de.adrodoc.brigadier.nbt.path;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import javax.annotation.concurrent.Immutable;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

@Immutable
public class NbtPath implements Iterable<NbtPathElement> {
  private final ImmutableList<NbtPathElement> elements;

  public NbtPath() {
    this(Collections.emptyList());
  }

  public NbtPath(Iterable<NbtPathElement> elements) {
    this.elements = ImmutableList.copyOf(elements);
  }

  public NbtPath resolve(NbtPathElement elements) {
    return new NbtPath(Iterables.concat(this.elements, Arrays.asList(elements)));
  }

  public NbtPath resolve(String key) {
    return resolve(new CompoundNbtPathElement(key));
  }

  public NbtPath resolveListElement() {
    return resolve(ListElementNbtPathElement.INSTANCE);
  }

  @Override
  public Iterator<NbtPathElement> iterator() {
    return elements.iterator();
  }

  @Override
  public String toString() {
    return Joiner.on('.').join(elements);
  }
}
