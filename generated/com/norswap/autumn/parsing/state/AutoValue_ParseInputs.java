
package com.norswap.autumn.parsing.state;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.util.Array;
import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_ParseInputs extends ParseInputs {

  private final ParsingExpression pe;
  private final int start;
  private final int blackStart;
  private final int precedence;
  private final boolean recordErrors;
  private final Array<ParseInputs.Entry> customInputs;

  AutoValue_ParseInputs(
      ParsingExpression pe,
      int start,
      int blackStart,
      int precedence,
      boolean recordErrors,
      Array<ParseInputs.Entry> customInputs) {
    if (pe == null) {
      throw new NullPointerException("Null pe");
    }
    this.pe = pe;
    this.start = start;
    this.blackStart = blackStart;
    this.precedence = precedence;
    this.recordErrors = recordErrors;
    if (customInputs == null) {
      throw new NullPointerException("Null customInputs");
    }
    this.customInputs = customInputs;
  }

  @Override
  public ParsingExpression pe() {
    return pe;
  }

  @Override
  public int start() {
    return start;
  }

  @Override
  public int blackStart() {
    return blackStart;
  }

  @Override
  public int precedence() {
    return precedence;
  }

  @Override
  public boolean recordErrors() {
    return recordErrors;
  }

  @Override
  public Array<ParseInputs.Entry> customInputs() {
    return customInputs;
  }

  @Override
  public String toString() {
    return "ParseInputs{"
        + "pe=" + pe + ", "
        + "start=" + start + ", "
        + "blackStart=" + blackStart + ", "
        + "precedence=" + precedence + ", "
        + "recordErrors=" + recordErrors + ", "
        + "customInputs=" + customInputs
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof ParseInputs) {
      ParseInputs that = (ParseInputs) o;
      return (this.pe.equals(that.pe()))
           && (this.start == that.start())
           && (this.blackStart == that.blackStart())
           && (this.precedence == that.precedence())
           && (this.recordErrors == that.recordErrors())
           && (this.customInputs.equals(that.customInputs()));
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= pe.hashCode();
    h *= 1000003;
    h ^= start;
    h *= 1000003;
    h ^= blackStart;
    h *= 1000003;
    h ^= precedence;
    h *= 1000003;
    h ^= recordErrors ? 1231 : 1237;
    h *= 1000003;
    h ^= customInputs.hashCode();
    return h;
  }

}
