package com.example.mobg_54018.repository.exception

/* `RepositoryException` is a class that extends `Exception` and has three constructors */
class RepositoryException : Exception {
    /**
     * Creates a new instance of `RepositoryException` without detail
     * message.
     */
    constructor() : super() {}

    /**
     * Constructs an instance of `RepositoryException` with the specified
     * detail message.
     *
     * @param msg message of the exception.
     */
    constructor(msg: String?) : super(msg) {}

    /**
     * Constructs an instance of `RepositoryException` and wrapped the
     * source exception.
     *
     * @param exception wrapped exception.
     */
    constructor(exception: Exception?) : super(exception) {}
}
