package com.norswap.autumn.parsing.config;

import com.norswap.autumn.parsing.state.CustomStateFactory;
import com.norswap.autumn.parsing.state.errors.DefaultErrorState;
import com.norswap.autumn.parsing.state.errors.ErrorState;
import com.norswap.autumn.parsing.Extension;
import com.norswap.util.Array;

import java.util.function.Supplier;

/**
 * Builder pattern for {@link ParserConfiguration}.
 */
public final class ParserConfigurationBuilder
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Underscore to not conflict with function names in {@link} javadoc tags.

    private Supplier<? extends ErrorState> _errorState;
    private Supplier<? extends MemoHandler> _memoHandler;

    private Array<CustomStateFactory> _customStates;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParserConfigurationBuilder errorState(Supplier<? extends ErrorState> supplier)
    {
        this._errorState = supplier;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public ParserConfigurationBuilder memoStrategy(Supplier<? extends MemoHandler> supplier)
    {
        this._memoHandler = supplier;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Registers an extension with the configuration. The {@link Extension#register} method will
     * be called, allowing the extension to further modify the configuration. In particular, it
     * can register scoped objects and custom states; and should take care to save the returned
     * indices, so that they can be used by the parsing expressions of this extension.
     */
    public ParserConfigurationBuilder extension(Extension extension)
    {
        extension.register(this);
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Registers a custom state with the configuration. Returns the custom state index.
     * This method should only be called from {@link Extension#register}.
     */
    public int customState(CustomStateFactory factory)
    {
        if (_customStates == null) _customStates = new Array<>();
        _customStates.add(factory);
        return _customStates.size() - 1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParserConfiguration build()
    {
        return new ParserConfiguration()
        {
            @Override
            public ErrorState errorState()
            {
                return _errorState != null
                    ? _errorState.get()
                    : new DefaultErrorState();
            }

            @Override
            public MemoHandler memoHandler()
            {
                return _memoHandler != null
                    ? _memoHandler.get()
                    : new DefaultMemoHandler();
            }

            @Override
            public Array<CustomStateFactory> customStateFactories()
            {
                return _customStates == null
                    ? new Array<>()
                    : _customStates;
            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
