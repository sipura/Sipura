object Utils {

    fun <E> intersectionSize(s1: Set<E>, s2: Set<E>): Int =
        if (s1.size < s2.size)
            s1.count { it in s2 }
        else
            s2.count { it in s1 }

    fun <E> isDisjoint(s1: Set<E>, s2: Set<E>): Boolean =
        if (s1.size < s2.size)
            s1.none { it in s2 }
        else
            s2.none { it in s1 }

}