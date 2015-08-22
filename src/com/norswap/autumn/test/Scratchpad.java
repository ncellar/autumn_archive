package com.norswap.autumn.test;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * A space to try stuff.
 */
public final class Scratchpad
{
    @Target({ANNOTATION_TYPE , CONSTRUCTOR , FIELD , LOCAL_VARIABLE , METHOD , PACKAGE , PARAMETER , TYPE , TYPE_PARAMETER, TYPE_USE})
    @interface A {}
}