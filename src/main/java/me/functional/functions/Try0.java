package me.functional.functions;

import java.util.function.Supplier;

public interface Try0<T, E extends Exception> {
    T get() throws E;
}

