package de.adrodoc.brigadier.nbt.path;

import javax.annotation.Nullable;
import de.adrodoc.brigadier.nbt.spec.CompoundNbtSpecNode;
import de.adrodoc.brigadier.nbt.spec.ListNbtSpecNode;
import de.adrodoc.brigadier.nbt.spec.NbtSpecNode;

public interface NbtPathElement {
  @Nullable
  NbtSpecNode of(CompoundNbtSpecNode node);

  @Nullable
  NbtSpecNode of(ListNbtSpecNode node);
}
