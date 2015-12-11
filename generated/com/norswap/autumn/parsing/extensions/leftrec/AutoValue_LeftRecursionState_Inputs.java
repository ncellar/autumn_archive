
package com.norswap.autumn.parsing.extensions.leftrec;

import com.norswap.autumn.parsing.ParsingExpression;
import java.util.HashSet;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_LeftRecursionState_Inputs extends LeftRecursionState.Inputs {

  private final Object seeds;
  private final HashSet<ParsingExpression> blocked;

  AutoValue_LeftRecursionState_Inputs(
      @com.norswap.util.annotations.Nullable Object seeds,
      HashSet<ParsingExpression> blocked) {
    this.seeds = seeds;
    if (blocked == null) {
      throw new NullPointerException("Null blocked");
    }
    this.blocked = blocked;
  }

  @com.norswap.util.annotations.Nullable
  @Override
  Object seeds() {
    return seeds;
  }

  @Override
  HashSet<ParsingExpression> blocked() {
    return blocked;
  }

  @Override
  public String toString() {
    return "Inputs{"
        + "seeds=" + seeds + ", "
        + "blocked=" + blocked
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof LeftRecursionState.Inputs) {
      LeftRecursionState.Inputs that = (LeftRecursionState.Inputs) o;
      return ((this.seeds == null) ? (that.seeds() == null) : this.seeds.equals(that.seeds()))
           && (this.blocked.equals(that.blocked()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= (seeds == null) ? 0 : seeds.hashCode();
    h *= 1000003;
    h ^= blocked.hashCode();
    return h;
  }

}
