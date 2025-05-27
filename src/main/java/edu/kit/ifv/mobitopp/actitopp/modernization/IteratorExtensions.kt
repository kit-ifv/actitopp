package edu.kit.ifv.mobitopp.actitopp.modernization

fun <T, R> Iterator<T>.foldUntil(
    predicate: (T) -> Boolean,
    initial: R,
    operation: (acc: R, T) -> R
): Pair<T?, R> {
    var acc = initial
    while(hasNext()) {
        val current = next()
        if(predicate(current)) {
            return  current to acc
        }
        acc = operation(acc, current)
    }
    return null to acc
}

fun <T, R> Sequence<T>.foldUntil(
    predicate: (T) -> Boolean,
    initial: R,
    operation: (acc: R, T) -> R
): Pair<
        T?, R> = iterator().foldUntil(predicate, initial, operation)