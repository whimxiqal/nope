package me.pietelite.nope.common.api;

import me.pietelite.nope.common.MockNope;
import me.pietelite.nope.common.api.edit.Alteration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

public abstract class ApiTest {

  protected static final String D1 = "d1";
  protected static final String D2 = "d2";

  MockNope nope;
  NopeService service;

  @BeforeEach
  void setUp() {
    nope = MockNope.init(D1, D2);
    service = NopeServiceProvider.service();
  }

  void assertSuccess(Alteration alteration) {
    Assertions.assertEquals(Alteration.Result.SUCCESS, alteration.result());
  }

  void assertFail(Alteration alteration) {
    Assertions.assertEquals(Alteration.Result.FAILURE, alteration.result(), alteration.message().orElse(""));
  }

}
