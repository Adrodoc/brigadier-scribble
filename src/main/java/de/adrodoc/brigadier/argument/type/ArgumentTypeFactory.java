package de.adrodoc.brigadier.argument.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.adrodoc.brigadier.DataContext;
import de.adrodoc.brigadier.SuggestionContext;
import de.adrodoc.brigadier.argument.type.minecraft.BlockPosArgumentType;
import de.adrodoc.brigadier.argument.type.minecraft.ColorArgumentType;
import de.adrodoc.brigadier.argument.type.minecraft.ResourceLocationArgumentType;
import de.adrodoc.brigadier.argument.type.minecraft.TeamArgumentType;
import de.adrodoc.brigadier.argument.type.minecraft.block.state.BlockStateArgumentType;

public class ArgumentTypeFactory {
  private final DataContext data;
  private final SuggestionContext suggestionsContext;
  private @Nullable ImmutableMap<String, Function<JsonObject, ArgumentType<?>>> argumentTypeFactories;

  public ArgumentTypeFactory(DataContext data, SuggestionContext context) {
    this.data = requireNonNull(data, "data == null!");
    this.suggestionsContext = requireNonNull(context, "context == null!");
  }

  public ArgumentType<?> getArgumentType(String identifier, @Nullable JsonObject properties) {
    Function<JsonObject, ArgumentType<?>> factory = getFactory(identifier);
    checkArgument(factory != null, "Unknown argument type '" + identifier + "'");
    return factory.apply(properties);
  }

  private Function<JsonObject, ArgumentType<?>> getFactory(String name) {
    return getFactories().get(name);
  }

  private ImmutableMap<String, Function<JsonObject, ArgumentType<?>>> getFactories() {
    if (argumentTypeFactories == null) {
      Map<String, Function<JsonObject, ArgumentType<?>>> parsers = new HashMap<>();
      parsers.put("brigadier:bool", this::brigadierBool);
      parsers.put("brigadier:double", this::brigadierDouble);
      parsers.put("brigadier:float", this::brigadierFloat);
      parsers.put("brigadier:integer", this::brigadierInteger);
      parsers.put("brigadier:string", this::brigadierString);
      parsers.put("minecraft:block_pos", this::minecraftBlockPos);
      parsers.put("minecraft:block_predicate", this::minecraftBlockPredicate);
      parsers.put("minecraft:block_state", this::minecraftBlockState);
      parsers.put("minecraft:color", this::minecraftColor);
      parsers.put("minecraft:component", this::minecraftComponent);
      parsers.put("minecraft:entity", this::minecraftEntity);
      parsers.put("minecraft:entity_anchor", this::minecraftEntityAnchor);
      parsers.put("minecraft:function", this::minecraftFunction);
      parsers.put("minecraft:game_profile", this::minecraftGameProfile);
      parsers.put("minecraft:item_enchantment", this::minecraftItemEnchantment);
      parsers.put("minecraft:item_predicate", this::minecraftItemPredicate);
      parsers.put("minecraft:item_slot", this::minecraftItemSlot);
      parsers.put("minecraft:item_stack", this::minecraftItemStack);
      parsers.put("minecraft:message", this::minecraftMessage);
      parsers.put("minecraft:mob_effect", this::minecraftMobEffect);
      parsers.put("minecraft:nbt", this::minecraftNbt);
      parsers.put("minecraft:nbt_path", this::minecraftNbtPath);
      parsers.put("minecraft:objective", this::minecraftObjective);
      parsers.put("minecraft:objective_criteria", this::minecraftObjectiveCriteria);
      parsers.put("minecraft:operation", this::minecraftOperation);
      parsers.put("minecraft:particle", this::minecraftParticle);
      parsers.put("minecraft:range", this::minecraftRange);
      parsers.put("minecraft:resource_location", this::minecraftResourceLocation);
      parsers.put("minecraft:rotation", this::minecraftRotation);
      parsers.put("minecraft:score_holder", this::minecraftScoreHolder);
      parsers.put("minecraft:scoreboard_slot", this::minecraftScoreboardSlot);
      parsers.put("minecraft:swizzle", this::minecraftSwizzle);
      parsers.put("minecraft:team", this::minecraftTeam);
      parsers.put("minecraft:vec2", this::minecraftVec2);
      parsers.put("minecraft:vec3", this::minecraftVec3);
      argumentTypeFactories = ImmutableMap.copyOf(parsers);
    }
    return argumentTypeFactories;
  }

  private void checkNoProperties(@Nullable JsonObject properties) {
    checkArgument(properties == null, "Properties are not supported for this argument type");
  }

