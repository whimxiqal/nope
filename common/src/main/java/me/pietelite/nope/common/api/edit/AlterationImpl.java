package me.pietelite.nope.common.api.edit;

import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class AlterationImpl implements Alteration {

  private final Result result;
  private final String message;

  public static AlterationImpl of(Result result) {
    return new AlterationImpl(result, null);
  }

  public static AlterationImpl of(Result result, String message) {
    return new AlterationImpl(result, message);
  }

  public static AlterationImpl success() {
    return new AlterationImpl(Result.SUCCESS, null);
  }

  public static AlterationImpl success(String message) {
    return new AlterationImpl(Result.SUCCESS, message);
  }

  public static AlterationImpl warn(String message) {
    return new AlterationImpl(Result.WARNING, message);
  }

  public static AlterationImpl fail(String message) {
    return new AlterationImpl(Result.FAILURE, message);
  }

  public static AlterationImpl nameDoesntExist(String name) {
    return new AlterationImpl(Result.FAILURE, "The name " + name + " doesn't exist");
  }

  private AlterationImpl(Result result, String message) {
    this.result = result;
    this.message = message;
  }

  @Override
  public Result result() {
    return result;
  }

  @Nullable
  @Override
  public Optional<String> message() {
    return Optional.ofNullable(message);
  }
}
