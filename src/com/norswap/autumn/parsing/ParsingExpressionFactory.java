package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.capture.DecorateWithAccessor;
import com.norswap.autumn.parsing.capture.DecorateWithGroup;
import com.norswap.autumn.parsing.capture.DecorateWithKind;
import com.norswap.autumn.parsing.expressions.*;
import com.norswap.autumn.parsing.expressions.Capture;
import com.norswap.autumn.parsing.capture.Decorate;
import com.norswap.autumn.parsing.extensions.cluster.expressions.ExpressionCluster;
import com.norswap.autumn.parsing.extensions.cluster.expressions.ExpressionCluster.Group;
import com.norswap.autumn.parsing.expressions.Whitespace;
import com.norswap.autumn.parsing.extensions.cluster.expressions.Filter;
import com.norswap.autumn.parsing.extensions.cluster.expressions.WithMinPrecedence;
import com.norswap.autumn.parsing.extensions.leftrec.LeftRecursive;
import java.util.Arrays;

public final class ParsingExpressionFactory
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // CAPTURES

    public static Capture capture(Decorate[] decorations, ParsingExpression operand)
    {
        return new Capture(true, false, operand, decorations);
    }

    // ---------------------------------------------------------------------------------------------

    public static Capture capture(ParsingExpression operand)
    {
        return capture(new Decorate[0], operand);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression capture(String name, ParsingExpression operand)
    {
        return capture($(accessor(name)), operand);
    }

    // ---------------------------------------------------------------------------------------------

    public static Capture captureText(Decorate[] decorations, ParsingExpression operand)
    {
        return new Capture(true, true, operand, decorations);
    }

    // ---------------------------------------------------------------------------------------------

    public static Capture captureText(ParsingExpression operand)
    {
        return captureText(new Decorate[0], operand);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression captureText(String name, ParsingExpression operand)
    {
        return captureText($(accessor(name)), operand);
    }

    // ---------------------------------------------------------------------------------------------

    public static Capture set(Decorate[] decorations, ParsingExpression operand)
    {
        return new Capture(false, false, operand, decorations);
    }

    // ---------------------------------------------------------------------------------------------

    public static Capture marker(Decorate[] decorations)
    {
        return capture(decorations, new Success());
    }

    // ---------------------------------------------------------------------------------------------

    public static Decorate[] $(Decorate... decorations)
    {
        return decorations;
    }

    // ---------------------------------------------------------------------------------------------

    public static Decorate kind(String kind)
    {
        return new DecorateWithKind(kind);
    }

    // ---------------------------------------------------------------------------------------------

    public static Decorate accessor(String accessor)
    {
        return new DecorateWithAccessor(accessor);
    }

    // ---------------------------------------------------------------------------------------------

    public static Decorate group(String group)
    {
        return new DecorateWithGroup(group);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression kind(String name, ParsingExpression operand)
    {
        return set($(kind(name)), operand);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression accessor(String name, ParsingExpression operand)
    {
        return set($(accessor(name)), operand);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression group(String name, ParsingExpression operand)
    {
        return set($(group(name)), operand);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static Any any()
    {
        return new Any();
    }

    // ---------------------------------------------------------------------------------------------

    public static CharRange charRange(char start, char end)
    {
        CharRange result = new CharRange();
        result.start = start;
        result.end = end;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static CharSet charSet(char[] chars)
    {
        CharSet result = new CharSet();
        result.chars = chars;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static CharSet charSet(String chars)
    {
        return charSet(chars.toCharArray());
    }

    // ---------------------------------------------------------------------------------------------

    public static Choice choice(ParsingExpression... operands)
    {
        Choice result = new Choice();
        result.operands = operands;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Dumb dumb(ParsingExpression operand)
    {
        Dumb result = new Dumb();
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression dumb(ParsingExpression... seq)
    {
        return dumb(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static ExpressionCluster cluster(Group... groups)
    {
        ExpressionCluster result = new ExpressionCluster();

        // Sort in decreasing order of precedence.
        Arrays.sort(groups, (g1, g2) -> g2.precedence - g1.precedence);

        result.groups = groups;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static WithMinPrecedence exprDropPrecedence(ParsingExpression operand)
    {
        return exprWithMinPrecedence(0, operand);
    }

    // ---------------------------------------------------------------------------------------------

    public static WithMinPrecedence exprDropPrecedence(ParsingExpression... seq)
    {
        return exprWithMinPrecedence(0, sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static WithMinPrecedence exprWithMinPrecedence(int minPrecedence, ParsingExpression operand)
    {
        WithMinPrecedence result = new WithMinPrecedence();
        result.operand = operand;
        result.minPrecedence = minPrecedence;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static WithMinPrecedence exprWithMinPrecedence(int minPrecedence, ParsingExpression... seq)
    {
        return exprWithMinPrecedence(minPrecedence, sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static Group group(int precedence, boolean leftRecursive, boolean leftAssociative, ParsingExpression... alternates)
    {
        Group group = new Group();
        group.precedence = precedence;
        group.leftRecursive = leftRecursive;
        group.leftAssociative = leftAssociative;
        group.operands = alternates;
        return group;
    }

    // ---------------------------------------------------------------------------------------------

    public static Group group(int precedence, ParsingExpression... alternates)
    {
        return group(precedence, false, false, alternates);
    }

    // ---------------------------------------------------------------------------------------------

    public static Group groupLeftRec(int precedence, ParsingExpression... alternates)
    {
        return group(precedence, true, false, alternates);
    }

    // ---------------------------------------------------------------------------------------------

    public static Group groupLeftAssoc(int precedence, ParsingExpression... alternates)
    {
        return group(precedence, true, true, alternates);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression filter(
        ParsingExpression pe,
        String[] allowed,
        String[] forbidden)
    {
        if ((allowed == null   || allowed.length == 0)
        &&  (forbidden == null || forbidden.length == 0))
        {
            return pe;
        }

        return new Filter(
            pe,
            allowed != null ? allowed : EMPTY_STRINGS,
            forbidden != null ? forbidden : EMPTY_STRINGS);
    }

    private static final String[] EMPTY_STRINGS = new String[0];

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression allow(ParsingExpression pe, String... allowed)
    {
        return filter(pe, allowed, null);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression forbid(ParsingExpression pe, String... forbidden)
    {
        return filter(pe, null, forbidden);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Use to create the allowed and forbidden parameters to {@link #filter}.
     */
    public static ParsingExpression[] $(ParsingExpression... exprs)
    {
        return exprs;
    }

    // ---------------------------------------------------------------------------------------------

    public static Literal literal(String string)
    {
        Literal result = new Literal();
        result.string = string;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static LeftRecursive leftAssociative(ParsingExpression operand)
    {
        LeftRecursive result = new LeftRecursive();
        result.operand = operand;
        result.leftAssociative = true;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static LeftRecursive leftAssociative(ParsingExpression... seq)
    {
        return leftAssociative(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static LeftRecursive leftRecursive(ParsingExpression operand)
    {
        LeftRecursive result = new LeftRecursive();
        result.operand = operand;
        result.name = operand.name;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static LeftRecursive leftRecursive(ParsingExpression... seq)
    {
        return leftRecursive(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static LongestMatch longestMatch(ParsingExpression... operands)
    {
        LongestMatch result = new LongestMatch();
        result.operands = operands;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Lookahead lookahead(ParsingExpression operand)
    {
        Lookahead result = new Lookahead();
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Lookahead lookahead(ParsingExpression... seq)
    {
        return lookahead(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static Memo memo(ParsingExpression operand)
    {
        Memo result = new Memo();
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression memo(ParsingExpression... seq)
    {
        return memo(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static Not not(ParsingExpression operand)
    {
        Not result = new Not();
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Not not(ParsingExpression... seq)
    {
        return not(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static Precedence noPrecedence(ParsingExpression operand)
    {
        Precedence result = new Precedence();
        result.precedence = Precedence.NONE;
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Precedence noPrecedence(ParsingExpression... seq)
    {
        return noPrecedence(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static OneMore oneMore(ParsingExpression operand)
    {
        OneMore result = new OneMore();
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static OneMore oneMore(ParsingExpression... seq)
    {
        return oneMore(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static Optional optional(ParsingExpression operand)
    {
        Optional result = new Optional();
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Optional optional(ParsingExpression... seq)
    {
        return optional(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static Precedence precedence(int precedence, ParsingExpression operand)
    {
        Precedence result = new Precedence();
        result.precedence = precedence;
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Precedence precedence(int precedence, ParsingExpression... operands)
    {
        return precedence(precedence, sequence(operands));
    }

    // ---------------------------------------------------------------------------------------------

    public static Reference reference(String target)
    {
        Reference result = new Reference();
        result.target = target;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Sequence sequence(ParsingExpression... operands)
    {
        Sequence result = new Sequence();
        result.operands = operands;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Token token(ParsingExpression operand)
    {
        Token result = new Token();
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Token token(String string)
    {
        Token result = new Token();
        result.operand = literal(string);
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static Token token(ParsingExpression... seq)
    {
        return token(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static Whitespace whitespace()
    {
        return new Whitespace();
    }

    // ---------------------------------------------------------------------------------------------

    public static ZeroMore zeroMore(ParsingExpression operand)
    {
        ZeroMore result = new ZeroMore();
        result.operand = operand;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static ZeroMore zeroMore(ParsingExpression... seq)
    {
        return zeroMore(sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static Success succeed()
    {
        return new Success();
    }

    // ---------------------------------------------------------------------------------------------

    public static Failure fail()
    {
        return new Failure();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParsingExpression notCharSet(String chars)
    {
        return sequence(not(charSet(chars)), any());
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression until(ParsingExpression op1, ParsingExpression op2)
    {
        return sequence(zeroMore(not(op2), op1), op2);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression aloUntil(ParsingExpression op1, ParsingExpression op2)
    {
        return sequence(oneMore(not(op2), op1), op2);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression separated(ParsingExpression op, ParsingExpression sep)
    {
        return optional(op, zeroMore(sep, op));
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression aloSeparated(ParsingExpression op, ParsingExpression sep)
    {
        return sequence(op, zeroMore(sep, op));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParsingExpression namekind(String string, ParsingExpression pe)
    {
        return named$(string, capture($(kind(string)), pe));
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression namekindText(String string, ParsingExpression pe)
    {
        return named$(string, captureText($(kind(string)), pe));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParsingExpression named$(String name, ParsingExpression pe)
    {
        pe.name = name;
        return pe;
    }

    // ---------------------------------------------------------------------------------------------

    public static Debug debug(String id)
    {
        return new Debug(id);
    }

    // ---------------------------------------------------------------------------------------------

    public static Not fdebug(String id)
    {
        return not(new Debug(id));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
