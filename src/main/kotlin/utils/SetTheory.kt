package utils

/**
 * Provides methods for working with sets. Some methods like [intersectionSize] are faster than alternatives,
 * because this one for example doesn't create the union set, but just works by counting elements.
 *
 * Other methods like [isSubset] provide a naming that expresses the intent clearer than the original name [containsAll]
 * in the Java-API.
 */
object SetTheory {

    /**
     * This method runs faster than creating the set that contains
     * the intersection of [s1] and [s2] and taking the size of it.
     * It works by taking the smaller set of [s1] and [s2],
     * and then counting how many elements are contained in the other set.
     *
     * runtime: O( min( |s1|, |s2| ) )
     *
     * @return This size of the union of [s1] and [s1].
     */
    fun <E> intersectionSize(s1: Set<E>, s2: Set<E>): Int =
        if (s1.size < s2.size) {
            s1.count { it in s2 }
        } else {
            s2.count { it in s1 }
        }

    /**
     * This function works by taking the smaller set of [s1] and [s2],
     * and then only taking the elements that are contained in the other set.
     *
     * runtime: O( min( |s1|, |s2| ) )
     *
     * @return The intersection of [s1] and [s1].
     */
    fun <E> intersection(s1: Set<E>, s2: Set<E>): MutableSet<E> {
        val res = HashSet<E>()

        if (s1.size < s2.size) {
            for (e in s1) {
                if (e in s2) res.add(e)
            }
        } else {
            for (e in s2) {
                if (e in s1) res.add(e)
            }
        }
        return res
    }

    /**
     * This method runs faster than creating the set that contains
     * the union of [s1] and [s2] and taking the size of it.
     * It works by taking the smaller set of [s1] and [s2],
     * and then counting how many elements are not contained in the other set.
     * Then the result plus the size of the other set is returned.
     *
     * runtime: O( min( |s1|, |s2| ) )
     *
     * @return This size of the union of [s1] and [s1].
     */
    fun <E> unionSize(s1: Set<E>, s2: Set<E>): Int =
        if (s1.size < s2.size) {
            s2.size + s1.count { it !in s2 }
        } else {
            s1.size + s2.count { it !in s1 }
        }

    /**
     * @return True iff [s1] and [s2] have no elements in common.
     *
     * This function works by taking the smaller set of [s1] and [s2],
     * and then checking if no element of the smaller set is not contained in the other set.
     */
    fun <E> isDisjoint(s1: Set<E>, s2: Set<E>): Boolean =
        if (s1.size < s2.size) {
            s1.none { it in s2 }
        } else {
            s2.none { it in s1 }
        }

    /**
     * runtime: O( |[s1]| )
     * Is also fail fast if [s1] has more elements than [s2].
     *
     * @return True if all elements of [s1] are also in [s2]
     */
    fun <E> isSubset(s1: Set<E>, s2: Set<E>): Boolean = s1.size <= s2.size && s1.all { it in s2 }
}
