package com.norswap.autumn.test.languages;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParseResult;
import com.norswap.autumn.parsing.ParsingExpression;
import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;
import com.norswap.autumn.parsing.source.Source;
import java.io.IOException;

public final class SnippetTest
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final String
        grammarFile = "src/com/norswap/autumn/test/languages/snippet/Grammar",
        testFile = "src/com/norswap/autumn/test/languages/snippet/Test";

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws IOException
    {
        int i = 0;
        ParsingExpression
        Identifier = token(oneMore(charRange('a', 'z'))),
        E = reference("Expression"),
        Expression = named$("Expression", cluster(
            groupLeftAssoc(++i, choice(
                sequence(E, token("*"), E),
                sequence(E, token("/"), E))),
            groupLeftAssoc(++i, choice(
                sequence(E, token("+"), E),
                sequence(E, token("-"), E))),
            group(++i, choice(
                Identifier,
                token(oneMore(charRange('0', '9'))),
                sequence(token("("), exprDropPrecedence(E), token(")")))))),

        Assignment = sequence(Identifier, token("="), Expression, token(";")),
        Print = sequence(token("print"), Identifier, token(";")),
        Statement = choice(Assignment, Print),
        Root = zeroMore(Statement);

        // Grammar grammar = Grammar.fromRoot(Root).build();
        Grammar grammar = Grammar.fromSource(Source.fromFile(grammarFile).columnStart(1).build())
            //.withExtension(new TracerExtension())
            //.withExtension(new BruteForceTreeExtension())
            .build();

        ParseResult result = Autumn.parseSource(grammar, Source.fromFile(testFile).columnStart(1).build());

        if (!result.matched)
        {
            System.err.println(result.error.message());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