  public BoolArgumentType brigadierBool(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return BoolArgumentType.bool();
  }

  public DoubleArgumentType brigadierDouble(@Nullable JsonObject properties) {
    if (properties != null) {
      JsonElement min = properties.get("min");
      if (min != null) {
        double minValue = min.getAsDouble();
        JsonElement max = properties.get("max");
        if (max != null) {
          double maxValue = max.getAsDouble();
          return DoubleArgumentType.doubleArg(minValue, maxValue);
        }
        return DoubleArgumentType.doubleArg(minValue);
      }
    }
    return DoubleArgumentType.doubleArg();
  }

  public FloatArgumentType brigadierFloat(@Nullable JsonObject properties) {
    if (properties != null) {
      JsonElement min = properties.get("min");
      if (min != null) {
        float minValue = min.getAsFloat();
        JsonElement max = properties.get("max");
        if (max != null) {
          float maxValue = max.getAsFloat();
          return FloatArgumentType.floatArg(minValue, maxValue);
        }
        return FloatArgumentType.floatArg(minValue);
      }
    }
    return FloatArgumentType.floatArg();
  }

  public IntegerArgumentType brigadierInteger(@Nullable JsonObject properties) {
    if (properties != null) {
      JsonElement min = properties.get("min");
      if (min != null) {
        int minValue = min.getAsInt();
        JsonElement max = properties.get("max");
        if (max != null) {
          int maxValue = max.getAsInt();
          return IntegerArgumentType.integer(minValue, maxValue);
        }
        return IntegerArgumentType.integer(minValue);
      }
    }
    return IntegerArgumentType.integer();
  }

  public StringArgumentType brigadierString(@Nullable JsonObject properties) {
    String type = properties.get("type").getAsString();
    switch (type) {
      case "word":
        return StringArgumentType.word();
      case "phrase":
        return StringArgumentType.string();
      case "greedy":
        return StringArgumentType.greedyString();
      default:
        throw new IllegalArgumentException("Unknown string type '" + type + "'");
    }
  }

  public BlockPosArgumentType minecraftBlockPos(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new BlockPosArgumentType();
  }

  public UnsupportedArgumentType minecraftBlockPredicate(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  public BlockStateArgumentType minecraftBlockState(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new BlockStateArgumentType(data);
  }

  public ColorArgumentType minecraftColor(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new ColorArgumentType();
  }

  public UnsupportedArgumentType minecraftComponent(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  @SuppressWarnings("unused")
  public UnsupportedArgumentType minecraftEntity(JsonObject properties) {
    String amount = properties.get("amount").getAsString();
    String type = properties.get("type").getAsString();
    return new UnsupportedArgumentType();
  }

  public UnsupportedArgumentType minecraftEntityAnchor(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  public UnsupportedArgumentType minecraftFunction(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  public UnsupportedArgumentType minecraftGameProfile(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  public UnsupportedArgumentType minecraftItemEnchantment(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  public UnsupportedArgumentType minecraftItemPredicate(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  public UnsupportedArgumentType minecraftItemSlot(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  public UnsupportedArgumentType minecraftItemStack(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  public StringArgumentType minecraftMessage(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return StringArgumentType.greedyString();
  }

  public UnsupportedArgumentType minecraftMobEffect(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  public UnsupportedArgumentType minecraftNbt(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  public UnsupportedArgumentType minecraftNbtPath(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  public UnsupportedArgumentType minecraftObjective(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  public UnsupportedArgumentType minecraftObjectiveCriteria(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  public UnsupportedArgumentType minecraftOperation(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  public UnsupportedArgumentType minecraftParticle(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  @SuppressWarnings("unused")
  public UnsupportedArgumentType minecraftRange(JsonObject properties) {
    boolean decimals = properties.get("decimals").getAsBoolean();
    return new UnsupportedArgumentType();
  }

  public ResourceLocationArgumentType minecraftResourceLocation(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new ResourceLocationArgumentType();
  }

  public UnsupportedArgumentType minecraftRotation(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  public UnsupportedArgumentType minecraftScoreboardSlot(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  @SuppressWarnings("unused")
  public UnsupportedArgumentType minecraftScoreHolder(JsonObject properties) {
    String amount = properties.get("amount").getAsString();
    return new UnsupportedArgumentType();
  }

  public UnsupportedArgumentType minecraftSwizzle(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  public TeamArgumentType minecraftTeam(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new TeamArgumentType(suggestionsContext);
  }

  public UnsupportedArgumentType minecraftVec2(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }

  public UnsupportedArgumentType minecraftVec3(@Nullable JsonObject properties) {
    checkNoProperties(properties);
    return new UnsupportedArgumentType();
  }
}
