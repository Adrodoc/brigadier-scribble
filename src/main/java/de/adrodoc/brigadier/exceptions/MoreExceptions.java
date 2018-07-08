package de.adrodoc.brigadier.exceptions;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.BuiltInExceptions;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class MoreExceptions extends BuiltInExceptions {
  private static final Dynamic3CommandExceptionType BLOCK_DOES_NOT_ACCEPT_VALUE_FOR_PROPERTY =
      new Dynamic3CommandExceptionType((block, value, property) -> new LiteralMessage(
          "Block " + block + " does not accept '" + value + "' for " + property + " property"));
  private static final Dynamic2CommandExceptionType BLOCK_DOES_NOT_HAVE_NBT =
      new Dynamic2CommandExceptionType((block, nbtPath) -> new LiteralMessage(
          "Block " + block + " does not have nbt '" + nbtPath + "'"));
  private static final Dynamic2CommandExceptionType BLOCK_DOES_NOT_HAVE_PROPERTY =
      new Dynamic2CommandExceptionType((block, property) -> new LiteralMessage(
          "Block " + block + " does not have property '" + property + "'"));
  private static final SimpleCommandExceptionType EXPECTED_3_COORDINATES =
      new SimpleCommandExceptionType(new LiteralMessage("Incomplete (expected 3 coordinates)"));
  private static final SimpleCommandExceptionType EXPECTED_CLOSING_SQUARE_BRACKET =
      new SimpleCommandExceptionType(
          new LiteralMessage("Expected closing ] for block state properties"));
  @Deprecated
  private static final SimpleCommandExceptionType EXPECTED_KEY =
      new SimpleCommandExceptionType(new LiteralMessage("Expected key"));
  private static final SimpleCommandExceptionType EXPECTED_VALUE =
      new SimpleCommandExceptionType(new LiteralMessage("Expected value"));
  private static final Dynamic2CommandExceptionType EXPECTED_VALUE_FOR_PROPERTY_ON_BLOCK =
      new Dynamic2CommandExceptionType((property, block) -> new LiteralMessage(
          "Expected value for property '" + property + "' on block " + block));
  private static final SimpleCommandExceptionType INVALID_ID =
      new SimpleCommandExceptionType(new LiteralMessage("Invalid ID"));
  public static final MoreExceptions MORE_EXCEPTIONS = new MoreExceptions();
  private static final DynamicCommandExceptionType UNKNOWN_BLOCK_TAG =
      new DynamicCommandExceptionType(
          value -> new LiteralMessage("Unknown block tag '" + value + "'"));
  private static final DynamicCommandExceptionType UNKNOWN_BLOCK_TYPE =
      new DynamicCommandExceptionType(
          value -> new LiteralMessage("Unknown block type '" + value + "'"));

  public Dynamic3CommandExceptionType blockDoesNotAcceptValueForProperty() {
    return BLOCK_DOES_NOT_ACCEPT_VALUE_FOR_PROPERTY;
  }

  public Dynamic2CommandExceptionType blockDoesNotHaveNbt() {
    return BLOCK_DOES_NOT_HAVE_NBT;
  }

  public Dynamic2CommandExceptionType blockDoesNotHaveProperty() {
    return BLOCK_DOES_NOT_HAVE_PROPERTY;
  }

  public SimpleCommandExceptionType expected3Coordinates() {
    return EXPECTED_3_COORDINATES;
  }

  public SimpleCommandExceptionType expectedClosingSquareBracket() {
    return EXPECTED_CLOSING_SQUARE_BRACKET;
  }

  @Deprecated
  public SimpleCommandExceptionType expectedKey() {
    return EXPECTED_KEY;
  }

  public SimpleCommandExceptionType expectedValue() {
    return EXPECTED_VALUE;
  }

  public Dynamic2CommandExceptionType expectedValueForPropertyOnBlock() {
    return EXPECTED_VALUE_FOR_PROPERTY_ON_BLOCK;
  }

  public SimpleCommandExceptionType invalidId() {
    return INVALID_ID;
  }

  public DynamicCommandExceptionType unknownBlockTag() {
    return UNKNOWN_BLOCK_TAG;
  }

  public DynamicCommandExceptionType unknownBlockType() {
    return UNKNOWN_BLOCK_TYPE;
  }
}
