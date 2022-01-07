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

import kotlin.math.max
import kotlin.math.min

/**
 * A fast and exact solver for the Auction problem by representing allocations using bitmasks.
 */
public class AuctionSolverExact : AuctionSolver {
    override val name: String = "exact"

    override fun solve(instance: AuctionInstance): Int {
        val items = instance.items
        val budgets = instance.budgets
        val bids = instance.bids

        // Note: we pre-allocate to contain the revenues (S, Sp) and the result of merging (Sr).
        // It is cleaner to have `generate` and `merge` instantiate this array themselves, but this
        // has a significant effect on performance.
        var S = IntArray(1 shl items)
        val Sp = IntArray(1 shl items)
        var Sr = IntArray(1 shl items)

        generate(S, budgets[0], bids[0])

        for (i in 1 until instance.bidders) {
            generate(Sp, budgets[i], bids[i])
            merge(Sr, S, Sp)

            // Swap `S` and `Sr`: we need to re-use the merging results in the next round.
            val St = S
            S = Sr
            Sr = St
        }

        return S[S.size - 1]
    }

    /**
     * Compute the maximal revenue from some bidder for each possible allocation of `k`
     * items in the auction (the power set of items).
     *
     * We represent an allocation of items as a bit string `u` where the ith position
     * in the bit string represents the allocation of the ith item in the auction.
     *
     * @param S An array of size `2^k` to which the results are written.
     * @param budget The budget constraint set by the bidder.
     * @param bids The bids of the bidder.
     */
    private fun generate(S: IntArray, budget: Int, bids: IntArray) {
        // Note: since allocations are represented as bit strings, we can quickly enumerate
        // the power set of allocations by incrementing the bit string by one.
        for (u in 1 until S.size) {
            S[u] = revenue(budget, bids, u)
        }
    }

    /**
     * Compute the maximal revenue from some bidder for the given allocation `u`.
     *
     * @param budget The budget constraint set by the bidder.
     * @param bids The bids of the bidder.
     * @param alloc The allocation for which to compute the maximal revenue represented as bit string.
     * @return The maximal revenue when allocating the items to this bidder.
     */
    private fun revenue(budget: Int, bids: IntArray, alloc: Int): Int {
        var r = 0

        // Find the positions of the set bits and sum the bids of these positions.
        var j = nextSetBit(alloc, 0)
        while (j >= 0) {
            r += bids[j]
            j = nextSetBit(alloc, j + 1)
        }

        return min(r, budget)
    }

    /**
     * Merge two sets of allocations while maximizing the total revenue of each allocation.
     *
     * @param Sr An array of size `2^` to which the revenues are written.
     * @param S The initial set of allocations.
     * @param Sp The set of allocations to merge with.
     */
    private fun merge(Sr: IntArray, S: IntArray, Sp: IntArray) {
        // Our merging strategy works as follows:
        // 1. Enumerate each possible allocation `u` in the first set
        // 2. Determine the optimal assignment of this allocation by calculating the revenue
        //    of each possible assignment to either `S` or `Sp`.
        for (u in 1 until S.size) {
            // Note: we use bit manipulations to obtain the permutations of toggled bits in `u`.
            // Suppose we have the allocation 0101. We want to obtain every possible
            // assignment of this allocation with `S` and `Sp`. That is:
            // 1. 0101 (S), 0000 (Sp)
            // 2. 0100 (S), 0001 (Sp)
            // 3. 0001 (S), 0100 (Sp)
            // 4. 0000 (S), 0101 (Sp)
            var r = max(S[u], Sp[u])

            // Initial case: all items are assigned to `S`
            var m = (u - 1) and u
            while (m != 0) {
                r = max(r, S[m] + Sp[m.inv() and u])
                m = (m - 1) and u
            }
            Sr[u] = r
        }
    }

    /**
     * Determine the next set bit in the bit string `s` from position `i`.
     *
     * @param s The bit string to find the next set bit in.
     * @param i The position in the bit string to start searching from.
     * @return The position in the bit string where the next bit is set or `-1` if no
     * remaining bits are set.
     */
    private fun nextSetBit(s: Int, i: Int): Int {
        val word = s and (WORD_MASK shl i)
        return if (word != 0) Integer.numberOfTrailingZeros(word) else -1
    }

    private companion object {
        private const val WORD_MASK = -0x1
    }
}