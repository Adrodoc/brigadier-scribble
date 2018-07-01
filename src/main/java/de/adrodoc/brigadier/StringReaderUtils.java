package de.adrodoc.brigadier;

import java.util.function.Predicate;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.adrodoc.brigadier.exceptions.MoreExceptions;

public class StringReaderUtils {
  public static String read(StringReader reader, Predicate<Character> allowed) {
    final int start = reader.getCursor();
    while (reader.canRead() && allowed.test(reader.peek())) {
      reader.skip();
    }
    return reader.getString().substring(start, reader.getCursor());
  }

  public static double readDoubleOrDefault(StringReader reader, int defaultValue)
      throws CommandSyntaxException {
    if (reader.canRead() && reader.peek() != CommandDispatcher.ARGUMENT_SEPARATOR_CHAR) {
      return reader.readDouble();
    } else {
      return defaultValue;
    }
  }

  public static void nextArgument(StringReader reader) throws CommandSyntaxException {
    if (reader.canRead()) {
      reader.expect(CommandDispatcher.ARGUMENT_SEPARATOR_CHAR);
    }
  }

  public static String readId(StringReader reader) throws CommandSyntaxException {
    String result = StringReaderUtils.read(reader, c -> isAllowedInId(c));
    int colonIndex = result.indexOf(':');
    if (colonIndex != result.lastIndexOf(':')) {
      throw MoreExceptions.MORE_EXCEPTIONS.invalidId().createWithContext(reader);
    }
    if (colonIndex == -1) {
      return "minecraft:" + result;
    } else {
      return result;
    }
  }

  public static boolean isAllowedInId(char c) {
    return c >= '0' && c <= '9'//
        || c >= 'A' && c <= 'Z'//
        || c >= 'a' && c <= 'z'//
        || c == '_' //
        || c == ':'//
        || c == '-'//
        || c == '/'//
        || c == '.'//
    ;
  }
}
