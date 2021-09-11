package com.minecraftonline.nope.common.host;

import java.util.Optional;

public interface Child<P extends Host> {

  Optional<P> parent();

}
