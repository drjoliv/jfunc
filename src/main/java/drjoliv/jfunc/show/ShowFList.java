package drjoliv.jfunc.show;

import static drjoliv.jfunc.data.list.FList.*;

import drjoliv.jfunc.data.list.FList;

public class ShowFList extends Show<Integer> {

  private static final ShowFList INSTANCE = new ShowFList();

  @Override
  public FList<Character> show(Integer a) {
    Show<Integer> def = defaultShow();
    return def.show(a);
  }
}
