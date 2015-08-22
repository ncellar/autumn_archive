package com.norswap.util;

public final class StringEscape
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a copy of string in which the escapable java characters have been replaced by their
     * escaped representation (\t, \b, \n, \r, \f, \', \" and \\).
     *
     * This does not insert octal or hexadecimal escapes for other non-printable characters.
     */
    public static String escape(String string)
    {
        StringBuilder str = new StringBuilder(string.length());

        for (char c: string.toCharArray())
        {
            switch (c)
            {
                case '\t':
                    str.append("\\t");
                    break;

                case '\b':
                    str.append("\\b");
                    break;

                case '\n':
                    str.append("\\n");
                    break;

                case '\r':
                    str.append("\\r");
                    break;

                case '\f':
                    str.append("\\f");
                    break;

                case '\'':
                    str.append("\\\'");
                    break;

                case '\"':
                    str.append("\\\"");
                    break;

                case '\\':
                    str.append("\\\\");
                    break;

                default:
                    str.append(c);
            }
        }

        return str.toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a copy of string in which the escape sequences have been replaced by the
     * characters they escape.
     *
     * It replaces all valid escape sequences: \t, \b, \n, \r, \f, \', \", \\; as well as the
     * octal (\0x, \0xx, \0xxx) and unicode (\_uxxx without the _) sequences. For unicode
     * sequences, variants sporting multiple 'u's are not supported.
     */
    public static String unescape(String string)
    {
        StringBuilder str = new StringBuilder(string.length());

        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; ++i)
        {

            if (chars[i] == '\\')
            {
                ++i;

                switch (chars[i])
                {
                    case 't':
                        str.append("\t");
                        break;

                    case 'b':
                        str.append("\b");
                        break;

                    case 'n':
                        str.append("\n");
                        break;

                    case 'r':
                        str.append("\r");
                        break;

                    case 'f':
                        str.append("\f");
                        break;

                    case '\'':
                        str.append("\'");
                        break;

                    case '"':
                        str.append("\"");
                        break;

                    case '\\':
                        str.append("\\");
                        break;

                    case '0':
                    {
                        int[] index = new int[]{ i + 1 };
                        char c = extractOctal(chars, index);

                        if (index[0] > i + 1)
                        {
                            i = index[0] - 1;
                            str.append(c);
                        }
                        else
                        {
                            str.append("\\0");
                        }

                        break;
                    }

                    case 'u':
                    {
                        int[] index = new int[]{i + 1};
                        char c = extractHexa(chars, index);

                        if (index[0] > i + 1)
                        {
                            i = index[0] - 1;
                            str.append(c);
                        }
                        else
                        {
                            str.append("\\u");
                        }

                        break;
                    }

                    default:
                        str.append('\\');
                        str.append(chars[i]);
                }
            }
            else
            {
                str.append(chars[i]);
            }
        }

        return str.toString();
    }

    // ---------------------------------------------------------------------------------------------

    private static char extractOctal(char[] chars, int[] index)
    {
        int initial = index[0];
        int i = initial;

        while (i < chars.length && '0' <= chars[i] && chars[i] <= '7')
        {
            ++i;
        }

        index[0] = i;

        char result = 0;

        for (int j = initial; j < i; ++j)
        {
            result *= 8;
            result += chars[j] - '0';

            if (result > 255)
            {
                index[0] = initial;
                return 0;
            }
        }

        return result;
    }

    // ---------------------------------------------------------------------------------------------

    private static char extractHexa(char[] chars, int[] index)
    {
        int initial = index[0];
        int i = initial;

        while (i < chars.length &&
            (  '0' <= chars[i] && chars[i] <= '9'
            || 'a' <= chars[i] && chars[i] <= 'f'
            || 'A' <= chars[i] && chars[i] <= 'F'))
        {
            ++i;
        }

        if (i - initial > 4)
        {
            index[0] = initial;
            return 0;
        }

        index[0] = i;

        char result = 0;

        for (int j = initial; j < i; ++j)
        {
            result *= 16;
            result += Character.getNumericValue(Character.toUpperCase(chars[j]));
        }


        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Escapes html entities.
     * Source: http://stackoverflow.com/a/25228492/298664
     */
    public static String escapeHTML(String s)
    {
        StringBuilder out = new StringBuilder(Math.max(16, s.length()));

        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);

            if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&')
            {
                out.append("&#");
                out.append((int) c);
                out.append(';');
            }
            else
            {
                out.append(c);
            }
        }
        return out.toString();
    }
}
