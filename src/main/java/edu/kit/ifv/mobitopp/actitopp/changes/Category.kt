package edu.kit.ifv.mobitopp.actitopp.changes


/**
 * For Some reason there are a lot of casts for category from Int to String and back. Until I figure
 * out the very exact reason what category should be I will wrap it around integer, which appears
 * to be the nubmer in the _KAT_{X}.csv files.
 */
@JvmInline
value class Category(val category: Int) {
}