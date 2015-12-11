
package com.norswap.autumn.parsing.extensions.cluster;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.extensions.cluster.expressions.ExpressionCluster;
import com.norswap.util.Array;
import java.util.HashMap;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_ClusterState_Inputs extends ClusterState.Inputs {

  private final Object seeds;
  private final HashMap<ParsingExpression, ClusterState.Precedence> precedences;
  private final Array<ExpressionCluster> history;

  AutoValue_ClusterState_Inputs(
      @com.norswap.util.annotations.Nullable Object seeds,
      HashMap<ParsingExpression, ClusterState.Precedence> precedences,
      Array<ExpressionCluster> history) {
    this.seeds = seeds;
    if (precedences == null) {
      throw new NullPointerException("Null precedences");
    }
    this.precedences = precedences;
    if (history == null) {
      throw new NullPointerException("Null history");
    }
    this.history = history;
  }

  @com.norswap.util.annotations.Nullable
  @Override
  Object seeds() {
    return seeds;
  }

  @Override
  HashMap<ParsingExpression, ClusterState.Precedence> precedences() {
    return precedences;
  }

  @Override
  Array<ExpressionCluster> history() {
    return history;
  }

  @Override
  public String toString() {
    return "Inputs{"
        + "seeds=" + seeds + ", "
        + "precedences=" + precedences + ", "
        + "history=" + history
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ClusterState.Inputs) {
      ClusterState.Inputs that = (ClusterState.Inputs) o;
      return ((this.seeds == null) ? (that.seeds() == null) : this.seeds.equals(that.seeds()))
           && (this.precedences.equals(that.precedences()))
           && (this.history.equals(that.history()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= (seeds == null) ? 0 : seeds.hashCode();
    h *= 1000003;
    h ^= precedences.hashCode();
    h *= 1000003;
    h ^= history.hashCode();
    return h;
  }

}
