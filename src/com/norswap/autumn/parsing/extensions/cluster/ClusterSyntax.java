package com.norswap.autumn.parsing.extensions.cluster;

import com.norswap.autumn.parsing.ParsingExpression;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;
import static com.norswap.autumn.parsing.support.MetaGrammar.*;

/**
 * Parsing expressions used to define the syntax of {@link SyntaxExtensionCluster} and
 * {@link SyntaxExtensionFilter}.
 */
public final class ClusterSyntax
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final static ParsingExpression

    clusterArrow =
        namekind("clusterArrow", sequence(
            ntoken("->"),
            optional(lhs),
            // TODO
            capture("expr",
                parsingExpression))),
                //forbid(parsingExpression, "choice")))),

    clusterDirective =
        namekindText("clusterDirective", choice(
            nKeyword("@+"),
            nKeyword("@+_left_assoc"),
            nKeyword("@+_left_recur"))),

    exprCluster =
        namekind("exprCluster", group("entries", oneMore(choice(clusterArrow, clusterDirective)))),

    filter =
        sequence(
            captureText("ref", name),
            semi,
            optional(group("allowed", sequence(
                nKeyword("allow"),
                colon,
                aloSeparated(captureText(name), comma),
                semi))),
            optional(group("forbidden", sequence(
                nKeyword("forbid"),
                colon,
                aloSeparated(captureText(name), comma),
                semi))));

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
