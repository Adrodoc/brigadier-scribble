package de.adrodoc.brigadier.nbt.spec;

import static java.util.Objects.requireNonNull;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import de.adrodoc.brigadier.nbt.path.NbtPathElement;

@Immutable
public class CompoundNbtSpecNode implements NbtSpecNode {
  private final ImmutableMap<String, NbtSpecNode> children;

  public CompoundNbtSpecNode(Map<String, NbtSpecNode> children) {
    this.children = ImmutableMap.copyOf(children);
  }

  @Override
  public NbtType getType() {
    return NbtType.COMPOUND;
  }

  @Override
  public @Nullable NbtSpecNode get(NbtPathElement pathElement) {
    return pathElement.of(this);
  }

  public ImmutableMap<String, NbtSpecNode> getChildren() {
    return children;
  }

  public NbtSpecNode getChild(String key) {
    requireNonNull(key, "key == null!");
    return children.get(key);
  }

  @Override
  public ImmutableSet<String> getChildNames() {
    return children.keySet();
  }
}
