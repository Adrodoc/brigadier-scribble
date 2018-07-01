package de.adrodoc.brigadier.argument.type.minecraft;

import static java.math.RoundingMode.FLOOR;
import javax.annotation.concurrent.Immutable;
import com.google.common.math.DoubleMath;

@Immutable
public class BlockPos {
  public interface Value {
    static Value absolute(int value) {
      return new AbsoluteValue(value);
    }

    static Value relative(double value) {
      return new RelativeValue(value);
    }

    int resolve(double relativeTo);
  }
  @Immutable
  private static class AbsoluteValue implements Value {
    private final int value;

    public AbsoluteValue(int value) {
      this.value = value;
    }

    @Override
    public int resolve(double relativeTo) {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }
  }
  @Immutable
  private static class RelativeValue implements Value {
    private final double value;

    public RelativeValue(double value) {
      this.value = value;
    }

    @Override
    public int resolve(double relativeTo) {
      return DoubleMath.roundToInt(relativeTo + value, FLOOR);
    }

    @Override
    public String toString() {
      return "~" + value;
    }
  }

  private final Value x, y, z;

  public BlockPos(Value x, Value y, Value z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Value getX() {
    return x;
  }

  public Value getY() {
    return y;
  }

  public Value getZ() {
    return z;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((x == null) ? 0 : x.hashCode());
    result = prime * result + ((y == null) ? 0 : y.hashCode());
    result = prime * result + ((z == null) ? 0 : z.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BlockPos other = (BlockPos) obj;
    if (x == null) {
      if (other.x != null)
        return false;
    } else if (!x.equals(other.x))
      return false;
    if (y == null) {
      if (other.y != null)
        return false;
    } else if (!y.equals(other.y))
      return false;
    if (z == null) {
      if (other.z != null)
        return false;
    } else if (!z.equals(other.z))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "BlockPos [x=" + x + ", y=" + y + ", z=" + z + "]";
  }
}
