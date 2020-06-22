package minecraftonline.nope.control;

import com.google.common.base.Preconditions;

import java.io.Serializable;
import javax.annotation.Nonnull;

public class Setting<T extends Serializable> {

  private Parameter<T> parameter;
  private T value;

  /**
   * Generic constructor.
   *
   * @param parameter The parameter holding information about this setting
   * @param value     The value set for this instance of Setting
   */
  public Setting(@Nonnull Parameter<T> parameter,
                 @Nonnull T value) {
    Preconditions.checkNotNull(parameter);
    Preconditions.checkNotNull(value);
    this.parameter = parameter;
    this.value = value;
  }

  public Parameter<T> getParameter() {
    return parameter;
  }

  public T getValue() {
    return value;
  }

}
