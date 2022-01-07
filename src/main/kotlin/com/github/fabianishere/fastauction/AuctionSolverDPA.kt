/*
 * Copyright (c) 2022 Fabian Mastenbroek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.fabianishere.fastauction

import java.util.*
import kotlin.math.min

/**
 * An approximate dynamic programming solver for the Auction problem, based on [1].
 *
 * [1] Hagerup, T., & Katajainen, J. (Eds.). (2004). Algorithm Theory - SWAT 2004. Lecture Notes in Computer Science.
 *
 * @param epsilon The epsilon of the approximation algorithm.
 */
public class AuctionSolverDPA(private val epsilon: Double) : AuctionSolver {
    override val name: String = "dpa"

    override fun solve(instance: AuctionInstance): Int {
        val n: Int = instance.items
        val k: Int = instance.bidders
        val gamma: Int = instance.budgets.maxOrNull()!!
        val intervalLength = gamma * epsilon / k

        // We represent each tuple as an integer array where the ith item is the benefit
        // of the bidder i, with the last item being the total benefit of the tuple.
        // This approach is faster than modeling the tuples as objects, due to the large amount of
        // copying needed for this algorithm (where objects scatter over memory). Instead, we make
        // use of the clone intrinsic for Java arrays.
        var S: List<IntArray> = listOf(IntArray(n + 1))

        for (j in 0 until k) {
            val Sj = generate(instance, S, j)
            S = purge(instance, Sj, intervalLength)
        }

        return findResult(n, S)
    }

    /**
     * Construct S_j from S_{j-1} by replacing each tuple s with n tuples
     * s_1,...,s_n, where tuple s_i represents the same allocation as s, with item
     * j allocated to bidder i.
     *
     * @param a The [AuctionInstance] to access the budgets and bids.
     * @param S The set of tuples from the previous iteration.
     * @param j The item to allocate to the bidders.
     */
    private fun generate(a: AuctionInstance, S: List<IntArray>, j: Int): MutableList<IntArray> {
        val n = a.bidders
        val d = a.budgets
        val b = a.bids
        val res: MutableList<IntArray> = ArrayList(S.size * n)
        for (s in S) {
            for (i in 0 until n) {
                val sp = s.clone()
                val vi = sp[i]
                val di = d[i]
                val bij = b[i][j]

                // Limit the benefit of bidder i to their budget constraint
                val vip = min(di, vi + bij)

                // Increase the total benefit of the allocation
                sp[n] += vip - vi
                sp[i] = vip
                res.add(sp)
            }
        }
        return res
    }

    /**
     * Purge tuples with all bidders on the same interval and a lower total benefit.
     *
     * @param a The [AuctionInstance] to access the budgets and bids.
     * @param S The set of tuples to purge.
     * @param intervalLength The length of the interval.
     * @return The purged set of tuples.
     */
    private fun purge(a: AuctionInstance, S: MutableList<IntArray>, intervalLength: Double): List<IntArray> {
        val n: Int = a.bidders
        val Ss = S.size

        // Although the algorithm prescribes that for every tuple s and s' in S, we need to perform
        // the check, it is enough to have each tuple visit each other tuple only once, since
        // performing this step multiple times will not change the outcome.
        var u = 0
        while (u < Ss) {
            val s = S[u]
            val tn = s[n]
            val v = u + 1
            while (u < Ss) {
                val sp = S[v]
                val tnp = sp[n]

                // Skip the tuple if it is already marked for removal or if its total benefit is
                // bigger than the current total benefit.
                if (tnp < 0 || tn < tnp) {
                    u++
                    continue
                }
                if (inInterval(n, s, sp, intervalLength)) {
                    // We mark an allocation s' for removal by setting the total benefit of the
                    // allocation s' to a negative value
                    sp[n] = -1
                }
                u++
            }
            u++
        }

        // Finally purge the marked allocations from the set
        S.removeIf { s: IntArray -> s[n] < 0 }
        return S
    }

    /**
     * Determine whether two tuples `s` and `sp` have all of their bidders
     * in the same interval.
     *
     * @param n The number of bidders in the auction.
     * @param s Some tuple representing a partial allocation.
     * @param sp Some tuple representing a partial allocation.
     * @param intervalLength The length of the interval.
     * @return `true` if both tuples have all of their bidders in the same interval,
     * `false` otherwise.
     */
    private fun inInterval(n: Int, s: IntArray, sp: IntArray, intervalLength: Double): Boolean {
        for (i in 0 until n) {
            val v1 = s[i]
            val v2 = sp[i]
            // We check whether two benefits are in the same interval by computing their index in
            // the segment, which is done by an integer division with the interval length.
            if (v1 / intervalLength != v2 / intervalLength) {
                return false
            }
        }
        return true
    }

    /**
     * Find the approximation of the maximal auction revenue.
     *
     * @param n The number of bidders in the auction.
     * @param S The set of partial allocations.
     * @return The maximum auction revenue approximation found.
     */
    private fun findResult(n: Int, S: List<IntArray>): Int {
        var max = 0
        for (s in S) {
            max = s[n].coerceAtLeast(max)
        }
        return max
    }
}